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

import java.lang.reflect.Method;
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
                                            MockEnvironment environment) throws Exception {
        HttpExchangeClientFactoryBean factoryBean = new HttpExchangeClientFactoryBean();
        @SuppressWarnings("unchecked")
        Class<Object> cast = (Class<Object>) clientInterface;
        factoryBean.setHttpExchangeClientInterface(cast);
        factoryBean.setApplicationContext(context);
        factoryBean.setEnvironment(environment);

        Method method = HttpExchangeClientFactoryBean.class.getDeclaredMethod("createWebClient");
        method.setAccessible(true);
        return (WebClient) method.invoke(factoryBean);
    }

    private static List<ExchangeFilterFunction> extractFilters(WebClient webClient) {
        List<ExchangeFilterFunction> filters = new ArrayList<>();
        webClient.mutate().filters(filters::addAll);
        return filters;
    }

    private static String invokeConvertBaseUrl(String baseUrl, String serverName, Environment environment) throws Exception {
        Class<?> configureClass = Class.forName("run.vexa.reactor.http.core.HttpExchangeClientFactoryBean$WebClientConfigure");
        Method convertMethod = configureClass.getDeclaredMethod("convertBaseUrl", String.class, String.class, Environment.class);
        convertMethod.setAccessible(true);
        return (String) convertMethod.invoke(null, baseUrl, serverName, environment);
    }
}
