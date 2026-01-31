package run.vexa.reactor.security.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import run.vexa.reactor.core.properties.AuthProperties;
import run.vexa.reactor.redis.util.ReactiveRedisUtil;
import run.vexa.reactor.security.cache.AuthenticationCache;
import run.vexa.reactor.security.cache.CaffeineAuthenticationCache;
import run.vexa.reactor.security.cache.RedisAuthenticationCache;
import run.vexa.reactor.security.domain.CurrentUser;

/**
 * AuthenticationCacheAutoconfigure is a class that config
 *
 * @author rained
 * @see AuthenticationCache
 */
@Configuration
@EnableConfigurationProperties(AuthProperties.class)
public class AuthenticationCacheAutoconfigure {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    protected AuthenticationCacheAutoconfigure() {
        // This constructor is intentionally empty.
    }


    /**
     * RedisAuthenticationCache
     */
    @Configuration
    @ConditionalOnProperty(name = "shore.security.cache", havingValue = "redis")
    @ConditionalOnClass(ReactiveRedisUtil.class)
    @Profile("!test")
    static class RedisCacheConfig {

        /**
         * Creates a Redis-based implementation of AuthenticationCache.
         *
         * @param reactiveRedisUtil           utility for reactive Redis operations
         * @param reactiveStringRedisTemplate template for reactive Redis string operations
         * @return an instance of AuthenticationCache using Redis
         */
        @Bean
        @ConditionalOnClass(ReactiveRedisUtil.class)
        public AuthenticationCache<CurrentUser> redisAuthenticationCache(
                ReactiveRedisUtil reactiveRedisUtil,
                ReactiveStringRedisTemplate reactiveStringRedisTemplate) {
            return new RedisAuthenticationCache<>(reactiveRedisUtil, reactiveStringRedisTemplate);
        }
    }

    /**
     * CaffeineAuthenticationCache
     */
    @Configuration
    @ConditionalOnProperty(name = "shore.security.cache", havingValue = "caffeine", matchIfMissing = true)
    static class CaffeineCacheConfig {

        /**
         * Default constructor.
         * This constructor is used for serialization and other reflective operations.
         */
        @Bean
        public AuthenticationCache<CurrentUser> caffeineAuthenticationCache() {
            return new CaffeineAuthenticationCache();
        }
    }

}
