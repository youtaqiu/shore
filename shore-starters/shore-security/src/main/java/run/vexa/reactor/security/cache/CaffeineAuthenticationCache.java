package run.vexa.reactor.security.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import run.vexa.reactor.security.domain.CurrentUser;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * authentication cache
 *
 * @author rained
 */
@NullMarked
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
    public Mono<Boolean> token(@Nullable String key, @Nullable String username, long expire) {
        if (key == null || username == null) {
            return Mono.just(false);
        }
        return Mono.fromRunnable(() -> tokenCache.put(key, username))
                .thenReturn(true);
    }

    @Override
    public Mono<List<String>> getTokenList(@Nullable String key) {
        if (key == null) {
            return Mono.just(Collections.emptyList());
        }
        List<String> tokens = tokenListCache.getIfPresent(key);
        return tokens != null ? Mono.just(tokens) : Mono.just(Collections.emptyList());
    }

    @Override
    public Mono<Boolean> tokenList(@Nullable String key, @Nullable List<String> tokens, long expire) {
        if (key == null) {
            return Mono.just(false);
        }
        return Mono.fromRunnable(() -> {
                    if (!CollectionUtils.isEmpty(tokens)) {
                        tokenListCache.put(key, tokens);
                    }
                })
                .thenReturn(true);
    }

    @Override
    public Mono<Duration> getExpire(@Nullable String key) {
        return Mono.just(Duration.ofSeconds(3600));
    }

    @Override
    public Mono<Boolean> user(@Nullable String key, CurrentUser currentUser, long expire) {
        if (key == null) {
            return Mono.just(false);
        }
        return Mono.fromRunnable(() -> userCache.put(key, currentUser))
                .thenReturn(true);
    }

    @Override
    public Mono<String> token(@Nullable String key) {
        if (key == null) {
            return Mono.empty();
        }
        return Mono.justOrEmpty(tokenCache.getIfPresent(key));
    }

    @Override
    public Mono<CurrentUser> user(@Nullable String key) {
        if (key == null) {
            return Mono.empty();
        }
        return Mono.justOrEmpty(userCache.getIfPresent(key));
    }

    @Override
    public Mono<Boolean> refreshToken(@Nullable String key, @Nullable String username, long expire) {
        // Delegate to token() since they have the same implementation
        return token(key, username, expire);
    }

    @Override
    public Mono<Long> delete(@Nullable String key) {
        if (key == null) {
            return Mono.just(0L);
        }
        return Mono.fromRunnable(() -> {
            userCache.invalidate(key);
            tokenCache.invalidate(key);
            tokenListCache.invalidate(key);
        }).thenReturn(1L);
    }

    @Override
    public Mono<Boolean> renew(@Nullable String tokenKey, long expire) {
        return Mono.just(true);
    }
}

