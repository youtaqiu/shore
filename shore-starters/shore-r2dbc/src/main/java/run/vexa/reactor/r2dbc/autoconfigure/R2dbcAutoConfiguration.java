package run.vexa.reactor.r2dbc.autoconfigure;

import run.vexa.reactor.security.context.UserContextHolder;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.StringUtils;

/**
 * R2dbc自动配置
 *
 * @author youta
 **/
@Configuration
@EnableConfigurationProperties(R2dbcProperties.class)
@EnableTransactionManagement
@EnableR2dbcAuditing
public class R2dbcAutoConfiguration {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public R2dbcAutoConfiguration() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * 审计
     *
     * @return currentAuditor
     */
    @Bean
    public ReactiveAuditorAware<String> auditorAware() {
        return () -> UserContextHolder.userIdDefault()
                .filter(StringUtils::hasText)
                .defaultIfEmpty("system");
    }

}
