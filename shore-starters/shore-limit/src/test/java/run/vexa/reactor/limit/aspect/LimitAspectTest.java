package run.vexa.reactor.limit.aspect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.MessageSource;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import run.vexa.reactor.commons.exception.ServerException;
import run.vexa.reactor.core.context.ReactiveContextHolder;
import run.vexa.reactor.limit.annotation.Limit;
import run.vexa.reactor.limit.provider.LimitProvider;

import java.lang.reflect.Method;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LimitAspectTest {

    private LimitAspect aspect;
    private LimitProvider limitProvider;

    @BeforeEach
    void setUp() {
        limitProvider = Mockito.mock(LimitProvider.class);
        @SuppressWarnings("unchecked")
        ObjectProvider<LimitProvider> provider = (ObjectProvider<LimitProvider>) Mockito.mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(limitProvider);
        MessageSource messageSource = Mockito.mock(MessageSource.class);
        aspect = new LimitAspect(provider, messageSource);
    }

    private Limit limit() {
        return new Limit() {
            @Override
            public Class<Limit> annotationType() {
                return Limit.class;
            }

            @Override
            public String key() {
                return "'key'";
            }

            @Override
            public long expire() {
                return 1;
            }

            @Override
            public ChronoUnit unit() {
                return ChronoUnit.SECONDS;
            }

            @Override
            public int rate() {
                return 1;
            }

            @Override
            public boolean restrictIp() {
                return false;
            }

            @Override
            public boolean restrictUser() {
                return false;
            }
        };
    }

    interface DemoService { Mono<String> ok(); }

    @Test
    void handlerAllowsAndReturnsResult() throws Throwable {
        when(limitProvider.tryAcquire(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyLong(), any(ChronoUnit.class)))
                .thenReturn(Mono.just(true));

        DemoService svc = () -> Mono.just("ok");
        Method method = DemoService.class.getMethod("ok");
        org.aspectj.lang.ProceedingJoinPoint pjp = Mockito.mock(org.aspectj.lang.ProceedingJoinPoint.class);
        org.aspectj.lang.reflect.MethodSignature sig = Mockito.mock(org.aspectj.lang.reflect.MethodSignature.class);
        when(sig.getMethod()).thenReturn(method);
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.getArgs()).thenReturn(new Object[]{});
        when(pjp.proceed()).thenReturn(svc.ok());

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/t").build());
        Mono<?> result = ((Mono<?>) aspect.handler(pjp, limit()))
                .contextWrite(ctx -> ctx.put(ReactiveContextHolder.CONTEXT_KEY, exchange));

        StepVerifier.create(result.map(Object::toString))
                .expectNext("ok")
                .verifyComplete();
    }

    @Test
    void handlerBlocksAndThrows429() throws Throwable {
        when(limitProvider.tryAcquire(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyLong(), any(ChronoUnit.class)))
                .thenReturn(Mono.just(false));

        DemoService svc = () -> Mono.just("ok");
        Method method = DemoService.class.getMethod("ok");
        org.aspectj.lang.ProceedingJoinPoint pjp = Mockito.mock(org.aspectj.lang.ProceedingJoinPoint.class);
        org.aspectj.lang.reflect.MethodSignature sig = Mockito.mock(org.aspectj.lang.reflect.MethodSignature.class);
        when(sig.getMethod()).thenReturn(method);
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.getArgs()).thenReturn(new Object[]{});
        when(pjp.proceed()).thenReturn(svc.ok());

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/t").build());
        Mono<?> result = ((Mono<?>) aspect.handler(pjp, limit()))
                .contextWrite(ctx -> ctx.put(ReactiveContextHolder.CONTEXT_KEY, exchange));

        StepVerifier.create(result)
                .expectError(ServerException.class)
                .verify();
    }
}


