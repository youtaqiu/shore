package run.vexa.reactor.log.aspect;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import run.vexa.reactor.core.context.ReactiveContextHolder;
import run.vexa.reactor.log.annotation.Log;

import java.util.function.Function;

import static lombok.AccessLevel.PACKAGE;


/**
 * Api log aspect.
 *
 * @author youta
 **/
@Aspect
@Slf4j
@Order(1)
@RequiredArgsConstructor(access = PACKAGE)
public class ApiLogAspect {

    private final JoinPointSerialise joinPointSerialise;
    private final Function<Class<?>, Logger> loggerGetter;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param joinPointSerialise the join point serialise
     */
    public ApiLogAspect(JoinPointSerialise joinPointSerialise) {
        this(joinPointSerialise, LoggerFactory::getLogger);
    }


    /**
     * 处理日志
     *
     * @param joinPoint 切点
     * @param log       注解
     * @return 返回值
     * @throws Throwable 异常
     */
    @Around("@annotation(log)")
    @SuppressWarnings("all")
    public Object handler(ProceedingJoinPoint joinPoint, Log log) throws Throwable {
        Object result = null;
        Throwable ex = null;
        Mono<?> monoResult = null;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            ex = e;
            throw e;
        } finally {
            long start = System.currentTimeMillis();
            if (result instanceof Mono<?> monoResultTemp) {
                monoResult = logMonoResult(joinPoint, ReactiveContextHolder.getExchange()
                        .map(ServerWebExchange::getRequest)
                        .zipWith(monoResultTemp), log, null);

            } else if (result instanceof Flux<?> fluxResult) {
                monoResult = logMonoResult(joinPoint, ReactiveContextHolder.getExchange()
                        .map(ServerWebExchange::getRequest)
                        .zipWith(fluxResult.collectList()), log, null);
            } else {
                Mono<Object> mono;
                if (ex != null) {
                    mono = Mono.error(ex);
                    monoResult = logMonoResult(joinPoint, ReactiveContextHolder.getExchange()
                            .map(ServerWebExchange::getRequest)
                            .zipWith(mono), log, ex);
                } else {
                    mono = Mono.justOrEmpty(result);
                    monoResult = logMonoResult(joinPoint, ReactiveContextHolder.getExchange()
                            .map(ServerWebExchange::getRequest)
                            .zipWith(mono), log, ex);
                }
            }
        }
        return monoResult;
    }

    /**
     * 记录日志
     *
     * @param joinPoint 切点
     * @param zipData   数据
     * @param apiLog    注解
     * @param ex        异常
     * @return 返回值
     */
    private Mono<?> logMonoResult(ProceedingJoinPoint joinPoint,
                                  Mono<? extends Tuple2<ServerHttpRequest, Object>> zipData,
                                  Log apiLog, Throwable ex) {
        Signature signature = joinPoint.getSignature();
        String logContent = StrUtil.trimToNull(apiLog.value());
        if (ex != null) {
            var serialisedJoinPoint = joinPointSerialise.serialise(joinPoint, logContent,
                    null, ex, null);
            var declaringType = signature.getDeclaringType();
            var logger = loggerGetter.apply(declaringType);
            logger.error(serialisedJoinPoint);
        }
        return zipData
                .map(data -> {
                    var request = data.getT1();
                    var obj = data.getT2();
                    if (!(signature instanceof MethodSignature)) {
                        return obj;
                    }
                    var serialisedJoinPoint = joinPointSerialise.serialise(joinPoint, logContent, request, ex, obj);
                    var declaringType = signature.getDeclaringType();
                    var logger = loggerGetter.apply(declaringType);
                    logger.info(serialisedJoinPoint);
                    return obj;
                });
    }

}
