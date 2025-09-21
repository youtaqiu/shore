package run.vexa.reactor.http.core;

import lombok.Setter;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.util.UriBuilderFactory;
import run.vexa.reactor.core.util.ApplicationContextUtils;
import run.vexa.reactor.http.annotation.HttpClient;

import java.util.Objects;

/**
 * Http exchange client factory bean.
 *
 * @author rained
 **/
@SuppressWarnings("unused")
public class HttpExchangeClientFactoryBean implements FactoryBean<Object>, EnvironmentAware, ApplicationContextAware {

    private static final String HTTP_PREFIX = "http://";

    /**
     * the interface class annotated by {@link HttpClient}
     */
    @Setter
    private Class<Object> httpExchangeClientInterface;

    private Environment environment;

    private ApplicationContext applicationContext;

    /**
     * default constructor
     */
    public HttpExchangeClientFactoryBean() {
    }

    /**
     * constructor with httpExchangeClientInterface
     *
     * @param httpExchangeClientInterface the interface class annotated by {@link HttpClient}
     */
    public HttpExchangeClientFactoryBean(Class<Object> httpExchangeClientInterface) {
        this.httpExchangeClientInterface = httpExchangeClientInterface;
    }

    @Override
    public Object getObject() {
        return createHttpServiceProxyFactory().createClient(httpExchangeClientInterface);
    }

    /**
     * create HttpServiceProxyFactory
     *
     * @return HttpServiceProxyFactory
     */
    private HttpServiceProxyFactory createHttpServiceProxyFactory() {
        return HttpServiceProxyFactory.builderFor(createWebClientAdapter()).build();
    }

    /**
     * create WebClientAdapter
     *
     * @return WebClientAdapter
     */
    private WebClientAdapter createWebClientAdapter() {
        return WebClientAdapter.create(createWebClient());
    }

    /**
     * create WebClient
     *
     * @return WebClient
     */
    private WebClient createWebClient() {
        HttpClient httpClient = AnnotatedElementUtils.findMergedAnnotation(httpExchangeClientInterface, HttpClient.class);
        WebClientConfigure configure = new WebClientConfigure(httpClient, WebClient.builder(), applicationContext, environment);
        return configure.baseUrl()
                .defaultUriVariables()
                .uriBuilderFactory()
                .defaultHeader()
                .defaultHeaders()
                .defaultCookie()
                .defaultCookies()
                .requestHeadersSpec()
                .filter()
                .filters()
                .defaultStatusHandler()
                .exchangeStrategies()
                .build();
    }

    /**
     * WebClientConfigure
     *
     * @param httpClient         {@link HttpClient}
     * @param builder            {@link WebClient.Builder}
     * @param applicationContext {@link ApplicationContext}
     * @param environment        {@link Environment}
     */
    private record WebClientConfigure(HttpClient httpClient,
                                      WebClient.Builder builder,
                                      ApplicationContext applicationContext,
                                      Environment environment) {

        /**
         * convert base url
         *
         * @param baseUrl     source baseUrl
         * @param environment {@link Environment}
         * @return target baseUrl
         */
        private static String convertBaseUrl(String baseUrl, String serverName, Environment environment) {
            if (StringUtils.hasText(baseUrl)) {
                baseUrl = environment.resolveRequiredPlaceholders(baseUrl);
                // 解析 baseUrl 占位符
                if (!baseUrl.endsWith("/")) {
                    baseUrl += "/";
                }
            }
            if (StringUtils.hasText(serverName)) {
                if (serverName.startsWith(HTTP_PREFIX)) {
                    serverName = serverName.substring(HTTP_PREFIX.length());
                }
                if (serverName.endsWith("/")) {
                    serverName = serverName.substring(0, serverName.length() - 1);
                }
                baseUrl = HTTP_PREFIX + serverName;
            }
            return baseUrl;
        }

        /**
         * baseUrl
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure baseUrl() {
            builder.baseUrl(convertBaseUrl(httpClient.baseUrl(), httpClient.serverName(), environment));
            return this;
        }

        /**
         * defaultUriVariables
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure defaultUriVariables() {
            Class<? extends DefaultUriVariablesSupplier> clazz = httpClient.defaultUriVariablesSupplier();
            DefaultUriVariablesSupplier supplier = null;
            if (clazz != DefaultUriVariablesSupplier.class) {
                supplier = ApplicationContextUtils.getBeanOrReflect(applicationContext, clazz);
            }
            if (Objects.nonNull(supplier)) {
                builder.defaultUriVariables(supplier.supply());
            }
            return this;
        }

        /**
         * uriBuilderFactory
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure uriBuilderFactory() {
            Class<? extends UriBuilderFactory> clazz = httpClient.uriBuilderFactory();
            UriBuilderFactory uriBuilderFactory = null;
            if (clazz != UriBuilderFactory.class) {
                uriBuilderFactory = ApplicationContextUtils.getBeanOrReflect(applicationContext, clazz);
            }
            if (Objects.nonNull(uriBuilderFactory)) {
                builder.uriBuilderFactory(uriBuilderFactory);
            }
            return this;
        }

        /**
         * filter
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure filter() {
            Class<? extends ExchangeFilterFunction> clazz = httpClient.filter();
            ExchangeFilterFunction filterFunction = null;
            if (clazz != ExchangeFilterFunction.class) {
                filterFunction = ApplicationContextUtils.getBeanOrReflect(applicationContext, clazz);
            }
            if (Objects.nonNull(filterFunction)) {
                builder.filter(filterFunction);
            }
            return this;
        }

        /**
         * filters
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure filters() {
            Class<? extends ExchangeFilterFunctionsConsumer> clazz = httpClient.filtersConsumer();
            ExchangeFilterFunctionsConsumer functionsConsumer = null;
            if (clazz != ExchangeFilterFunctionsConsumer.class) {
                functionsConsumer = ApplicationContextUtils.getBeanOrReflect(applicationContext, clazz);
            }
            if (Objects.nonNull(functionsConsumer)) {
                boolean cloud = httpClient.cloud();
                if (StringUtils.hasText(httpClient.serverName())) {
                    cloud = true;
                }
                if (functionsConsumer instanceof LoadBalancerExchangeFilterFunctionsConsumer && !cloud) {
                    return this;
                }
                builder.filters(functionsConsumer.consume());
            }
            return this;
        }

        /**
         * defaultHeader
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure defaultHeader() {
            String headerKey = httpClient.defaultHeaderKey();
            String[] headerValues = httpClient.defaultHeaderValues();
            if (StringUtils.hasText(headerKey) && Objects.nonNull(headerValues) && headerValues.length != 0) {
                builder.defaultHeader(headerKey, headerValues);
            }
            return this;
        }

        /**
         * defaultHeaders
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure defaultHeaders() {
            Class<? extends HttpHeadersConsumer> clazz = httpClient.httpHeadersConsumer();
            HttpHeadersConsumer consumer = null;
            if (clazz != HttpHeadersConsumer.class) {
                consumer = ApplicationContextUtils.getBeanOrReflect(applicationContext, clazz);
            }
            if (Objects.nonNull(consumer)) {
                builder.defaultHeaders(consumer.consume());
            }
            return this;
        }

        /**
         * defaultCookie
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure defaultCookie() {
            String cookieKey = httpClient.defaultCookieKey();
            String[] cookieValues = httpClient.defaultCookieValues();
            if (StringUtils.hasText(cookieKey) && Objects.nonNull(cookieValues) && cookieValues.length != 0) {
                builder.defaultCookie(cookieKey, cookieValues);
            }
            return this;
        }

        /**
         * defaultCookies
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure defaultCookies() {
            Class<? extends CookiesConsumer> clazz = httpClient.cookiesConsumer();
            CookiesConsumer consumer = null;
            if (clazz != CookiesConsumer.class) {
                consumer = ApplicationContextUtils.getBeanOrReflect(applicationContext, clazz);
            }
            if (Objects.nonNull(consumer)) {
                builder.defaultCookies(consumer.consume());
            }
            return this;
        }

        /**
         * requestHeadersSpec
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure requestHeadersSpec() {
            Class<? extends RequestHeadersSpecConsumer> clazz = httpClient.requestHeadersSpecConsumer();
            RequestHeadersSpecConsumer consumer = null;
            if (clazz != RequestHeadersSpecConsumer.class) {
                consumer = ApplicationContextUtils.getBeanOrReflect(applicationContext, clazz);
            }
            if (Objects.nonNull(consumer)) {
                builder.defaultRequest(consumer.consume());
            }
            return this;
        }

        /**
         * defaultStatusHandler
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure defaultStatusHandler() {
            Class<? extends DefaultStatusHandlerHolder> clazz = httpClient.defaultStatusHandlerHolder();
            DefaultStatusHandlerHolder defaultStatusHandlerHolder = null;
            if (clazz != DefaultStatusHandlerHolder.class) {
                defaultStatusHandlerHolder = ApplicationContextUtils.getBeanOrReflect(applicationContext, clazz);
            }
            if (Objects.nonNull(defaultStatusHandlerHolder)) {
                builder.defaultStatusHandler(defaultStatusHandlerHolder.statusPredicate(), defaultStatusHandlerHolder.exceptionFunction());
            }
            return this;
        }

        /**
         * exchangeStrategies
         *
         * @return WebClientConfigure
         */
        public WebClientConfigure exchangeStrategies() {
            Class<? extends ClientCodecConfigurerConsumer> clazz = httpClient.codecConfigurerConsumer();
            ClientCodecConfigurerConsumer consumer = null;
            if (clazz != ClientCodecConfigurerConsumer.class) {
                consumer = ApplicationContextUtils.getBeanOrReflect(applicationContext, clazz);
            }
            if (Objects.nonNull(consumer)) {
                builder.exchangeStrategies(
                        ExchangeStrategies.builder()
                                .codecs(consumer.consumer())
                                .build()
                );
            }
            return this;
        }

        /**
         * build WebClient
         *
         * @return WebClient
         */
        public WebClient build() {
            return builder.build();
        }
    }

    @Override
    public Class<?> getObjectType() {
        return this.httpExchangeClientInterface;
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
