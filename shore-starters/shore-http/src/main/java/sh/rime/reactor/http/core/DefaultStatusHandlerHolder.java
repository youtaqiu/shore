package sh.rime.reactor.http.core;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Default status handler holder.
 *
 * @author rained
 **/
public interface DefaultStatusHandlerHolder {

    /**
     * to match responses with
     *
     * @return {@link Predicate< HttpStatusCode >}
     */
    Predicate<HttpStatusCode> statusPredicate();

    /**
     * to map the response to an error signal
     *
     * @return {@link Function<ClientResponse, Mono<? extends Throwable>>}
     */
    Function<ClientResponse, Mono<? extends Throwable>> exceptionFunction();
}
