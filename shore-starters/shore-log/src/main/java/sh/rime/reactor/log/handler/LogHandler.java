package sh.rime.reactor.log.handler;

import sh.rime.reactor.log.annotation.Log;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Mono;

/**
 * 日志处理器.
 *
 * @author youta
 **/
public interface LogHandler {

    /**
     * 判别是否处理日志.
     *
     * @param methodSignature 切点
     * @param log             日志注解
     * @return 处理结果
     */
    boolean accept(MethodSignature methodSignature, Log log);

    /**
     * 处理日志.
     *
     * @param logContent     日志内容
     * @param requestMethod  请求方法
     * @param requestUri     请求uri
     * @param requestId      请求id
     * @param traceId        跟踪id
     * @param clientId       客户端id
     * @param ip             ip
     * @param operationParam 操作参数
     * @param result         响应
     * @param ex             异常
     * @return 处理结果
     */
    Mono<Boolean> handler(String logContent, String requestMethod, String requestUri,
                          String requestId, String traceId, String clientId, String ip,
                          @Nullable Object operationParam,
                          @Nullable Object result, @Nullable Throwable ex);

}
