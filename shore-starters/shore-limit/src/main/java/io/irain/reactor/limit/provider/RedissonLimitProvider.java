package io.irain.reactor.limit.provider;

import cn.hutool.core.util.StrUtil;
import io.irain.reactor.commons.exception.ServerException;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RRateLimiterReactive;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonReactiveClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author youta
 **/
@RequiredArgsConstructor
public class RedissonLimitProvider implements LimitProvider {

    private final RedissonReactiveClient redissonReactiveClient;

    @Override
    public Mono<Boolean> tryAcquire(String key, int rate, long expire, TimeUnit unit) {
        if (StrUtil.isEmpty(key)) {
            throw new ServerException("Limit key is null or empty");
        }
        RateIntervalUnit intervalUnit = RateIntervalUnit.valueOf(unit.name());
        RRateLimiterReactive rateLimiter = this.redissonReactiveClient.getRateLimiter(key);
        return rateLimiter
                .trySetRate(RateType.OVERALL, rate, expire, intervalUnit)
                .switchIfEmpty(Mono.error(new ServerException("Unsupported TimeUnit")))
                .flatMap(x -> rateLimiter.tryAcquire(1))
                .flatMap(acquired -> rateLimiter.expire(Duration.ofMillis(unit.toMillis(expire))).thenReturn(acquired));
    }
}
