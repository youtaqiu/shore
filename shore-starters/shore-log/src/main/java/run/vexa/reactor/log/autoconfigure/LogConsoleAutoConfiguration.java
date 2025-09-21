package run.vexa.reactor.log.autoconfigure;

import run.vexa.reactor.log.handler.SimpleLogHandler;
import run.vexa.reactor.log.properties.LoggingProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 控制台日志自动配置
 *
 * @author youta
 */
@EnableAsync
@ConditionalOnWebApplication
@Configuration
@EnableConfigurationProperties(value = LoggingProperties.class)
@ConditionalOnProperty(value = LoggingProperties.PREFIX + ".console", havingValue = "true", matchIfMissing = true)
public class LogConsoleAutoConfiguration {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public LogConsoleAutoConfiguration() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * 控制台日志处理器
     *
     * @return 控制台日志处理器
     */
    @Bean
    public SimpleLogHandler simpleLogHandler() {
        return new SimpleLogHandler();
    }


}
