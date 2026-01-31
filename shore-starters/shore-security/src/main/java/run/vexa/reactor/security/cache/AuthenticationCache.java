package run.vexa.reactor.security.cache;


import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.List;

/**
 * authentication cache
 *
 * @param <T> token information
 * @author rained
 */
@NullMarked
public interface AuthenticationCache<T> {

    /**
     * cache token
     *
     * @param key      token
     * @param username username
     * @param expire   expire time in seconds
     * @return success or not
     */
    Mono<Boolean> token(@Nullable String key, @Nullable String username, long expire);

    /**
     * get token list
     *
     * @param key key
     * @return token list
     */
    Mono<List<String>> getTokenList(@Nullable String key);

    /**
     * cache token list
     *
     * @param key    key
     * @param tokens token list
     * @param expire expire time in seconds
     * @return success or not
     */
    Mono<Boolean> tokenList(@Nullable String key, @Nullable List<String> tokens, long expire);

    /**
     * get expire time
     *
     * @param key key
     * @return expire time
     */
    Mono<Duration> getExpire(@Nullable String key);

    /**
     * cache authentication information
     *
     * @param key         token
     * @param currentUser token information
     * @param expire      expire time in seconds
     * @return success or not
     */
    Mono<Boolean> user(@Nullable String key, T currentUser, long expire);

    /**
     * get token information
     *
     * @param key token
     * @return token information
     */
    Mono<String> token(@Nullable String key);

    /**
     * get current user
     *
     * @param key token
     * @return current user
     */
    Mono<T> user(@Nullable String key);

    /**
     * refresh token
     *
     * @param key      token
     * @param username username
     * @param expire   expire time in seconds
     * @return success or not
     */
    Mono<Boolean> refreshToken(@Nullable String key, @Nullable String username, long expire);

    /**
     * set the value of the key.
     *
     * @param key the key
     * @return a Mono of Boolean
     */
    Mono<Long> delete(@Nullable String key);

    /**
     * Set the value of the key.
     *
     * @param tokenKey The token key
     * @param expire    The expiry time in seconds
     * @return a Mono of Boolean
     */
    Mono<Boolean> renew(@Nullable String tokenKey, long expire);

    /**
     * Renew the token
     *
     * @param tokenKey  The token key
     * @param expire    The expiry time in seconds
     * @param renewTime The renewal time in seconds
     */
    default void renew(String tokenKey, long expire, long renewTime) {
        var renewExpire = expire - renewTime > 0 ? renewTime : expire;
        Schedulers.boundedElastic().schedule(() -> this.renew(tokenKey, renewExpire).subscribe());
    }

}


