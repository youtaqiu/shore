package run.vexa.reactor.http.core;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import run.vexa.reactor.http.annotation.HttpClient;
import run.vexa.reactor.http.function.CustomLoadBalancerExchangeFilterFunction;
import run.vexa.reactor.http.testclients.CloudHttpClient;
import run.vexa.reactor.http.testclients.NoCloudHttpClient;
import run.vexa.reactor.http.testclients.ServerNameHttpClient;
import run.vexa.reactor.http.testconfig.LoadBalancerExchangeFilterConfigurationSupport;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;

class HttpExchangeClientFactoryBeanTest {

    @Test
    void filterSkippedWhenCloudDisabled() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(LoadBalancerExchangeFilterConfigurationSupport.class);
            context.refresh();

            MockEnvironment environment = new MockEnvironment();
            WebClient webClient = buildWebClient(NoCloudHttpClient.class, context, environment);

            List<ExchangeFilterFunction> filters = extractFilters(webClient);
            ExchangeFilterFunction loadBalancerFilter = context.getBean(CustomLoadBalancerExchangeFilterFunction.class);
            assertFalse(filters.contains(loadBalancerFilter));
        }
    }

    @Test
    void filterIncludedWhenCloudEnabled() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(LoadBalancerExchangeFilterConfigurationSupport.class);
            context.refresh();

            MockEnvironment environment = new MockEnvironment();
            WebClient webClient = buildWebClient(CloudHttpClient.class, context, environment);

            List<ExchangeFilterFunction> filters = extractFilters(webClient);
            ExchangeFilterFunction loadBalancerFilter = context.getBean(CustomLoadBalancerExchangeFilterFunction.class);
            assertTrue(filters.contains(loadBalancerFilter));
        }
    }

    @Test
    void filterIncludedWhenServerNamePresent() throws Exception {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(LoadBalancerExchangeFilterConfigurationSupport.class);
            context.refresh();

            MockEnvironment environment = new MockEnvironment();
            WebClient webClient = buildWebClient(ServerNameHttpClient.class, context, environment);

            List<ExchangeFilterFunction> filters = extractFilters(webClient);
            ExchangeFilterFunction loadBalancerFilter = context.getBean(CustomLoadBalancerExchangeFilterFunction.class);
            assertTrue(filters.contains(loadBalancerFilter));
        }
    }

    @Test
    void convertBaseUrlResolvesPlaceholdersAndEnsuresTrailingSlash() throws Exception {
        MockEnvironment environment = new MockEnvironment();
        environment.setProperty("service.base", "https://api.example.com/v1");

        String converted = invokeConvertBaseUrl("${service.base}", "", environment);

        assertEquals("https://api.example.com/v1/", converted);
    }

    @Test
    void convertBaseUrlPrefersServerName() throws Exception {
        MockEnvironment environment = new MockEnvironment();

        String converted = invokeConvertBaseUrl("http://ignored", "http://orders-service/", environment);

        assertEquals("http://orders-service", converted);
    }

    @Test
    void appliesCustomAnnotationConfiguration() throws Exception {
        InvocationTracker.reset();
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(LoadBalancerExchangeFilterConfigurationSupport.class);
            context.registerBean(TestHttpHeadersConsumer.class);
            context.registerBean(TestCookiesConsumer.class);
            context.registerBean(TestRequestHeadersSpecConsumer.class);
            context.registerBean(TestDefaultStatusHandlerHolder.class);
            context.registerBean(TestExchangeFilterFunction.class);
            context.registerBean(TestExchangeFilterFunctionsConsumer.class);
            context.registerBean(TestClientCodecConfigurerConsumer.class);
            context.registerBean(TestDefaultUriVariablesSupplier.class);
            context.registerBean(TestUriBuilderFactory.class);
            context.refresh();

            MockEnvironment environment = new MockEnvironment();
            environment.setProperty("service.base", "https://custom.example");

            WebClient webClient = buildWebClient(CustomConfiguredHttpClient.class, context, environment);

            assertTrue(InvocationTracker.headerConsumerInvoked);
            assertTrue(InvocationTracker.cookiesConsumerInvoked);
            assertTrue(InvocationTracker.requestHeadersConsumerInvoked);
            assertTrue(InvocationTracker.statusHandlerInvoked);
            assertTrue(TestExchangeFilterFunction.INSTANTIATED.get());
            assertTrue(InvocationTracker.filtersConsumerInvoked);
            assertTrue(InvocationTracker.codecConfigurerInvoked);
            assertTrue(TestDefaultUriVariablesSupplier.SUPPLY_INVOKED.get());
            assertTrue(TestUriBuilderFactory.CREATED.get());

            List<ExchangeFilterFunction> filters = extractFilters(webClient);
            assertTrue(filters.stream().anyMatch(filter -> filter instanceof TestExchangeFilterFunction));
            assertTrue(filters.contains(TestExchangeFilterFunctionsConsumer.ADDED_FILTER));
        }
    }

    private static WebClient buildWebClient(Class<?> clientInterface,
                                            AnnotationConfigApplicationContext context,
                                            MockEnvironment environment) throws ReflectiveOperationException {
        HttpExchangeClientFactoryBean factoryBean = new HttpExchangeClientFactoryBean();
        @SuppressWarnings("unchecked")
        Class<Object> cast = (Class<Object>) clientInterface;
        factoryBean.setHttpExchangeClientInterface(cast);
        factoryBean.setApplicationContext(context);
        factoryBean.setEnvironment(environment);

        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(HttpExchangeClientFactoryBean.class, lookup);
        MethodHandle methodHandle = privateLookup.findVirtual(HttpExchangeClientFactoryBean.class, "createWebClient",
                MethodType.methodType(WebClient.class));
        try {
            return (WebClient) methodHandle.invoke(factoryBean);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to invoke createWebClient", throwable);
        }
    }

    @HttpClient(
            baseUrl = "${service.base}",
            serverName = "custom-service",
            cloud = true,
            defaultHeaderKey = "X-Test",
            defaultHeaderValues = {"one", "two"},
            httpHeadersConsumer = TestHttpHeadersConsumer.class,
            defaultCookieKey = "sid",
            defaultCookieValues = {"cookie"},
            cookiesConsumer = TestCookiesConsumer.class,
            requestHeadersSpecConsumer = TestRequestHeadersSpecConsumer.class,
            defaultStatusHandlerHolder = TestDefaultStatusHandlerHolder.class,
            filter = TestExchangeFilterFunction.class,
            filtersConsumer = TestExchangeFilterFunctionsConsumer.class,
            codecConfigurerConsumer = TestClientCodecConfigurerConsumer.class,
            defaultUriVariablesSupplier = TestDefaultUriVariablesSupplier.class,
            uriBuilderFactory = TestUriBuilderFactory.class
    )
    interface CustomConfiguredHttpClient {
    }

    private static final class InvocationTracker {
        private static boolean headerConsumerInvoked;
        private static boolean cookiesConsumerInvoked;
        private static boolean requestHeadersConsumerInvoked;
        private static boolean statusHandlerInvoked;
        private static boolean filtersConsumerInvoked;
        private static boolean codecConfigurerInvoked;

        private static void reset() {
            headerConsumerInvoked = false;
            cookiesConsumerInvoked = false;
            requestHeadersConsumerInvoked = false;
            statusHandlerInvoked = false;
            filtersConsumerInvoked = false;
            codecConfigurerInvoked = false;
            TestExchangeFilterFunction.INSTANTIATED.set(false);
            TestDefaultUriVariablesSupplier.SUPPLY_INVOKED.set(false);
            TestUriBuilderFactory.CREATED.set(false);
        }
    }

    static final class TestHttpHeadersConsumer implements HttpHeadersConsumer {
        @Override
        public java.util.function.Consumer<HttpHeaders> consume() {
            InvocationTracker.headerConsumerInvoked = true;
            return headers -> headers.add("X-Header", "value");
        }
    }

    static final class TestCookiesConsumer implements CookiesConsumer {
        @Override
        public java.util.function.Consumer<MultiValueMap<String, String>> consume() {
            InvocationTracker.cookiesConsumerInvoked = true;
            return cookies -> cookies.add("session", "value");
        }
    }

    static final class TestRequestHeadersSpecConsumer implements RequestHeadersSpecConsumer {
        @Override
        public java.util.function.Consumer<WebClient.RequestHeadersSpec<?>> consume() {
            InvocationTracker.requestHeadersConsumerInvoked = true;
            return spec -> spec.header("X-Request", "value");
        }
    }

    static final class TestDefaultStatusHandlerHolder implements DefaultStatusHandlerHolder {
        @Override
        public java.util.function.Predicate<HttpStatusCode> statusPredicate() {
            InvocationTracker.statusHandlerInvoked = true;
            return status -> false;
        }

        @Override
        public java.util.function.Function<ClientResponse, Mono<? extends Throwable>> exceptionFunction() {
            return response -> Mono.empty();
        }
    }

    static final class TestExchangeFilterFunction implements ExchangeFilterFunction {
        private static final AtomicBoolean INSTANTIATED = new AtomicBoolean(false);

    TestExchangeFilterFunction() {
            INSTANTIATED.set(true);
        }

        @Override
        public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
            return next.exchange(request);
        }
    }

    static final class TestExchangeFilterFunctionsConsumer implements ExchangeFilterFunctionsConsumer {
        static final ExchangeFilterFunction ADDED_FILTER = (request, next) -> next.exchange(request);

        @Override
        public java.util.function.Consumer<List<ExchangeFilterFunction>> consume() {
            InvocationTracker.filtersConsumerInvoked = true;
            return filters -> filters.add(ADDED_FILTER);
        }
    }

    static final class TestClientCodecConfigurerConsumer implements ClientCodecConfigurerConsumer {
        @Override
        public java.util.function.Consumer<ClientCodecConfigurer> consumer() {
            InvocationTracker.codecConfigurerInvoked = true;
            return configurer -> {
            };
        }
    }

    static final class TestDefaultUriVariablesSupplier implements DefaultUriVariablesSupplier {
        private static final AtomicBoolean SUPPLY_INVOKED = new AtomicBoolean(false);

        @Override
        public Map<String, ?> supply() {
            SUPPLY_INVOKED.set(true);
            return Map.of("token", "123");
        }
    }

    static final class TestUriBuilderFactory extends org.springframework.web.util.DefaultUriBuilderFactory {
        private static final AtomicBoolean CREATED = new AtomicBoolean(false);

    TestUriBuilderFactory() {
            CREATED.set(true);
        }
    }

    private static List<ExchangeFilterFunction> extractFilters(WebClient webClient) {
        List<ExchangeFilterFunction> filters = new ArrayList<>();
        webClient.mutate().filters(filters::addAll);
        return filters;
    }

    private static String invokeConvertBaseUrl(String baseUrl, String serverName, Environment environment) throws ReflectiveOperationException {
        Class<?> configureClass = Class.forName("run.vexa.reactor.http.core.HttpExchangeClientFactoryBean$WebClientConfigure");
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandles.Lookup privateLookup = MethodHandles.privateLookupIn(configureClass, lookup);
        MethodHandle convertHandle = privateLookup.findStatic(configureClass, "convertBaseUrl",
                MethodType.methodType(String.class, String.class, String.class, Environment.class));
        try {
            return (String) convertHandle.invoke(baseUrl, serverName, environment);
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to invoke convertBaseUrl", throwable);
        }
    }
}
