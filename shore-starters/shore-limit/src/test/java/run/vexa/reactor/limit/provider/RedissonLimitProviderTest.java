package run.vexa.reactor.limit.provider;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.redisson.api.RRateLimiterReactive;
import org.redisson.api.RateType;
import org.redisson.api.RedissonReactiveClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import run.vexa.reactor.commons.exception.ServerException;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

class RedissonLimitProviderTest {

    @Test
    void tryAcquireThrowsWhenKeyEmpty() {
        RedissonReactiveClient client = Mockito.mock(RedissonReactiveClient.class);
        RedissonLimitProvider provider = new RedissonLimitProvider(client);
        
        StepVerifier.create(provider.tryAcquire("", 1, 1, ChronoUnit.SECONDS))
                .expectError(ServerException.class)
                .verify();
    }

    @Test
    void tryAcquireSuccessFlow() {
        RedissonReactiveClient client = Mockito.mock(RedissonReactiveClient.class);
        RRateLimiterReactive limiter = Mockito.mock(RRateLimiterReactive.class);
        when(client.getRateLimiter("k")).thenReturn(limiter);
        when(limiter.trySetRate(eq(RateType.OVERALL), eq(1L), any(Duration.class))).thenReturn(Mono.just(true));
        when(limiter.tryAcquire(1)).thenReturn(Mono.just(true));
        when(limiter.expire(any(Duration.class))).thenReturn(Mono.just(true));

        RedissonLimitProvider provider = new RedissonLimitProvider(client);
        StepVerifier.create(provider.tryAcquire("k", 1, 1, ChronoUnit.SECONDS))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void tryAcquireUnsupportedTimeUnit() {
        RedissonReactiveClient client = Mockito.mock(RedissonReactiveClient.class);
        RRateLimiterReactive limiter = Mockito.mock(RRateLimiterReactive.class);
        when(client.getRateLimiter("k")).thenReturn(limiter);
        when(limiter.trySetRate(eq(RateType.OVERALL), eq(1L), any(Duration.class))).thenReturn(Mono.empty());

        RedissonLimitProvider provider = new RedissonLimitProvider(client);
        StepVerifier.create(provider.tryAcquire("k", 1, 1, ChronoUnit.SECONDS))
                .expectError(ServerException.class)
                .verify();
    }
}


