package sh.rime.reactor.http.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import sh.rime.reactor.http.core.AutoConfiguredHttpExchangeClientScannerRegistrar;
import sh.rime.reactor.http.core.HttpExchangeClientFactoryBean;

/**
 * Http exchange client scanner registrar configuration.
 *
 * @author rained
 **/
@Configuration
@Import({AutoConfiguredHttpExchangeClientScannerRegistrar.class})
@ConditionalOnMissingBean(HttpExchangeClientFactoryBean.class)
public class HttpExchangeClientScannerRegistrarConfiguration {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public HttpExchangeClientScannerRegistrarConfiguration() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }
}
