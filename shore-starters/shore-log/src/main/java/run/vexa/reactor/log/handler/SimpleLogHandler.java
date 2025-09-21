package run.vexa.reactor.log.handler;

import cn.hutool.json.JSONUtil;
import run.vexa.reactor.core.util.OptionalBean;
import run.vexa.reactor.log.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import reactor.core.publisher.Mono;

/**
 * Simple log handler.
 *
 * @author youta
 **/
@Slf4j
public class SimpleLogHandler implements LogHandler {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public SimpleLogHandler() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    @Override
    public boolean accept(MethodSignature methodSignature, Log log) {
        return log.enable();
    }

    @Override
    public Mono<Boolean> handler(LogDomain logDomain) {
        var logStr = "logContent: {}, requestMethod: {}, requestUri: {}, requestId: {}, ip: {}, "
                + "traceId: {}, queryParams: {}, operationParam: {}, result: {}";
        Throwable ex = logDomain.getEx();
        var logContent = logDomain.getLogContent();
        var requestMethod = logDomain.getRequestMethod();
        var requestUri = logDomain.getRequestUri();
        var requestId = logDomain.getRequestId();
        var ip = logDomain.getIp();
        var traceId = logDomain.getTraceId();
        var queryParams = logDomain.getQueryParams();
        var operationParam = logDomain.getOperationParam();
        var result = logDomain.getResult();
        if (ex == null) {
            log.info(logStr, logContent, requestMethod, requestUri,
                    requestId, ip, traceId, JSONUtil.toJsonStr(queryParams),
                    JSONUtil.toJsonStr(operationParam), JSONUtil.toJsonStr(result));
            return Mono.just(true);
        }
        logStr = logStr + ", ex: {}";
        log.info(logStr, logContent, requestMethod, requestUri,
                requestId, traceId, ip,
                JSONUtil.toJsonStr(operationParam), JSONUtil.toJsonStr(result),
                OptionalBean.ofNullable(ex).getBean(Throwable::getLocalizedMessage).orElse(null));
        return Mono.just(true);
    }

}
