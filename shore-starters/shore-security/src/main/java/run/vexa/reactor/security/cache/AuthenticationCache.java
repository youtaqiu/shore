package run.vexa.reactor.security.cache;


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
public interface AuthenticationCache<T> {

    /**
     * cache token
     *
     * @param key      token
     * @param username username
     * @param expire   expire time in seconds
     * @return success or not
     */
    Mono<Boolean> token(String key, String username, long expire);

    /**
     * get token list
     *
     * @param key key
     * @return token list
     */
    Mono<List<String>> getTokenList(String key);

    /**
     * cache token list
     *
     * @param key    key
     * @param tokens token list
     * @param expire expire time in seconds
     * @return success or not
     */
    Mono<Boolean> tokenList(String key, List<String> tokens, long expire);

    /**
     * get expire time
     *
     * @param key key
     * @return expire time
     */
    Mono<Duration> getExpire(String key);

    /**
     * cache authentication information
     *
     * @param key         token
     * @param currentUser token information
     * @param expire      expire time in seconds
     * @return success or not
     */
    Mono<Boolean> user(String key, T currentUser, long expire);

    /**
     * get token information
     *
     * @param key token
     * @return token information
     */
    Mono<String> token(String key);

    /**
     * get current user
     *
     * @param key token
     * @return current user
     */
    Mono<T> user(String key);

    /**
     * refresh token
     *
     * @param key      token
     * @param username username
     * @param expire   expire time in seconds
     * @return success or not
     */
    Mono<Boolean> refreshToken(String key, String username, long expire);

    /**
     * set the value of the key.
     *
     * @param key the key
     * @return a Mono of Boolean
     */
    Mono<Long> delete(String key);

    /**
     * Set the value of the key.
     *
     * @param tokenKey The token key
     * @param expire   The expire time
     * @return a Mono of Boolean
     */
    Mono<Boolean> renew(String tokenKey, long expire);

    /**
     * Renew the token
     *
     * @param expire    The expire time
     * @param tokenKey  The token key
     * @param renewTime The renew time
     */
    default void renew(String tokenKey, long expire, long renewTime) {
        var renewExpire = expire - renewTime > 0 ? renewTime : expire;
        Schedulers.boundedElastic().schedule(() -> this.renew(tokenKey, renewExpire).subscribe());
    }

}


