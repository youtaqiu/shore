package sh.rime.reactor.core.autoconfigure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * WebClientConfiguration unit test.
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
}

