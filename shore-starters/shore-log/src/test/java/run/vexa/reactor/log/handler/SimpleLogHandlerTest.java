package run.vexa.reactor.log.handler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.vexa.reactor.log.annotation.Log;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SimpleLogHandlerTest {

    private final SimpleLogHandler handler = new SimpleLogHandler();
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(SimpleLogHandler.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        Logger logger = (Logger) org.slf4j.LoggerFactory.getLogger(SimpleLogHandler.class);
        logger.detachAppender(listAppender);
    }

    @Test
    void acceptShouldRespectLogAnnotationFlag() throws Exception {
        Method method = SampleClass.class.getDeclaredMethod("enabledMethod");
        Log enabledLog = method.getAnnotation(Log.class);
        Method disabled = SampleClass.class.getDeclaredMethod("disabledMethod");
        Log disabledLog = disabled.getAnnotation(Log.class);

        MethodSignature signature = mock(MethodSignature.class);
        assertThat(handler.accept(signature, enabledLog)).isTrue();
        assertThat(handler.accept(signature, disabledLog)).isFalse();
    }

    @Test
    void handlerShouldLogInformationWhenNoException() {
        LogDomain logDomain = LogDomain.builder()
                .logContent("operation")
                .requestMethod("GET")
                .requestUri("/api")
                .requestId("req-1")
                .ip("127.0.0.1")
                .traceId("trace")
                .queryParams(new Object[]{"a", 1})
                .operationParam(new Object[]{"b", 2})
                .result("ok")
                .build();

        Boolean result = handler.handler(logDomain).block();

        assertThat(result).isTrue();
        assertThat(listAppender.list)
                .hasSize(1)
                .first()
                .satisfies(event -> {
                    assertThat(event.getLevel()).isEqualTo(Level.INFO);
                    assertThat(event.getFormattedMessage()).contains("operation")
                            .contains("GET")
                            .contains("/api")
                            .contains("ok");
                });
    }

    @Test
    void handlerShouldIncludeExceptionDetails() {
        Throwable ex = new IllegalStateException("boom");
        LogDomain logDomain = LogDomain.builder()
                .logContent("operation")
                .requestMethod("POST")
                .requestUri("/api")
                .requestId("req-2")
                .ip("192.168.0.1")
                .traceId("trace-2")
                .operationParam(new Object[]{"payload"})
                .result("failure")
                .ex(ex)
                .build();

        Boolean result = handler.handler(logDomain).block();

        assertThat(result).isTrue();
        assertThat(listAppender.list)
                .hasSize(1)
                .first()
                .satisfies(event -> assertThat(event.getFormattedMessage()).contains("boom"));
    }

    private static final class SampleClass {
        @Log("enabled")
        private void enabledMethod() {
            // no-op
        }

        @Log(value = "disabled", enable = false)
        private void disabledMethod() {
            // no-op
        }
    }
}
