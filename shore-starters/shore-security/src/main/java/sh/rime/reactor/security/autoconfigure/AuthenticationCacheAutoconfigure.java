package sh.rime.reactor.security.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import sh.rime.reactor.core.properties.AuthProperties;
import sh.rime.reactor.redis.util.ReactiveRedisUtil;
import sh.rime.reactor.security.cache.AuthenticationCache;
import sh.rime.reactor.security.cache.CaffeineAuthenticationCache;
import sh.rime.reactor.security.cache.RedisAuthenticationCache;
import sh.rime.reactor.security.domain.CurrentUser;

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
    public AuthenticationCacheAutoconfigure() {
        // This constructor is intentionally empty.
    }


    /**
     * RedisAuthenticationCache
     */
    @Configuration
    @ConditionalOnProperty(name = "shore.security.cache", havingValue = "redis")
    @ConditionalOnClass(ReactiveRedisUtil.class)
    static class RedisCacheConfig {

        /**
         * Default constructor.
         *
         * @param reactiveRedisUtil           reactiveRedisUtil
         * @param reactiveStringRedisTemplate reactiveStringRedisTemplate
         * @return RedisAuthenticationCache
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
