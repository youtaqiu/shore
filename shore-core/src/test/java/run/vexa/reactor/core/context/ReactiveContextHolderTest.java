package run.vexa.reactor.core.context;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.util.context.Context;
import run.vexa.reactor.core.test.TestUtils;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

/**
 * Reactive context holder unit test.
 *
 * @author rained
 **/
class ReactiveContextHolderTest {

    @Test
    void testPrivateConstructor() {
        // The actual exception is wrapped in InvocationTargetException
        var exception = assertThrows(InvocationTargetException.class,
            () -> TestUtils.invokePrivateConstructor(ReactiveContextHolder.class));
        
        // Verify the cause is UnsupportedOperationException with the right message
        assertInstanceOf(UnsupportedOperationException.class, exception.getCause());
        assertEquals(
            "This is a utility class and cannot be instantiated", 
            exception.getCause().getMessage()
        );
    }

    @Test
    void testContextKey() {
        assertEquals(ReactiveContextHolder.CONTEXT_KEY, ServerWebExchange.class);
    }

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

    @Test
    void testGetExchangeWithWrongType() {
        // Test with a wrong type in the context
        Mono<ServerWebExchange> exchangeMono = Mono.empty()
                .contextWrite(Context.of(ReactiveContextHolder.CONTEXT_KEY, "wrong type"))
                .transform(unused -> ReactiveContextHolder.getExchange());

        // Since there's a wrong type in the context, it should be treated as not having the key
        StepVerifier.create(exchangeMono)
                .verifyComplete();
    }

}
