package run.vexa.reactor.http.core;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
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
