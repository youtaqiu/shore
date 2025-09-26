package run.vexa.reactor.http.testconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.vexa.reactor.http.core.LoadBalancerExchangeFilterFunctionsConsumer;
import run.vexa.reactor.http.function.CustomLoadBalancerExchangeFilterFunction;

import static org.mockito.Mockito.mock;

@Configuration
@SuppressWarnings("java:S2187")
public class LoadBalancerTestConfiguration {

    @Bean
    public CustomLoadBalancerExchangeFilterFunction customLoadBalancerExchangeFilterFunction() {
        return mock(CustomLoadBalancerExchangeFilterFunction.class);
    }

    @Bean
    public LoadBalancerExchangeFilterFunctionsConsumer loadBalancerExchangeFilterFunctionsConsumer() {
        return new LoadBalancerExchangeFilterFunctionsConsumer();
    }
}
