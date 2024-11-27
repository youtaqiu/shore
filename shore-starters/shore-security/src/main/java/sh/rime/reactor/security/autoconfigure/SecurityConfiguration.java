package sh.rime.reactor.security.autoconfigure;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * SecurityConfiguration is a class that config
 *
 * @author rained
 **/
@Configuration
@Import({WebSecurityAutoconfigure.class, AuthenticationCacheAutoconfigure.class, AuthenticationCacheAutoconfigure.class, PasswordEncoderAutoconfigure.class})
public class SecurityConfiguration {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public SecurityConfiguration() {
    }

}
