package run.vexa.reactor.http.autoconfigure;

import run.vexa.reactor.http.function.CustomLoadBalancerExchangeFilterFunction;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerClientRequestTransformer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ReactorResourceFactory;

import java.util.Collections;
import java.util.List;

/**
 * webclient负载均衡配置
 *
 * @author youta
 **/
@Configuration
public class WebClientLoadBalancerConfiguration {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public WebClientLoadBalancerConfiguration() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * 负载均衡工厂
     *
     * @return 负载均衡工厂
     */
    @Bean
    public ReactorResourceFactory reactorResourceFactory() {
        ReactorResourceFactory factory = new ReactorResourceFactory();
        factory.setUseGlobalResources(false);
        return factory;
    }

    /**
     * 自定义负载均衡过滤器函数
     *
     * @param loadBalancerFactory 负载均衡工厂
     * @param transformers        负载均衡转换器
     * @return 自定义负载均衡过滤器函数
     */
    @ConditionalOnMissingBean
    @ConditionalOnProperty(value = "spring.cloud.loadbalancer.retry.enabled", havingValue = "false",
            matchIfMissing = true)
    @Bean
    @SuppressWarnings("all")
    public CustomLoadBalancerExchangeFilterFunction loadBalancerExchangeFilterFunction(
            ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory,
            ObjectProvider<List<LoadBalancerClientRequestTransformer>> transformers) {
        return new CustomLoadBalancerExchangeFilterFunction(loadBalancerFactory,
                transformers.getIfAvailable(Collections::emptyList));
    }

    /**
     * webclient bean定制器
     *
     * @param reactorLoadBalancerExchangeFilterFunction 负载均衡过滤器函数
     * @return webclient bean定制器
     */
    @Bean
    public WebClientCustomizer loadbalancerWebClientCustomizer(CustomLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction) {
        return webClientBuilder -> webClientBuilder.filter(reactorLoadBalancerExchangeFilterFunction);
    }

}
