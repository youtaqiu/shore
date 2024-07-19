package sh.rime.reactor.http.factory;

import sh.rime.reactor.http.function.CustomLoadBalancerExchangeFilterFunction;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author youta
 **/
@Setter
public class HttpFactoryBean implements FactoryBean<Object> {

    private Class<?> httpInterfaceType;

    private BeanFactory beanFactory;
    private String serverName;
    private CustomLoadBalancerExchangeFilterFunction reactorLoadBalancerExchangeFilterFunction;
    private final static String HTTP_PREFIX = "http://";

    @Override
    public Object getObject() {
        if (StringUtils.hasText(serverName)) {
            this.trimServerName();
            WebClient webClient = WebClient
                    .builder()
                    .filter(reactorLoadBalancerExchangeFilterFunction)
                    .baseUrl(HTTP_PREFIX.concat(serverName))
                    .build();
            HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(WebClientAdapter.create(webClient)).build();
            return factory.createClient(httpInterfaceType);
        }
        HttpServiceProxyFactory proxyFactory = beanFactory.getBean(HttpServiceProxyFactory.class);
        return proxyFactory.createClient(httpInterfaceType);
    }

    @Override
    public Class<?> getObjectType() {
        return httpInterfaceType;
    }

    /**
     * 剔除serverName可能多余的符号
     */
    private void trimServerName() {
        if (StringUtils.hasText(serverName)) {
            if (serverName.startsWith(HTTP_PREFIX)) {
                serverName = serverName.substring(HTTP_PREFIX.length());
            }
            if (serverName.endsWith("/")) {
                serverName = serverName.substring(0, serverName.length() - 1);
            }
        }
    }
}
