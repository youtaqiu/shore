package sh.rime.reactor.test;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import sh.rime.reactor.security.cache.AuthenticationCache;

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
     * Mock AuthenticationRepository
     *
     * @return AuthenticationRepository
     */
    @Bean
    public AuthenticationCache<?> authenticationRepository() {
        return Mockito.mock(AuthenticationCache.class);
    }
}
