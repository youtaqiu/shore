package sh.rime.reactor.http.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import sh.rime.reactor.http.function.CustomLoadBalancerExchangeFilterFunction;

import java.util.List;
import java.util.function.Consumer;

/**
 * Load balancer exchange filter functions consumer.
 *
 * @author rained
 **/
public class LoadBalancerExchangeFilterFunctionsConsumer implements ExchangeFilterFunctionsConsumer {

    @Autowired
    private CustomLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public LoadBalancerExchangeFilterFunctionsConsumer() {
    }

    @Override
    public Consumer<List<ExchangeFilterFunction>> consume() {
        return filterFunctions -> filterFunctions.add(reactorLoadBalancerExchangeFilterFunction);
    }

}
