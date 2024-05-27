package io.irain.reactor.http.autoconfigure;

import io.irain.reactor.http.factory.HttpServiceProxyFactoryCustomizer;
import io.irain.reactor.http.factory.HttpServiceRegistrar;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author youta
 **/
@Configuration
@Import(HttpServiceRegistrar.class)
@ConditionalOnClass(value = WebClient.class)
public class HttpInterfaceAutoConfiguration {


    /**
     * Web client web client.
     *
     * @param builder the builder
     * @return the web client
     */
    @Bean
    @ConditionalOnMissingBean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    /**
     * Http service proxy factory http service proxy factory.
     *
     * @param webClient   the web client
     * @param customizers the customizers
     * @return the http service proxy factory
     */
    @Bean
    public HttpServiceProxyFactory httpServiceProxyFactory(WebClient webClient,
                                                           ObjectProvider<HttpServiceProxyFactoryCustomizer> customizers) {
        HttpServiceProxyFactory.Builder builder = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient));
        customizers.orderedStream().forEach(customizer -> customizer.customize(builder));
        return builder.build();
    }

}
