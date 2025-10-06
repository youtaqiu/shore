package run.vexa.reactor.log.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JoinPointSerialiseTest {

    private JoinPointSerialise serialiser;

    @BeforeEach
    void setUp() {
        serialiser = new JoinPointSerialise();
    }

    @Test
    void serialiseShouldIncludeParametersAndQueryParams() throws Exception {
        Method method = SampleController.class.getDeclaredMethod("handleWithRequest", String.class, String.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getMethod()).thenReturn(method);
        when(signature.getParameterNames()).thenReturn(new String[]{"hidden", "visible"});
        when(signature.getDeclaringType()).thenReturn(SampleController.class);
        when(signature.getName()).thenReturn("handle");

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"secret", Map.of("key", "value")});

        MockServerHttpRequest request = MockServerHttpRequest.get("/test")
                .queryParam("foo", "bar")
                .remoteAddress(new java.net.InetSocketAddress("127.0.0.1", 8080))
                .build();
        ServerHttpRequest serverHttpRequest = MockServerWebExchange.from(request).getRequest();

        String output = serialiser.serialise(joinPoint, "Log content", serverHttpRequest, null, Map.of("result", 1));
        assertThat(output.replace("\033", ""))
                .contains("SampleController#handle")
                .contains("foo");
        assertThat(output).doesNotContain("secret");
    }

    @Test
    void serialiseShouldHandleExceptionsGracefully() throws Exception {
        Method method = SampleController.class.getDeclaredMethod("handleWithoutRequest", String.class, String.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getMethod()).thenReturn(method);
        when(signature.getParameterNames()).thenReturn(new String[]{"hidden", "visible"});
        when(signature.getDeclaringType()).thenReturn(SampleController.class);
        when(signature.getName()).thenReturn("handle");

        JoinPoint joinPoint = mock(JoinPoint.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"secret", "public"});

        String output = serialiser.serialise(joinPoint, "Log content", null,
                new IllegalArgumentException("invalid"), null);

        assertThat(output).contains("invalid");
        assertThat(output).contains("SampleController#handle");
    }

    private static final class SampleController {
        @SuppressWarnings("unused")
        public void handleWithRequest(@run.vexa.reactor.log.annotation.Log.Exclude String hidden, String visible) {
            // no-op
        }

        @SuppressWarnings("unused")
        public void handleWithoutRequest(@run.vexa.reactor.log.annotation.Log.Exclude String hidden, String visible) {
            // no-op
        }
    }
}
