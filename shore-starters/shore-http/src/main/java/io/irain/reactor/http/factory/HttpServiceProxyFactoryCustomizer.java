package io.irain.reactor.http.factory;

import io.irain.reactor.commons.function.Customizer;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * HttpServiceProxyFactoryCustomizer.
 *
 * @author youta
 **/
@FunctionalInterface
public interface HttpServiceProxyFactoryCustomizer extends Customizer<HttpServiceProxyFactory.Builder> {
}
