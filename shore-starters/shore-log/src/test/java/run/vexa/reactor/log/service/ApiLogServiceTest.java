package run.vexa.reactor.log.service;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.TraceFlags;
import io.opentelemetry.api.trace.TraceState;
import io.opentelemetry.context.Scope;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import reactor.core.publisher.Mono;
import run.vexa.reactor.log.annotation.Log;
import run.vexa.reactor.log.handler.LogDomain;
import run.vexa.reactor.log.handler.LogHandler;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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

        // This will trigger logHandler internally
        service.log(domain, signature, logAnnotation);

        verify(handler).handler(domain);
        // Verify traceId was set
        assertThat(domain.getTraceId()).isNotNull();
    }

    @Test
    void shouldPopulateTraceIdFromCurrentSpan() throws Exception {
        String traceId = "0123456789abcdef0123456789abcdef";
        SpanContext context = SpanContext.create(traceId, "0123456789abcdef", TraceFlags.getSampled(), TraceState.getDefault());
        Span span = Span.wrap(context);

        LogHandler handler = mock(LogHandler.class);
        when(handler.accept(Mockito.any(), Mockito.any())).thenReturn(true);
        when(handler.handler(Mockito.any())).thenReturn(Mono.just(true));

        ApiLogService service = new ApiLogService(objectProvider(handler));
        LogDomain domain = LogDomain.builder().build();
        Method method = SampleClass.class.getDeclaredMethod("sample", String.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getMethod()).thenReturn(method);
        Log logAnnotation = method.getAnnotation(Log.class);

        try (Scope ignored = span.makeCurrent()) {
            service.log(domain, signature, logAnnotation);
        }

        assertThat(domain.getTraceId()).isEqualTo(traceId);
    }

    @Test
    void shouldCatchThrownExceptionsFromHandler() throws Exception {
        LogHandler handler = mock(LogHandler.class);
        when(handler.accept(Mockito.any(), Mockito.any())).thenReturn(true);
        when(handler.handler(Mockito.any())).thenThrow(new IllegalArgumentException("fail"));

        ApiLogService service = new ApiLogService(objectProvider(handler));
        LogDomain domain = LogDomain.builder().build();
        Method method = SampleClass.class.getDeclaredMethod("sample", String.class);
        MethodSignature signature = mock(MethodSignature.class);
        when(signature.getMethod()).thenReturn(method);
        Log logAnnotation = method.getAnnotation(Log.class);

        service.log(domain, signature, logAnnotation);

        verify(handler).handler(domain);
        assertThat(domain.getTraceId()).isNotNull();
    }

    private ObjectProvider<LogHandler> objectProvider(LogHandler... handlers) {
        return new ObjectProvider<>() {
            private final List<LogHandler> list = List.of(handlers);

            @Override
            @NonNull
            public LogHandler getObject(@NonNull Object... args) {
                return list.getFirst();
            }

            @Override
            public LogHandler getIfAvailable() {
                return list.isEmpty() ? null : list.getFirst();
            }

            @Override
            public LogHandler getIfUnique() {
                return list.size() == 1 ? list.getFirst() : null;
            }

            @Override
            @NonNull
            public Stream<LogHandler> stream() {
                return list.stream();
            }

            @Override
            @NonNull
            public Stream<LogHandler> orderedStream() {
                return list.stream();
            }
        };
    }

    private static final class SampleClass {
        @Log("sample")
        @SuppressWarnings("unused")
        private void sample(String input) {
            // no-op
        }
    }
}
