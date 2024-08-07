package sh.rime.reactor.core.autoconfigure;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.DefaultSslContextSpec;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Function;

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
            Function<HttpClient, HttpClient> function = httpClient ->
                    httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3_000)
                            .wiretap(WebClient.class.getName(), LogLevel.DEBUG,
                                    AdvancedByteBufFormat.TEXTUAL, StandardCharsets.UTF_8)
                            .responseTimeout(Duration.ofSeconds(15))
                            .secure(sslContextSpec -> sslContextSpec.sslContext(
                                    DefaultSslContextSpec.forClient().configure(builder ->
                                            builder.trustManager(InsecureTrustManagerFactory.INSTANCE))))
                            .doOnConnected(connection ->
                                    connection.addHandlerLast(new ReadTimeoutHandler(20))
                                            .addHandlerLast(new WriteTimeoutHandler(20)));
            ReactorClientHttpConnector connector = new ReactorClientHttpConnector(reactorResourceFactory, function);
            return webClientBuilder -> webClientBuilder.clientConnector(connector);
        }
    }
}
