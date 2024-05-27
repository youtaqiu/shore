package io.irain.reactor.http.function;

import io.irain.reactor.commons.constants.Constants;
import io.irain.reactor.security.context.UserContextHolder;
import io.micrometer.common.util.StringUtils;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerClientRequestTransformer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @author youta
 **/
public class CustomLoadBalancerExchangeFilterFunction extends ReactorLoadBalancerExchangeFilterFunction {

    /**
     * 自定义负载均衡过滤器函数
     *
     * @param loadBalancerFactory 负载均衡工厂
     * @param transformers        负载均衡转换器
     */
    public CustomLoadBalancerExchangeFilterFunction(ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory,
                                                    List<LoadBalancerClientRequestTransformer> transformers) {
        super(loadBalancerFactory, transformers);
    }


    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        return UserContextHolder.token()
                .filter(StringUtils::isNotBlank)
                .flatMap(token -> super.filter(ClientRequest.from(request)
                        .header(HttpHeaders.AUTHORIZATION, Constants.TOKEN_TYPE.concat(token))
                        .build(), next))
                .switchIfEmpty(super.filter(request, next));
    }

}
