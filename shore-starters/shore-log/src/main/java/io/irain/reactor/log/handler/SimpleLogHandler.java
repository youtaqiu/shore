package io.irain.reactor.log.handler;

import cn.hutool.json.JSONUtil;
import io.irain.reactor.core.util.OptionalBean;
import io.irain.reactor.log.annotation.Log;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

/**
 * @author youta
 **/
@Slf4j
public class SimpleLogHandler implements LogHandler {

    @Override
    public boolean accept(MethodSignature methodSignature, Log log) {
        return true;
    }

    @Override
    public Mono<Boolean> handler(String logContent, String requestMethod, String requestUri,
                                 String requestId, String traceId, String clientId, String ip,
                                 @Nullable Object operationParam,
                                 @Nullable Object result, @Nullable Throwable ex) {
        var logStr = "logContent: {}, requestMethod: {}, requestUri: {}, requestId: {}, ip: {}, traceId: {}, clientId: {}, operationParam: {}, result: {}";
        if (ex == null) {
            log.info(logStr, logContent, requestMethod, requestUri,
                    requestId, ip, traceId, clientId,
                    JSONUtil.toJsonStr(operationParam), JSONUtil.toJsonStr(result));
            return Mono.just(true);
        }
        logStr = logStr + ", ex: {}";
        log.info(logStr, logContent, requestMethod, requestUri,
                requestId, traceId, clientId,ip,
                JSONUtil.toJsonStr(operationParam), JSONUtil.toJsonStr(result),
                OptionalBean.ofNullable(ex).getBean(Throwable::getLocalizedMessage).orElse(null));
        return Mono.just(true);
    }

}
