package run.vexa.reactor.http.core;

import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

import java.util.List;
import java.util.function.Consumer;

/**
 * Exchange filter functions consumer.
 *
 * @author rained
 **/
public interface ExchangeFilterFunctionsConsumer {

    /**
     * hold a consumer of {@link ExchangeFilterFunction} list
     *
     * @return {@link Consumer<List<ExchangeFilterFunction>>}
     */
    Consumer<List<ExchangeFilterFunction>> consume();
}
