package run.vexa.reactor.core.autoconfigure;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.webclient.WebClientCustomizer;
import org.springframework.boot.webclient.autoconfigure.WebClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.time.Duration;
import java.util.function.UnaryOperator;

/**
 * webClient configuration.
 *
 * @author youta
 **/
@AutoConfiguration(after = WebClientAutoConfiguration.class)
@ConditionalOnClass(WebClient.class)
public class WebClientConfiguration {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public WebClientConfiguration() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * webClient 配置
     *
     * @param builder the web client builder
     * @return WebClient web client
     */
    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }


    /**
     * The type Reactor client configuration.
     */
    @Configuration
    @ConditionalOnClass(HttpClient.class)
    public static class ReactorClientConfiguration {

        /**
         * Default constructor.
         * This constructor is used for serialization and other reflective operations.
         */
        public ReactorClientConfiguration() {
            // This constructor is intentionally empty.
            // Further initialization logic can be added here if needed in the future.
        }

        /**
         * Reactor resource factory reactor resource factory.
         *
         * @return the reactor resource factory
         */
        @Bean
        public ReactorResourceFactory reactorResourceFactory() {
            ReactorResourceFactory factory = new ReactorResourceFactory();
            factory.setUseGlobalResources(false);
            return factory;
        }

        /**
         * Reactor client web client customizer web client customizer.
         *
         * @param reactorResourceFactory the reactor resource factory
         * @return the web client customizer
         */
        @Bean
        @ConditionalOnClass(ReactorResourceFactory.class)
        public WebClientCustomizer shoreWebClientCustomizer(ReactorResourceFactory reactorResourceFactory) {
            UnaryOperator<HttpClient> function = httpClient ->
                    httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                            .wiretap(WebClient.class.getName(), LogLevel.DEBUG, AdvancedByteBufFormat.TEXTUAL)
                            .responseTimeout(Duration.ofSeconds(15))
                            .doOnConnected(connection ->
                                    connection.addHandlerLast(new ReadTimeoutHandler(20))
                                            .addHandlerLast(new WriteTimeoutHandler(20)));

            ReactorClientHttpConnector connector = new ReactorClientHttpConnector(reactorResourceFactory, function);
            return webClientBuilder -> webClientBuilder.clientConnector(connector);
        }
    }
}
