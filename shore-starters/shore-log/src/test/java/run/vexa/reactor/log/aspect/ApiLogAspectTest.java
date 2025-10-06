package run.vexa.reactor.log.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import run.vexa.reactor.core.context.ReactiveContextHolder;
import run.vexa.reactor.log.annotation.Log;

import java.lang.reflect.Method;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiLogAspectTest {

    @Test
    void handlerShouldLogMonoResult() throws Throwable {
        JoinPointSerialise serialiser = mock(JoinPointSerialise.class);
        when(serialiser.serialise(any(), any(), any(), any(), any())).thenReturn("serialised");
        Logger logger = mock(Logger.class);
        Function<Class<?>, Logger> loggerGetter = clazz -> logger;
        ApiLogAspect aspect = new ApiLogAspect(serialiser, loggerGetter);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Method method = SampleService.class.getDeclaredMethod("mono", String.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getDeclaringType()).thenReturn(SampleService.class);
        when(signature.getMethod()).thenReturn(method);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"value"});
        when(joinPoint.proceed()).thenReturn(Mono.just("ok"));

        Log logAnnotation = method.getAnnotation(Log.class);

        Object result = aspect.handler(joinPoint, logAnnotation);
        assertThat(result).isInstanceOf(Mono.class);

        MockServerHttpRequest request = MockServerHttpRequest.get("/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<String> response = ((Mono<?>) result)
                .map(String.class::cast)
                .contextWrite(context -> context.put(ReactiveContextHolder.CONTEXT_KEY, exchange));

        StepVerifier.create(response)
                .expectNext("ok")
                .verifyComplete();

        verify(logger).info("serialised");
    }

    @Test
    void handlerShouldLogErrorsAndRethrow() throws Throwable {
        JoinPointSerialise serialiser = mock(JoinPointSerialise.class);
        when(serialiser.serialise(any(), any(), any(), any(), any())).thenReturn("error-log");
        Logger logger = mock(Logger.class);
        ApiLogAspect aspect = new ApiLogAspect(serialiser, clazz -> logger);

        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Method method = SampleService.class.getDeclaredMethod("mono", String.class);
        Signature signature = mock(Signature.class);
        when(signature.getDeclaringType()).thenReturn(SampleService.class);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.proceed()).thenThrow(new IllegalStateException("boom"));

        Log logAnnotation = method.getAnnotation(Log.class);

        assertThatThrownBy(() -> aspect.handler(joinPoint, logAnnotation))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("boom");

        verify(logger).error("error-log");
    }

    private static final class SampleService {
        @Log("mono")
        private Mono<String> mono(String value) {
            return Mono.just(value);
        }
    }
}
