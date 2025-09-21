package run.vexa.reactor.http.core;

import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

/**
 * hold a consumer of {@link HttpHeaders}
 *
 * @author rained
 **/
public interface HttpHeadersConsumer {

    /**
     * hold a consumer of {@link HttpHeaders}
     *
     * @return {@link Consumer<HttpHeaders>}
     */
    Consumer<HttpHeaders> consume();

}
