package run.vexa.reactor.security.autoconfigure;

import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import run.vexa.reactor.security.domain.*;

/**
 * SecurityConfiguration is a class that config
 *
 * @author rained
 **/
@Configuration
@RegisterReflectionForBinding({LoginRequest.class, CurrentUser.class, TokenAuthentication.class, TokenInfo.class, RoleInfo.class})
@Import({WebSecurityAutoconfigure.class, AuthenticationCacheAutoconfigure.class, PasswordEncoderAutoconfigure.class})
public class SecurityConfiguration {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public SecurityConfiguration() {
    }

}
