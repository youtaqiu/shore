package sh.rime.reactor.limit.provider;

import cn.hutool.core.util.StrUtil;
import sh.rime.reactor.commons.exception.ServerException;
import org.redisson.api.RRateLimiterReactive;
import org.redisson.api.RateType;
import org.redisson.api.RedissonReactiveClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 * Redisson限流提供者
 *
 * @author youta
 **/
public class RedissonLimitProvider implements LimitProvider {

    private final RedissonReactiveClient redissonReactiveClient;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param redissonReactiveClient the redisson reactive client
     */
    public RedissonLimitProvider(RedissonReactiveClient redissonReactiveClient) {
        this.redissonReactiveClient = redissonReactiveClient;
    }


    @Override
    public Mono<Boolean> tryAcquire(String key, int rate, long expire, ChronoUnit unit) {
        if (StrUtil.isEmpty(key)) {
            throw new ServerException("Limit key is null or empty");
        }
        RRateLimiterReactive rateLimiter = this.redissonReactiveClient.getRateLimiter(key);
        return rateLimiter
                .trySetRate(RateType.OVERALL, rate, Duration.of(expire, unit))
                .switchIfEmpty(Mono.error(new ServerException("Unsupported TimeUnit")))
                .flatMap(x -> rateLimiter.tryAcquire(1))
                .flatMap(acquired -> rateLimiter.expire(Duration.of(expire, unit)).thenReturn(acquired));
    }
}
