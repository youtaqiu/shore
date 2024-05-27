package io.irain.reactor.security.repository;

import io.irain.reactor.redis.util.ReactiveRedisUtil;
import io.irain.reactor.security.domain.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;


/**
 * @author youta
 **/
@Repository
@RequiredArgsConstructor
public class AuthenticationRepository {

    private final ReactiveRedisUtil reactiveRedisUtil;
    private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;

    /**
     * 缓存认证信息
     *
     * @param key      token
     * @param username 用户名
     * @param expire   过期时间 单位秒
     * @return 是否成功
     */
    public Mono<Boolean> token(String key, String username, long expire) {
        return this.reactiveStringRedisTemplate.opsForValue().set(key, username, Duration.ofSeconds(expire));
    }


    /**
     * 获取令牌集合
     *
     * @param key key
     * @return 令牌集合
     */
    public Mono<List<String>> getTokenList(String key) {
        return this.reactiveRedisUtil.get(key);
    }


    /**
     * 设置令牌集合
     *
     * @param key    key
     * @param tokens 令牌集合
     * @param expire 过期时间 单位秒
     * @return 令牌集合
     */
    public Mono<Boolean> tokenList(String key, List<String> tokens, long expire) {
        return this.reactiveRedisUtil.set(key, tokens, Duration.ofSeconds(expire));
    }

    /**
     * 获取过期时间
     *
     * @param key key
     * @return 过期时间
     */
    public Mono<Duration> getExpire(String key) {
        return this.reactiveStringRedisTemplate.getExpire(key);
    }

    /**
     * 缓存认证信息
     *
     * @param key         token
     * @param currentUser token信息
     * @param expire      过期时间 单位秒
     * @return 是否成功
     */
    public Mono<Boolean> user(String key, CurrentUser currentUser, long expire) {
        return this.reactiveRedisUtil.set(key, currentUser, Duration.ofSeconds(expire));
    }

    /**
     * 令牌信息
     *
     * @param key token
     * @return token信息
     */
    public Mono<String> token(String key) {
        return this.reactiveStringRedisTemplate
                .opsForValue()
                .get(key);
    }

    /**
     * 缓存认证信息
     *
     * @param key token
     * @return token信息
     */
    public Mono<CurrentUser> user(String key) {
        return this.reactiveRedisUtil.get(key);
    }

    /**
     * 缓存认证信息
     *
     * @param key      refresh token
     * @param username 用户名
     * @param expire   过期时间 单位秒
     * @return 是否成功
     */
    public Mono<Boolean> refreshToken(String key, String username, long expire) {
        return this.reactiveRedisUtil.set(key, username, Duration.ofSeconds(expire));
    }


    /**
     * 删除认证信息
     *
     * @param key token
     * @return 删除数量
     */
    public Mono<Long> delete(String key) {
        return this.reactiveRedisUtil.del(key);
    }

    /**
     * 续期
     *
     * @param tokenKey token
     * @param expire   过期时间 单位秒
     * @return 是否成功
     */
    public Mono<Boolean> renew(String tokenKey, long expire) {
        return this.reactiveRedisUtil.getExpire(tokenKey)
                .map(Duration::getSeconds)
                .map(expireTime -> expireTime + expire)
                .flatMap(expireTime -> this.reactiveRedisUtil.expire(tokenKey, Duration.ofSeconds(expireTime)));
    }

    /**
     * 续期
     * @param expire 过期时间 单位秒
     * @param tokenKey token
     * @param renewTime 续期时间 单位秒
     */
    public void renew(long expire, String tokenKey,long renewTime) {
        var renewExpire = expire - renewTime > 0 ? renewTime : expire;
        Schedulers.boundedElastic().schedule(() -> this.renew(tokenKey, renewExpire).subscribe());
    }
}
