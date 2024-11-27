package sh.rime.reactor.security.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import sh.rime.reactor.security.domain.CurrentUser;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * authentication cache
 *
 * @author rained
 */
public class CaffeineAuthenticationCache implements AuthenticationCache<CurrentUser> {

    private final Cache<String, String> tokenCache;
    private final Cache<String, List<String>> tokenListCache;
    private final Cache<String, CurrentUser> userCache;

    /**
     * Creates a Caffeine-based implementation of AuthenticationCache.
     */
    public CaffeineAuthenticationCache() {
        this.userCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .initialCapacity(100)
                .maximumSize(1000)
                .build();
        this.tokenCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .initialCapacity(100)
                .maximumSize(1000)
                .build();
        this.tokenListCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .initialCapacity(100)
                .maximumSize(1000)
                .build();
    }

    @Override
    public Mono<Boolean> token(String key, String username, long expire) {
        return Mono.fromRunnable(() -> tokenCache.put(key, username))
                .thenReturn(true);
    }

    @Override
    public Mono<List<String>> getTokenList(String key) {
        return Mono.justOrEmpty(tokenListCache.get(key, k -> null));
    }

    @Override
    public Mono<Boolean> tokenList(String key, List<String> tokens, long expire) {
        return Mono.fromRunnable(() -> {
                    if (!CollectionUtils.isEmpty(tokens)) {
                        tokenListCache.put(key, tokens);
                    }
                })
                .thenReturn(true);
    }

    @Override
    public Mono<Duration> getExpire(String key) {
        return Mono.just(Duration.ofSeconds(3600));
    }

    @Override
    public Mono<Boolean> user(String key, CurrentUser currentUser, long expire) {
        return Mono.fromRunnable(() -> userCache.put(key, currentUser))
                .thenReturn(true);
    }

    @Override
    public Mono<String> token(String key) {
        return Mono.justOrEmpty(tokenCache.get(key, k -> null));
    }

    @Override
    public Mono<CurrentUser> user(String key) {
        return Mono.justOrEmpty(userCache.get(key, k -> null));
    }

    @Override
    public Mono<Boolean> refreshToken(String key, String username, long expire) {
        return Mono.fromRunnable(() -> tokenCache.put(key, username))
                .thenReturn(true);
    }

    @Override
    public Mono<Long> delete(String key) {
        return Mono.fromRunnable(() -> {
            userCache.invalidate(key);
            tokenCache.invalidate(key);
            tokenListCache.invalidate(key);
        }).thenReturn(1L);
    }

    @Override
    public Mono<Boolean> renew(String tokenKey, long expire) {
        return Mono.just(true);
    }
}

