package run.vexa.reactor.security.cache;

import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import reactor.core.publisher.Mono;
import run.vexa.reactor.redis.util.ReactiveRedisUtil;

import java.time.Duration;
import java.util.List;

/**
 * Redis authentication cache
 *
 * @param <T> token information
 * @author rained
 */
public class RedisAuthenticationCache<T> implements AuthenticationCache<T> {

    private final ReactiveRedisUtil reactiveRedisUtil;
    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param reactiveRedisUtil           the reactive redis util
     * @param reactiveStringRedisTemplate the reactive string redis template
     */
    public RedisAuthenticationCache(ReactiveRedisUtil reactiveRedisUtil, ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
        this.reactiveRedisUtil = reactiveRedisUtil;
        this.reactiveStringRedisTemplate = reactiveStringRedisTemplate;
    }

    @Override
    public Mono<Boolean> token(String key, String username, long expire) {
        return this.reactiveStringRedisTemplate.opsForValue().set(key, username, Duration.ofSeconds(expire));
    }

    @Override
    public Mono<List<String>> getTokenList(String key) {
        return this.reactiveRedisUtil.get(key);
    }

    @Override
    public Mono<Boolean> tokenList(String key, List<String> tokens, long expire) {
        return this.reactiveRedisUtil.set(key, tokens, Duration.ofSeconds(expire));
    }

    @Override
    public Mono<Duration> getExpire(String key) {
        return this.reactiveStringRedisTemplate.getExpire(key);
    }

    @Override
    public Mono<Boolean> user(String key, T currentUser, long expire) {
        return this.reactiveRedisUtil.set(key, currentUser, Duration.ofSeconds(expire));
    }

    @Override
    public Mono<String> token(String key) {
        return this.reactiveStringRedisTemplate.opsForValue().get(key);
    }

    @Override
    public Mono<T> user(String key) {
        return this.reactiveRedisUtil.get(key);
    }

    @Override
    public Mono<Boolean> refreshToken(String key, String username, long expire) {
        return this.reactiveRedisUtil.set(key, username, Duration.ofSeconds(expire));
    }

    @Override
    public Mono<Long> delete(String key) {
        return this.reactiveRedisUtil.del(key);
    }

    @Override
    public Mono<Boolean> renew(String tokenKey, long expire) {
        return this.reactiveRedisUtil.getExpire(tokenKey)
                .map(Duration::getSeconds)
                .map(expireTime -> expireTime + expire)
                .flatMap(expireTime -> this.reactiveRedisUtil.expire(tokenKey, Duration.ofSeconds(expireTime)));
    }
}
