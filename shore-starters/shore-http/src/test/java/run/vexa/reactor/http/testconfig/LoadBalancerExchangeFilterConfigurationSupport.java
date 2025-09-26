package run.vexa.reactor.http.testconfig;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import run.vexa.reactor.http.core.LoadBalancerExchangeFilterFunctionsConsumer;
import run.vexa.reactor.http.function.CustomLoadBalancerExchangeFilterFunction;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

@Configuration
public class LoadBalancerExchangeFilterConfigurationSupport {

    @Bean
    public CustomLoadBalancerExchangeFilterFunction customLoadBalancerExchangeFilterFunction() {
        return mock(CustomLoadBalancerExchangeFilterFunction.class);
    }

    @Bean
    public LoadBalancerExchangeFilterFunctionsConsumer loadBalancerExchangeFilterFunctionsConsumer() {
        return new LoadBalancerExchangeFilterFunctionsConsumer();
    }

}

class LoadBalancerExchangeFilterConfigurationSupportTest {

    @Test
    void registersBeans() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(LoadBalancerExchangeFilterConfigurationSupport.class)) {
            assertNotNull(context.getBean(CustomLoadBalancerExchangeFilterFunction.class));
            assertNotNull(context.getBean(LoadBalancerExchangeFilterFunctionsConsumer.class));
        }
    }
}
