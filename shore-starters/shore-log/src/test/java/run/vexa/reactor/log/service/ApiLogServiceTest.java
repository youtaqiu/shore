package run.vexa.reactor.log.service;

import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import reactor.core.publisher.Mono;
import run.vexa.reactor.log.annotation.Log;
import run.vexa.reactor.log.handler.LogDomain;
import run.vexa.reactor.log.handler.LogHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ApiLogServiceTest {

    @Test
    void shouldInvokeAcceptedHandlers() throws Exception {
        LogHandler accepted = mock(LogHandler.class);
        when(accepted.accept(Mockito.any(), Mockito.any())).thenReturn(true);
        when(accepted.handler(Mockito.any())).thenReturn(Mono.just(true));

        LogHandler rejected = mock(LogHandler.class);
        when(rejected.accept(Mockito.any(), Mockito.any())).thenReturn(false);

        ObjectProvider<LogHandler> provider = objectProvider(accepted, rejected);
        ApiLogService service = new ApiLogService(provider);

        LogDomain domain = LogDomain.builder()
                .requestId("req-123")
                .build();

        Method method = SampleClass.class.getDeclaredMethod("sample", String.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getMethod()).thenReturn(method);

        Log logAnnotation = method.getAnnotation(Log.class);

        service.log(domain, signature, logAnnotation);

        verify(accepted).handler(domain);
        verify(rejected, never()).handler(Mockito.any());
        assertThat(domain.getTraceId()).isNotNull();
    }

    @Test
    void shouldHandleHandlerErrorsGracefully() throws Exception {
        LogHandler handler = mock(LogHandler.class);
        when(handler.accept(Mockito.any(), Mockito.any())).thenReturn(true);
        when(handler.handler(Mockito.any())).thenReturn(Mono.error(new IllegalStateException("boom")));

        ObjectProvider<LogHandler> provider = objectProvider(handler);
        ApiLogService service = new ApiLogService(provider);

        LogDomain domain = LogDomain.builder().build();
        Method method = SampleClass.class.getDeclaredMethod("sample", String.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getMethod()).thenReturn(method);
        Log logAnnotation = method.getAnnotation(Log.class);

        service.log(domain, signature, logAnnotation);

        verify(handler).handler(domain);
    }

    private ObjectProvider<LogHandler> objectProvider(LogHandler... handlers) {
        return new ObjectProvider<>() {
            private final List<LogHandler> list = List.of(handlers);

            @Override
            public LogHandler getObject(Object... args) {
                return list.get(0);
            }

            @Override
            public LogHandler getIfAvailable() {
                return list.isEmpty() ? null : list.get(0);
            }

            @Override
            public LogHandler getIfUnique() {
                return list.size() == 1 ? list.get(0) : null;
            }

            @Override
            public Stream<LogHandler> stream() {
                return list.stream();
            }

            @Override
            public Stream<LogHandler> orderedStream() {
                return list.stream();
            }
        };
    }

    private static final class SampleClass {
        @Log("sample")
        private void sample(String input) {
            // no-op
        }
    }
}
