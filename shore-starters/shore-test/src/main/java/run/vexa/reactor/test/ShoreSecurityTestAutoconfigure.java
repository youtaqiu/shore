package run.vexa.reactor.test;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import run.vexa.reactor.security.cache.AuthenticationCache;
import run.vexa.reactor.security.cache.CaffeineAuthenticationCache;

/**
 * ShoreSecurityTestAutoconfigure is a class that config.
 *
 * @author rained
 */
@TestConfiguration
public class ShoreSecurityTestAutoconfigure {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public ShoreSecurityTestAutoconfigure() {
    }

    /**
     * Mock CaffeineAuthenticationCache
     *
     * @return CaffeineAuthenticationCache
     */
    @Bean
    @Profile("test")
    public AuthenticationCache<?> caffeineAuthenticationCache() {
        return new CaffeineAuthenticationCache();
    }
}
