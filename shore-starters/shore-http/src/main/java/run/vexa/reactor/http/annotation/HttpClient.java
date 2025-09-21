package run.vexa.reactor.http.annotation;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.util.UriBuilderFactory;
import run.vexa.reactor.http.core.*;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Http exchange client.
 *
 * @author rained
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
@Component
public @interface HttpClient {

    /**
     * Base url string.
     *
     * @return the string
     */
    String baseUrl() default "";

    /**
     * Server name string.
     *
     * @return the string
     */
    String serverName() default "";

    /**
     * Cloud boolean.
     *
     * @return the boolean
     */
    boolean cloud() default false;

    /**
     * defaultUriVariablesSupplier
     *
     * @return the defaultUriVariablesSupplier
     */
    Class<? extends DefaultUriVariablesSupplier> defaultUriVariablesSupplier() default DefaultUriVariablesSupplier.class;

    /**
     * Uri builder factory class.
     *
     * @return the class
     */
    Class<? extends UriBuilderFactory> uriBuilderFactory() default UriBuilderFactory.class;

    /**
     * Default header key string.
     *
     * @return the string
     */
    String defaultHeaderKey() default "";

    /**
     * Default header values string [ ].
     *
     * @return the string [ ]
     */
    String[] defaultHeaderValues() default {};

    /**
     * Http headers consumer class.
     *
     * @return the class
     */
    Class<? extends HttpHeadersConsumer> httpHeadersConsumer() default HttpHeadersConsumer.class;

    /**
     * Default cookie key string.
     *
     * @return the string
     */
    String defaultCookieKey() default "";

    /**
     * Default cookie values string [ ].
     *
     * @return the string [ ]
     */
    String[] defaultCookieValues() default {};

    /**
     * Cookies consumer class.
     *
     * @return the class
     */
    Class<? extends CookiesConsumer> cookiesConsumer() default CookiesConsumer.class;

    /**
     * Request headers spec consumer class.
     *
     * @return the class
     */
    Class<? extends RequestHeadersSpecConsumer> requestHeadersSpecConsumer() default RequestHeadersSpecConsumer.class;

    /**
     * Request body consumer class.
     *
     * @return the class
     */
    Class<? extends DefaultStatusHandlerHolder> defaultStatusHandlerHolder() default DefaultStatusHandlerHolder.class;

    /**
     * Request body consumer class.
     *
     * @return the class
     */
    Class<? extends ExchangeFilterFunction> filter() default ExchangeFilterFunction.class;

    /**
     * Filters consumer class.
     *
     * @return the class
     */
    Class<? extends ExchangeFilterFunctionsConsumer> filtersConsumer() default LoadBalancerExchangeFilterFunctionsConsumer.class;

    /**
     * Codec configurer consumer class.
     *
     * @return the class
     */
    Class<? extends ClientCodecConfigurerConsumer> codecConfigurerConsumer() default ClientCodecConfigurerConsumer.class;
}
