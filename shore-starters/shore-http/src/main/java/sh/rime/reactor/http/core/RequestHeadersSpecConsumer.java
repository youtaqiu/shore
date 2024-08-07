package sh.rime.reactor.http.core;

import org.springframework.web.reactive.function.client.WebClient;

import java.util.function.Consumer;

/**
 * Request headers spec consumer.
 *
 * @author rained
 **/
public interface RequestHeadersSpecConsumer {

    /**
     * hold a consumer of {@link WebClient.RequestHeadersSpec<?>}
     *
     * @return {@link Consumer<WebClient.RequestHeadersSpec<?>>}
     */
    Consumer<WebClient.RequestHeadersSpec<?>> consume();
}
