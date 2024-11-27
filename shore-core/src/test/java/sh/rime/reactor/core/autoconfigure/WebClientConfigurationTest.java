package sh.rime.reactor.core.autoconfigure;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.logging.AdvancedByteBufFormat;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * WebClientConfiguration unit test.
 *
 * @author rained
 **/
class WebClientConfigurationTest {

    private WebClientConfiguration webClientConfiguration;
    private WebClientConfiguration.ReactorClientConfiguration reactorClientConfiguration;

    @BeforeEach
    void setUp() {
        webClientConfiguration = new WebClientConfiguration();
        reactorClientConfiguration = new WebClientConfiguration.ReactorClientConfiguration();
    }

    @Test
    void testWebClient() {
        WebClient.Builder builder = mock(WebClient.Builder.class);
        when(builder.build()).thenReturn(mock(WebClient.class));

        WebClient webClient = webClientConfiguration.webClient(builder);

        assertNotNull(webClient, "WebClient should not be null");
        verify(builder).build();
    }

    @Test
    void testReactorResourceFactory() {
        ReactorResourceFactory factory = reactorClientConfiguration.reactorResourceFactory();

        assertNotNull(factory, "ReactorResourceFactory should not be null");
        assertNotNull(factory.getConnectionProvider(), "Connection provider should be set");
    }

    @Test
    void testShoreWebClientCustomizer() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        WebClientCustomizer customizer = reactorClientConfiguration.shoreWebClientCustomizer(factory);

        WebClient.Builder builder = WebClient.builder();
        customizer.customize(builder);
        WebClient webClient = builder.build();

        assertNotNull(webClient, "WebClient should not be null");
    }

    @Test
    void testWebClientWithInvalidConfiguration() {
        WebClient.Builder invalidBuilder = mock(WebClient.Builder.class);
        when(invalidBuilder.build()).thenThrow(new RuntimeException("Invalid configuration"));

        assertThrows(RuntimeException.class, () -> webClientConfiguration.webClient(invalidBuilder),
                "Should throw an exception due to invalid configuration");
    }


    @Test
    void testShoreWebClientCustomizerFunction() {
        ReactorResourceFactory reactorResourceFactory = new ReactorResourceFactory();
        ReactorClientHttpConnector connector = getReactorClientHttpConnector(reactorResourceFactory);
        WebClientCustomizer customizer = webClientBuilder -> webClientBuilder.clientConnector(connector);

        assertNotNull(customizer, "WebClientCustomizer should not be null");

        WebClient.Builder builder = mock(WebClient.Builder.class);
        customizer.customize(builder);

        Mockito.verify(builder).clientConnector(Mockito.any(ReactorClientHttpConnector.class));
    }

    private static ReactorClientHttpConnector getReactorClientHttpConnector(ReactorResourceFactory reactorResourceFactory) {
        Function<HttpClient, HttpClient> function = httpClient ->
                httpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3_000)
                        .wiretap(WebClient.class.getName(), LogLevel.DEBUG,
                                AdvancedByteBufFormat.TEXTUAL, StandardCharsets.UTF_8)
                        .responseTimeout(Duration.ofSeconds(15))
                        .doOnConnected(connection ->
                                connection.addHandlerLast(new ReadTimeoutHandler(20))
                                        .addHandlerLast(new WriteTimeoutHandler(20)));
        return new ReactorClientHttpConnector(reactorResourceFactory, function);
    }

    @Test
    void testWebClientConfigurationConstructor() {
        WebClientConfiguration configuration = new WebClientConfiguration();
        assertNotNull(configuration, "WebClientConfiguration instance should be created");
    }

    @Test
    void testReactorClientConfigurationConstructor() {
        WebClientConfiguration.ReactorClientConfiguration localReactorClientConfiguration = new WebClientConfiguration
                .ReactorClientConfiguration();
        assertNotNull(localReactorClientConfiguration, "ReactorClientConfiguration instance should be created");
    }

}

