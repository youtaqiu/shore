package run.vexa.reactor.logging.appender;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import io.opentelemetry.instrumentation.logback.appender.v1_0.OpenTelemetryAppender;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import run.vexa.reactor.logging.properties.LoggingProperties;

/**
 * OpenTelemetry 日志输出器
 *
 * @author rained
 **/
@Slf4j
public class LoggingOpenTelemetryAppender implements ILoggingAppender {
    private static final String APPENDER_NAME = "OpenTelemetry";
    private final LoggingProperties properties;

    /**
     * 构造器
     *
     * @param properties properties
     */
    public LoggingOpenTelemetryAppender(LoggingProperties properties) {
        this.properties = properties;
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.start(context);
    }

    /**
     * 重新加载
     *
     * @param context context
     */
    @Override
    public void reload(LoggerContext context) {
        LoggingProperties.OpenTelemetry openTelemetry = properties.getOtel();
        if (openTelemetry.isEnabled()) {
            addOpenTelemetryAppender(context);
        }
    }

    /**
     * 添加 OpenTelemetry Appender
     *
     * @param context context
     */
    private void addOpenTelemetryAppender(LoggerContext context) {
        OpenTelemetryAppender openTelemetryAppender = new OpenTelemetryAppender();
        openTelemetryAppender.setName(APPENDER_NAME);
        openTelemetryAppender.setContext(context);
        openTelemetryAppender.start();
        // 先删除，再添加
        context.getLogger(Logger.ROOT_LOGGER_NAME).detachAppender(APPENDER_NAME);
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(openTelemetryAppender);
    }

}
