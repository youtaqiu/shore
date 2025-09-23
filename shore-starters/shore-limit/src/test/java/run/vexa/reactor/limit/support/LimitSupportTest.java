package run.vexa.reactor.limit.support;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import run.vexa.reactor.core.context.ReactiveContextHolder;
import run.vexa.reactor.limit.annotation.Limit;
import run.vexa.reactor.limit.provider.LimitProvider;
import run.vexa.reactor.security.domain.CurrentUser;
import run.vexa.reactor.security.domain.TokenAuthentication;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class LimitSupportTest {

    private LimitProvider limitProvider;
    private LimitSupport limitSupport;
    private Method sampleMethod;

    @BeforeEach
    void setUp() throws Exception {
        this.limitProvider = Mockito.mock(LimitProvider.class);
        this.limitSupport = new LimitSupport(limitProvider);
        this.sampleMethod = SampleClass.class.getDeclaredMethod("sample", String.class);
    }

    static class SampleClass {
        String sample(String id) {
            return id;
        }
    }

    private Limit limit(boolean restrictIp, boolean restrictUser) {
        return new Limit() {
            @Override
            public Class<Limit> annotationType() {
                return Limit.class;
            }

            @Override
            public String key() {
                return "#p0";
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
                return restrictIp;
            }

            @Override
            public boolean restrictUser() {
                return restrictUser;
            }
        };
    }

    private Mono<Boolean> execWithContext(Limit limit, ServerWebExchange exchange, Object... args) {
        return limitSupport.exec(limit, sampleMethod, args)
                .contextWrite(ctx -> ctx.put(ReactiveContextHolder.CONTEXT_KEY, exchange));
    }

    @Test
    void execUsesSpelArgWithoutIpOrUser() {
        when(limitProvider.tryAcquire(any(), eq(1), eq(1L), eq(ChronoUnit.SECONDS)))
                .thenReturn(Mono.just(true));

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        StepVerifier.create(execWithContext(limit(false, false), exchange, "k1"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void execAppendsIpWhenRestrictIpTrue() {
        when(limitProvider.tryAcquire(any(), eq(1), eq(1L), eq(ChronoUnit.SECONDS)))
                .thenReturn(Mono.just(true));

        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .remoteAddress(new InetSocketAddress("1.2.3.4", 0))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        StepVerifier.create(execWithContext(limit(true, false), exchange, "k2"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void execAppendsUserWhenRestrictUserTrue() {
        when(limitProvider.tryAcquire(any(), eq(1), eq(1L), eq(ChronoUnit.SECONDS)))
                .thenReturn(Mono.just(true));

        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());

        CurrentUser currentUser = CurrentUser.builder().userId("u1").accessToken("t").build();
        TokenAuthentication authentication = new TokenAuthentication(currentUser, null);
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        Mono<Boolean> result = limitSupport.exec(limit(false, true), sampleMethod, new Object[]{"k3"})
                .contextWrite(ctx -> ctx.put(ReactiveContextHolder.CONTEXT_KEY, exchange))
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void execAppendsIpAndUserWhenBothRestricted() {
        when(limitProvider.tryAcquire(any(), eq(1), eq(1L), eq(ChronoUnit.SECONDS)))
                .thenReturn(Mono.just(true));

        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .remoteAddress(new InetSocketAddress("1.2.3.4", 0))
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        CurrentUser currentUser = CurrentUser.builder().userId("u2").accessToken("t").build();
        TokenAuthentication authentication = new TokenAuthentication(currentUser, null);
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        Mono<Boolean> result = limitSupport.exec(limit(true, true), sampleMethod, new Object[]{"k4"})
                .contextWrite(ctx -> ctx.put(ReactiveContextHolder.CONTEXT_KEY, exchange))
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));

        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
    }
}


