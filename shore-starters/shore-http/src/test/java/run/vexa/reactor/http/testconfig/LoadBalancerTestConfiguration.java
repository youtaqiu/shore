package run.vexa.reactor.http.testconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.vexa.reactor.http.core.LoadBalancerExchangeFilterFunctionsConsumer;
import run.vexa.reactor.http.function.CustomLoadBalancerExchangeFilterFunction;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@Configuration
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

class LoadBalancerTestConfigurationTest {

    @Test
    void registersBeans() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(LoadBalancerTestConfiguration.class)) {
            assertNotNull(context.getBean(CustomLoadBalancerExchangeFilterFunction.class));
            assertNotNull(context.getBean(LoadBalancerExchangeFilterFunctionsConsumer.class));
        }
    }
}
