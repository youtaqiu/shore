package io.irain.reactor.log.autoconfigure;

import io.irain.reactor.log.handler.SimpleLogHandler;
import io.irain.reactor.log.properties.LoggingProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author youta
 */
@EnableAsync
@ConditionalOnWebApplication
@Configuration
@EnableConfigurationProperties(value = LoggingProperties.class)
@ConditionalOnProperty(value = LoggingProperties.PREFIX + ".console", havingValue = "true", matchIfMissing = true)
public class LogConsoleAutoConfiguration {


    /**
     * 控制台日志处理器
     * @return 控制台日志处理器
     */
    @Bean
    public SimpleLogHandler simpleLogHandler() {
        return new SimpleLogHandler();
    }


}
