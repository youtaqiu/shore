package run.vexa.reactor.log.handler;

import run.vexa.reactor.log.annotation.Log;
import org.aspectj.lang.reflect.MethodSignature;
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
     * @param logDomain 日志实体
     * @return 处理结果
     */
    Mono<Boolean> handler(LogDomain logDomain);

}
