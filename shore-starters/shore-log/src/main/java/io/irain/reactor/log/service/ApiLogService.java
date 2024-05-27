package io.irain.reactor.log.service;

import io.irain.reactor.log.annotation.Log;
import io.irain.reactor.log.handler.LogHandler;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author youta
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiLogService {

    private final ObjectProvider<LogHandler> logHandlersProvider;

    /**
     * 处理日志
     *
     * @param content         日志内容
     * @param method          请求方法
     * @param uri             请求uri
     * @param params          请求参数
     * @param requestId       请求id
     * @param clientId        客户端id
     * @param remoteAddr      ip
     * @param obj             操作参数
     * @param ex              异常
     * @param methodSignature 切点
     * @param apiLog          日志注解
     */
    @Async
    public void log(String content, String method, String uri, Map<String, Object> params, String requestId, String clientId, String remoteAddr,
                    @Nullable Object obj, @Nullable Throwable ex, MethodSignature methodSignature, Log apiLog) {
        logHandlersProvider.stream().filter(handler -> handler.accept(methodSignature, apiLog))
                .forEach(handler -> logHandler(content, method, uri, params, requestId, clientId, remoteAddr, obj, ex, handler));
    }

    /**
     * 处理日志
     *
     * @param logContent 日志内容
     * @param method     请求方法
     * @param uri        请求uri
     * @param params     请求参数
     * @param requestId  请求id
     * @param clientId   客户端id
     * @param ip         ip
     * @param result     响应
     * @param ex         异常
     * @param handler    日志处理器
     */
    private void logHandler(String logContent, String method, String uri, Map<String, Object> params, String requestId, String clientId, String ip,
                            @Nullable Object result, @Nullable Throwable ex, LogHandler handler) {
        try {
            handler.handler(logContent, method, uri, requestId, getTraceId(), clientId, ip, params, result, ex).subscribe();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage(), e);
        }
    }

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
