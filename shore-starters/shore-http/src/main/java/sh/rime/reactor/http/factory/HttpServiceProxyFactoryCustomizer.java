package sh.rime.reactor.http.factory;

import sh.rime.reactor.commons.function.Customizer;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * HttpServiceProxyFactoryCustomizer.
 *
 * @author youta
 **/
@FunctionalInterface
public interface HttpServiceProxyFactoryCustomizer extends Customizer<HttpServiceProxyFactory.Builder> {
}
