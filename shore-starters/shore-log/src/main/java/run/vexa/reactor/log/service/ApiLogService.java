package run.vexa.reactor.log.service;

import run.vexa.reactor.log.annotation.Log;
import run.vexa.reactor.log.handler.LogDomain;
import run.vexa.reactor.log.handler.LogHandler;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


/**
 * Api log service.
 *
 * @author youta
 **/
@Component
@Slf4j
public class ApiLogService {

    private final ObjectProvider<LogHandler> logHandlersProvider;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param logHandlersProvider the log handlers provider
     */
    public ApiLogService(ObjectProvider<LogHandler> logHandlersProvider) {
        this.logHandlersProvider = logHandlersProvider;
    }

    /**
     * 处理日志
     *
     * @param logDomain       日志内容
     * @param methodSignature 切点
     * @param apiLog          日志注解
     */
    @Async
    public void log(LogDomain logDomain, MethodSignature methodSignature, Log apiLog) {
        logHandlersProvider.stream().filter(handler -> handler.accept(methodSignature, apiLog))
                .forEach(handler -> logHandler(logDomain, handler));
    }

    /**
     * 处理日志
     *
     * @param logDomain 日志内容
     * @param handler   日志处理器
     */
    private void logHandler(LogDomain logDomain, LogHandler handler) {
        try {
            String traceId = this.getTraceId();
            logDomain.setTraceId(traceId);
            handler.handler(logDomain).subscribe();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

    /**
     * 获取traceId
     *
     * @return traceId
     */
    private String getTraceId() {
        Span span = Span.current();
        String traceId = "";
        if (span != null) {
            SpanContext spanContext = span.getSpanContext();
            traceId = spanContext.getTraceId();
        }
        return traceId;
    }
}
