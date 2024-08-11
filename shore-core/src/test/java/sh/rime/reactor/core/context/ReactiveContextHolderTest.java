package sh.rime.reactor.core.context;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;

import static org.mockito.Mockito.mock;

/**
 * Reactive context holder unit test.
 *
 * @author rained
 **/
class ReactiveContextHolderTest {

    @Test
    void testGetExchange() {
        // Create a mock ServerWebExchange
        ServerWebExchange mockExchange = mock(ServerWebExchange.class);

        // Add the mock ServerWebExchange to the Reactor Context
        Mono<ServerWebExchange> exchangeMono = ReactiveContextHolder.getExchange()
                .contextWrite(Context.of(ReactiveContextHolder.CONTEXT_KEY, mockExchange));

        // Verify that the Mono emits the correct ServerWebExchange from the context
        StepVerifier.create(exchangeMono)
                .expectNext(mockExchange)
                .verifyComplete();
    }

    @Test
    void testGetExchangeWithEmptyContext() {
        // Test with an empty context to see if it correctly handles the absence of CONTEXT_KEY
        Mono<ServerWebExchange> exchangeMono = ReactiveContextHolder.getExchange()
                .contextWrite(Context.empty());

        // Verify that the Mono completes without emitting any value
        StepVerifier.create(exchangeMono)
                .verifyComplete();
    }
}
