package run.vexa.reactor.http.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerClientRequestTransformer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.WebClientCustomizer;
import org.springframework.http.client.ReactorResourceFactory;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import run.vexa.reactor.http.function.CustomLoadBalancerExchangeFilterFunction;

import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WebClientLoadBalancerConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(WebClientLoadBalancerConfiguration.class)
            .withBean(ReactiveLoadBalancer.Factory.class, this::reactiveLoadBalancerFactory)
            .withBean(LoadBalancerClientRequestTransformer.class, () -> mock(LoadBalancerClientRequestTransformer.class));

    @Test
    void reactorResourceFactoryDisablesGlobalResources() {
        contextRunner.run(context -> {
            ReactorResourceFactory factory = context.getBean(ReactorResourceFactory.class);
            assertThat(factory.isUseGlobalResources()).isFalse();
        });
    }

    @Test
    void customFilterFunctionIsRegisteredAndCustomizerAppliesIt() {
        contextRunner.run(context -> {
            CustomLoadBalancerExchangeFilterFunction filterFunction =
                    context.getBean(CustomLoadBalancerExchangeFilterFunction.class);
            assertThat(filterFunction).isNotNull();

            WebClientCustomizer customizer = context.getBean(WebClientCustomizer.class);
            WebClient.Builder builder = WebClient.builder();
            customizer.customize(builder);

            List<ExchangeFilterFunction> filters = new ArrayList<>();
            builder.filters(filters::addAll);
            assertThat(filters).containsExactly(filterFunction);
        });
    }

    private ReactiveLoadBalancer.Factory<ServiceInstance> reactiveLoadBalancerFactory() {
        @SuppressWarnings("unchecked")
        ReactiveLoadBalancer.Factory<ServiceInstance> factory = mock(ReactiveLoadBalancer.Factory.class);
        ReactiveLoadBalancer<ServiceInstance> loadBalancer = request -> Mono.empty();
        when(factory.getInstance(anyString())).thenReturn(loadBalancer);
        when(factory.getInstance(anyString(), any(), any())).thenAnswer(invocation -> loadBalancer);
        when(factory.getProperties(anyString())).thenReturn(new org.springframework.cloud.client.loadbalancer.LoadBalancerProperties());
        return factory;
    }
}
