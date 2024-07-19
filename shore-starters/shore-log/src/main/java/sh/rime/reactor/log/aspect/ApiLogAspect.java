package sh.rime.reactor.log.aspect;

import cn.hutool.core.util.StrUtil;
import sh.rime.reactor.core.context.ReactiveContextHolder;
import sh.rime.reactor.core.util.ReactiveAddrUtil;
import sh.rime.reactor.log.annotation.Log;
import sh.rime.reactor.log.service.ApiLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static sh.rime.reactor.commons.constants.Constants.CLIENT_ID_HEADER;
import static sh.rime.reactor.commons.constants.Constants.REQUEST_ID_HEADER;


/**
 * @author youta
 **/
@Aspect
@RequiredArgsConstructor
@Slf4j
@Order(1)
public class ApiLogAspect {

    private final MessageSource messageSource;
    private final ApiLogService apiLogService;

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
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            ex = e;
            throw e;
        } finally {
            long start = System.currentTimeMillis();
            if (result instanceof Mono<?> monoResult) {
                return logMonoResult(joinPoint, ReactiveContextHolder.getExchange()
                        .map(ServerWebExchange::getRequest)
                        .zipWith(monoResult), log, null);
            } else if (result instanceof Flux<?> fluxResult) {
                return logMonoResult(joinPoint, ReactiveContextHolder.getExchange()
                        .map(ServerWebExchange::getRequest)
                        .zipWith(fluxResult.collectList()), log, null);
            } else {
                Mono<Object> mono;
                if (ex != null) {
                    mono = Mono.just("");
                    return logMonoResult(joinPoint, ReactiveContextHolder.getExchange()
                            .map(ServerWebExchange::getRequest)
                            .zipWith(mono), log, ex)
                            .then(Mono.error(ex));
                } else {
                    mono = Mono.justOrEmpty(result);
                    return logMonoResult(joinPoint, ReactiveContextHolder.getExchange()
                            .map(ServerWebExchange::getRequest)
                            .zipWith(mono), log, ex);
                }
            }
        }

    }


    private Mono<?> logMonoResult(ProceedingJoinPoint joinPoint, Mono<? extends Tuple2<ServerHttpRequest, Object>> zipData, Log apiLog, Throwable ex) {
        Signature signature = joinPoint.getSignature();
        String logContent = StrUtil.trimToNull(apiLog.value());
        return zipData
                .map(data -> {
                    var request = data.getT1();
                    var obj = data.getT2();
                    var method = request.getMethod().name();
                    var uri = request.getPath().value();
                    HttpHeaders headers = request.getHeaders();
                    String requestId = headers.getFirst(REQUEST_ID_HEADER);
                    String clientId = headers.getFirst(CLIENT_ID_HEADER);

                    if (!(signature instanceof MethodSignature methodSignature)) {
                        return obj;
                    }
                    Object[] args = joinPoint.getArgs();
                    String[] parameterNames = methodSignature.getParameterNames();
                    if (!Objects.equals(args.length, parameterNames.length)) {
                        log.error("parameter length is {}, but args length is {}", parameterNames.length, args.length);
                        return obj;
                    }
                    String formattedLogContent = parseLogContent(logContent, args);
                    Map<String, Object> params = buildParamMap(args, parameterNames);
                    String remoteAddr = ReactiveAddrUtil.getRemoteAddr(request);
                    this.apiLogService.log(formattedLogContent, method, uri, params, requestId, clientId, remoteAddr, obj, ex, methodSignature, apiLog);
                    return obj;
                });
    }


    private String parseLogContent(String logContent, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(logContent, args, logContent, locale);
        return message == null ? logContent : message;
    }

    private Map<String, Object> buildParamMap(Object[] args, String[] parameterNames) {
        Map<String, Object> params = new HashMap<>(args.length);
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServerWebExchange) {
                continue;
            }
            params.put(parameterNames[i], args[i]);
        }
        return params;
    }


}
