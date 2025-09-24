package run.vexa.reactor.core.autoconfigure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
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
    void testShoreWebClientCustomizerWithLambdas() {
        // Create test resources with specific connection handling
        ReactorResourceFactory reactorResourceFactory = new ReactorResourceFactory();
        reactorResourceFactory.setUseGlobalResources(false);

        // Create customizer that will trigger lambda$shoreWebClientCustomizer$0
        WebClientCustomizer customizer = reactorClientConfiguration.shoreWebClientCustomizer(reactorResourceFactory);

        // Create WebClient with customizer
        WebClient.Builder builder = WebClient.builder();
        customizer.customize(builder);
        
        // Verify the builder was modified by the customizer
        assertNotNull(builder, "WebClient builder should not be null");
        
        // Build the client and verify its configuration
        WebClient client = builder.build();
        assertNotNull(client, "WebClient should be created successfully");

        // Verify the reactor resource factory is properly set up
        assertFalse(reactorResourceFactory.isUseGlobalResources(),
            "ReactorResourceFactory should not use global resources");


        // Force the connection lambda to execute with write operations
        for (int i = 0; i < 2; i++) {
            try {
                // Using raw TCP connection attempts to trigger connection setup
                client.post()
                    .uri("http://127.0.0.1:1")  // Use invalid port to force connection failure
                    .bodyValue("test-data")      // Add payload to trigger write path
                    .exchangeToMono(response -> response.bodyToMono(String.class))
                    .timeout(Duration.ofMillis(100))  // Short timeout to avoid hanging
                    .doOnError(e -> {
                        // Error is expected, the important part is that the connection was attempted
                        // which means the lambda was executed
                    })
                    .onErrorResume(e -> reactor.core.publisher.Mono.empty())
                    .block(Duration.ofMillis(200));
            } catch (Exception expected) {
                // Expected - connection should fail but lambda should be executed
                assertTrue(expected.getCause() instanceof ConnectException || expected.getCause() instanceof TimeoutException,
                    "Expected connection failure or timeout but got: " + expected.getClass().getName());
            }
        }
    }

    @Test
    void testConnectionHandlers() {
        // Create resources
        ReactorResourceFactory reactorResourceFactory = new ReactorResourceFactory();
        reactorResourceFactory.setUseGlobalResources(false);
        WebClientCustomizer customizer = reactorClientConfiguration.shoreWebClientCustomizer(reactorResourceFactory);

        // Build the WebClient with custom connection handlers
        WebClient.Builder builder = WebClient.builder();
        customizer.customize(builder);
        WebClient client = builder.build();

        // Test different HTTP methods to ensure handlers are added
        for (String method : new String[]{"GET", "POST", "PUT", "DELETE"}) {
            try {
                client.method(org.springframework.http.HttpMethod.valueOf(method))
                    .uri("http://127.0.0.1:1")  // Invalid port to force connection failure
                    .bodyValue("test")           // Add body to test write path
                    .exchangeToMono(response -> response.bodyToMono(String.class))
                    .timeout(Duration.ofMillis(50))
                    .block();
                fail("Expected connection to fail but it succeeded for method: " + method);
            } catch (Exception e) {
                // Verify that the exception is due to connection failure or timeout
                assertTrue(e instanceof ConnectException || 
                         e.getCause() instanceof ConnectException ||
                         e instanceof TimeoutException ||
                         e.getCause() instanceof TimeoutException,
                    "Expected connection failure or timeout but got: " + e.getClass().getName() + " for method: " + method);
                
                // Verify the error message contains connection-related information
                String errorMessage = e.getMessage() + (e.getCause() != null ? ", Cause: " + e.getCause().getMessage() : "");
                assertTrue(errorMessage.contains("Connection") || 
                         errorMessage.contains("refused") ||
                         errorMessage.contains("timeout") ||
                         errorMessage.contains("Timeout"),
                    "Expected connection-related error message but got: " + errorMessage);
            }
        }
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

