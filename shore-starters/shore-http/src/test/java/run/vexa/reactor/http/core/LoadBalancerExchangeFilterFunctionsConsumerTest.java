package run.vexa.reactor.http.core;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import run.vexa.reactor.http.function.CustomLoadBalancerExchangeFilterFunction;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class LoadBalancerExchangeFilterFunctionsConsumerTest {

    @Test
    void consumerAddsLoadBalancerFilter() {
        CustomLoadBalancerExchangeFilterFunction filterFunction = mock(CustomLoadBalancerExchangeFilterFunction.class);
        LoadBalancerExchangeFilterFunctionsConsumer consumer = new LoadBalancerExchangeFilterFunctionsConsumer();
        ReflectionTestUtils.setField(consumer, "reactorLoadBalancerExchangeFilterFunction", filterFunction);

        List<ExchangeFilterFunction> functions = new ArrayList<>();
        consumer.consume().accept(functions);

        assertThat(functions).containsExactly(filterFunction);
    }
}
