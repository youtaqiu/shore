package run.vexa.reactor.http.function;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.cloud.client.loadbalancer.reactive.ReactiveLoadBalancer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import run.vexa.reactor.commons.constants.Constants;
import run.vexa.reactor.security.context.UserContextHolder;

import java.net.URI;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CustomLoadBalancerExchangeFilterFunctionTest {

    @Test
    void addsAuthorizationHeaderWhenTokenAvailable() {
        CustomLoadBalancerExchangeFilterFunction filterFunction =
                new CustomLoadBalancerExchangeFilterFunction(loadBalancerFactory(), Collections.emptyList());

        AtomicReference<ClientRequest> interceptedRequest = new AtomicReference<>();
        ExchangeFunction exchangeFunction = clientRequest -> {
            interceptedRequest.set(clientRequest);
            return Mono.just(ClientResponse.create(HttpStatus.OK).build());
        };

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://service/endpoint"))
                .build();

        try (MockedStatic<UserContextHolder> mocked = Mockito.mockStatic(UserContextHolder.class)) {
            mocked.when(UserContextHolder::token).thenReturn(Mono.just("token-123"));

            StepVerifier.create(filterFunction.filter(request, exchangeFunction))
                    .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                    .verifyComplete();
        }

        ClientRequest captured = interceptedRequest.get();
        assertThat(captured).isNotNull();
        assertThat(captured.headers().getFirst(HttpHeaders.AUTHORIZATION))
                .isEqualTo(Constants.TOKEN_TYPE + "token-123");
    }

    @Test
    void delegatesWithoutAuthorizationHeaderWhenTokenMissing() {
        CustomLoadBalancerExchangeFilterFunction filterFunction =
                new CustomLoadBalancerExchangeFilterFunction(loadBalancerFactory(), Collections.emptyList());

        AtomicReference<ClientRequest> interceptedRequest = new AtomicReference<>();
        ExchangeFunction exchangeFunction = clientRequest -> {
            interceptedRequest.set(clientRequest);
            return Mono.just(ClientResponse.create(HttpStatus.OK).build());
        };

        ClientRequest request = ClientRequest.create(HttpMethod.GET, URI.create("http://service/endpoint"))
                .build();

        try (MockedStatic<UserContextHolder> mocked = Mockito.mockStatic(UserContextHolder.class)) {
            mocked.when(UserContextHolder::token).thenReturn(Mono.empty());

            StepVerifier.create(filterFunction.filter(request, exchangeFunction))
                    .expectNextMatches(response -> response.statusCode().equals(HttpStatus.OK))
                    .verifyComplete();
        }

        ClientRequest captured = interceptedRequest.get();
        assertThat(captured).isNotNull();
        assertThat(captured.headers().contains(HttpHeaders.AUTHORIZATION)).isFalse();
    }

    private ReactiveLoadBalancer.Factory<ServiceInstance> loadBalancerFactory() {
        ServiceInstance instance = new DefaultServiceInstance("service-instance", "service", "localhost", 8080, false);
        ReactiveLoadBalancer<ServiceInstance> loadBalancer = request -> Mono.just(new StaticResponse(instance));
        @SuppressWarnings("unchecked")
        ReactiveLoadBalancer.Factory<ServiceInstance> factory = mock(ReactiveLoadBalancer.Factory.class);
        when(factory.getInstance(anyString())).thenReturn(loadBalancer);
        when(factory.getInstance(anyString(), any(), any())).thenAnswer(invocation -> loadBalancer);
        when(factory.getProperties(anyString())).thenReturn(new org.springframework.cloud.client.loadbalancer.LoadBalancerProperties());
        return factory;
    }

    private static final class StaticResponse implements Response<ServiceInstance> {

        private final ServiceInstance instance;

        private StaticResponse(ServiceInstance instance) {
            this.instance = instance;
        }

        @Override
        public boolean hasServer() {
            return instance != null;
        }

        @Override
        public ServiceInstance getServer() {
            return instance;
        }
    }
}
