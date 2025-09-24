package run.vexa.reactor.limit.aspect;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import run.vexa.reactor.commons.enums.TimeUnitMessageKey;
import run.vexa.reactor.commons.exception.ServerException;
import run.vexa.reactor.core.context.ReactiveContextHolder;
import run.vexa.reactor.limit.annotation.Limit;
import run.vexa.reactor.limit.provider.LimitProvider;

import java.lang.reflect.Method;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
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

    static class DemoService {
        public Mono<String> ok() {
            return Mono.just("ok");
        }

        public String plain() {
            return "plain";
        }

        public Flux<String> stream() {
            return Flux.just("alpha", "beta");
        }
    }

    @Test
    void handlerAllowsAndReturnsResult() throws Throwable {
        when(limitProvider.tryAcquire(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyLong(), any(ChronoUnit.class)))
                .thenReturn(Mono.just(true));

        DemoService svc = new DemoService();
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

        DemoService svc = new DemoService();
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

    @Test
    void handlerReturnsCollectionWhenMethodProducesFlux() throws Throwable {
        when(limitProvider.tryAcquire(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyLong(), any(ChronoUnit.class)))
                .thenReturn(Mono.just(true));

        DemoService svc = new DemoService();
        Method method = DemoService.class.getMethod("stream");
        org.aspectj.lang.ProceedingJoinPoint pjp = Mockito.mock(org.aspectj.lang.ProceedingJoinPoint.class);
        org.aspectj.lang.reflect.MethodSignature sig = Mockito.mock(org.aspectj.lang.reflect.MethodSignature.class);
        when(sig.getMethod()).thenReturn(method);
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.getArgs()).thenReturn(new Object[]{});
        when(pjp.proceed()).thenReturn(svc.stream());

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/flux").build());
        Mono<?> result = ((Mono<?>) aspect.handler(pjp, limit()))
                .contextWrite(ctx -> ctx.put(ReactiveContextHolder.CONTEXT_KEY, exchange));

        StepVerifier.create(result.cast(List.class))
                .expectNext(List.of("alpha", "beta"))
                .verifyComplete();
    }

    @Test
    void handlerWrapsPlainReturnValueInMono() throws Throwable {
        when(limitProvider.tryAcquire(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyLong(), any(ChronoUnit.class)))
                .thenReturn(Mono.just(true));

        DemoService svc = new DemoService();
        Method method = DemoService.class.getMethod("plain");
        org.aspectj.lang.ProceedingJoinPoint pjp = Mockito.mock(org.aspectj.lang.ProceedingJoinPoint.class);
        org.aspectj.lang.reflect.MethodSignature sig = Mockito.mock(org.aspectj.lang.reflect.MethodSignature.class);
        when(sig.getMethod()).thenReturn(method);
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.getArgs()).thenReturn(new Object[]{});
        when(pjp.proceed()).thenReturn(svc.plain());

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/plain").build());

        Mono<?> result = ((Mono<?>) aspect.handler(pjp, limit()))
                .contextWrite(ctx -> ctx.put(ReactiveContextHolder.CONTEXT_KEY, exchange));

        StepVerifier.create(result.cast(String.class))
                .expectNext("plain")
                .verifyComplete();
    }

    @Test
    void handlerPropagatesErrorsFromProceedingJoinPoint() throws Throwable {
        when(limitProvider.tryAcquire(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyInt(), org.mockito.ArgumentMatchers.anyLong(), any(ChronoUnit.class)))
                .thenReturn(Mono.just(true));

        Method method = DemoService.class.getMethod("ok");
        org.aspectj.lang.ProceedingJoinPoint pjp = Mockito.mock(org.aspectj.lang.ProceedingJoinPoint.class);
        org.aspectj.lang.reflect.MethodSignature sig = Mockito.mock(org.aspectj.lang.reflect.MethodSignature.class);
        when(sig.getMethod()).thenReturn(method);
        when(pjp.getSignature()).thenReturn(sig);
        when(pjp.getArgs()).thenReturn(new Object[]{});
        when(pjp.proceed()).thenThrow(new IllegalStateException("boom"));

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/error").build());

        Mono<?> result = ((Mono<?>) aspect.handler(pjp, limit()))
                .contextWrite(ctx -> ctx.put(ReactiveContextHolder.CONTEXT_KEY, exchange));

        StepVerifier.create(result)
                .expectErrorMatches(throwable -> throwable instanceof IllegalStateException && "boom".equals(throwable.getMessage()))
                .verify();
    }

    @Test
    void handlerReturnsDirectResultWhenSignatureIsNotMethod() throws Throwable {
        org.aspectj.lang.ProceedingJoinPoint pjp = Mockito.mock(org.aspectj.lang.ProceedingJoinPoint.class);
        org.aspectj.lang.Signature signature = Mockito.mock(org.aspectj.lang.Signature.class);
        when(pjp.getSignature()).thenReturn(signature);
        when(pjp.proceed()).thenReturn("bypassed");

        Object result = aspect.handler(pjp, limit());

        verifyNoInteractions(limitProvider);
        org.junit.jupiter.api.Assertions.assertEquals("bypassed", result);
    }

    @Test
    void getTimeUnitNameReturnsConfiguredValue() {
        org.junit.jupiter.api.Assertions.assertEquals("seconds", aspect.getTimeUnitName(ChronoUnit.SECONDS));
    }

    @Test
    void getTimeUnitNameFallsBackToEmptyOnMissingMessage() {
        try (MockedStatic<TimeUnitMessageKey> mocked = Mockito.mockStatic(TimeUnitMessageKey.class)) {
            mocked.when(() -> TimeUnitMessageKey.getKey(ChronoUnit.MINUTES))
                    .thenThrow(new NoSuchMessageException("minutes"));

            org.junit.jupiter.api.Assertions.assertEquals("", aspect.getTimeUnitName(ChronoUnit.MINUTES));
        }
    }
}
