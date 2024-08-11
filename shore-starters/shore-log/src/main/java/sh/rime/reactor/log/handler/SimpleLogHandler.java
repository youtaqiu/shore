package sh.rime.reactor.log.handler;

import cn.hutool.json.JSONUtil;
import sh.rime.reactor.core.util.OptionalBean;
import sh.rime.reactor.log.annotation.Log;
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
    }

    @Override
    public boolean accept(MethodSignature methodSignature, Log log) {
        return log.enable();
    }

    @Override
    public Mono<Boolean> handler(LogDomain logDomain) {
        var logStr = "logContent: {}, requestMethod: {}, requestUri: {}, requestId: {}, ip: {}, "
                + "traceId: {}, clientId: {}, queryParams: {}, operationParam: {}, result: {}";
        Throwable ex = logDomain.getEx();
        var logContent = logDomain.getLogContent();
        var requestMethod = logDomain.getRequestMethod();
        var requestUri = logDomain.getRequestUri();
        var requestId = logDomain.getRequestId();
        var ip = logDomain.getIp();
        var traceId = logDomain.getTraceId();
        var clientId = logDomain.getClientId();
        var queryParams = logDomain.getQueryParams();
        var operationParam = logDomain.getOperationParam();
        var result = logDomain.getResult();
        if (ex == null) {
            log.info(logStr, logContent, requestMethod, requestUri,
                    requestId, ip, traceId, clientId, JSONUtil.toJsonStr(queryParams),
                    JSONUtil.toJsonStr(operationParam), JSONUtil.toJsonStr(result));
            return Mono.just(true);
        }
        logStr = logStr + ", ex: {}";
        log.info(logStr, logContent, requestMethod, requestUri,
                requestId, traceId, clientId, ip,
                JSONUtil.toJsonStr(operationParam), JSONUtil.toJsonStr(result),
                OptionalBean.ofNullable(ex).getBean(Throwable::getLocalizedMessage).orElse(null));
        return Mono.just(true);
    }

}
