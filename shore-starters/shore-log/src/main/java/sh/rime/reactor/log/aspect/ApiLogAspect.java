package sh.rime.reactor.log.aspect;

import cn.hutool.core.util.StrUtil;
import org.springframework.util.MultiValueMap;
import sh.rime.reactor.core.context.ReactiveContextHolder;
import sh.rime.reactor.core.util.ReactiveAddrUtil;
import sh.rime.reactor.log.annotation.Log;
import sh.rime.reactor.log.handler.LogDomain;
import sh.rime.reactor.log.service.ApiLogService;
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

import java.util.*;

import static sh.rime.reactor.commons.constants.Constants.CLIENT_ID_HEADER;
import static sh.rime.reactor.commons.constants.Constants.REQUEST_ID_HEADER;


/**
 * Api log aspect.
 *
 * @author youta
 **/
@Aspect
@Slf4j
@Order(1)
public class ApiLogAspect {

    private final MessageSource messageSource;
    private final ApiLogService apiLogService;

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param messageSource the message source
     * @param apiLogService the api log service
     */
    public ApiLogAspect(MessageSource messageSource, ApiLogService apiLogService) {
        this.messageSource = messageSource;
        this.apiLogService = apiLogService;
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
                    mono = Mono.just("");
                    monoResult = logMonoResult(joinPoint, ReactiveContextHolder.getExchange()
                            .map(ServerWebExchange::getRequest)
                            .zipWith(mono), log, ex)
                            .then(Mono.error(ex));
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
                    Map<String, Object> queryParamMap = Map.of();
                    MultiValueMap<String, String> queryParams = request.getQueryParams();
                    if (!queryParams.isEmpty()) {
                        queryParamMap = buildParamMap(queryParams);
                    }
                    Collection<Object> values = queryParamMap.values();
                    List<Object> params = new ArrayList<>();
                    for (Object arg : args) {
                        if (!values.contains(arg)) {
                            params.add(arg);
                        }
                    }
                    String formattedLogContent = parseLogContent(logContent, args);
                    String remoteAddr = ReactiveAddrUtil.getRemoteAddr(request);
                    LogDomain logDomain = LogDomain.builder()
                            .logContent(formattedLogContent)
                            .requestMethod(method)
                            .requestUri(uri)
                            .requestId(requestId)
                            .clientId(clientId)
                            .ip(remoteAddr)
                            .queryParams(queryParamMap)
                            .operationParam(params)
                            .result(obj)
                            .ex(ex)
                            .build();
                    this.apiLogService.log(logDomain, methodSignature, apiLog);
                    return obj;
                });
    }

    /**
     * 解析日志内容
     *
     * @param logContent 日志内容
     * @param args       参数
     * @return 解析后的日志内容
     */
    private String parseLogContent(String logContent, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(logContent, args, logContent, locale);
        return message == null ? logContent : message;
    }

    /**
     * 构建参数
     *
     * @param parameterNames 参数名
     * @return 参数
     */
    private Map<String, Object> buildParamMap(MultiValueMap<String, String> parameterNames) {
        Map<String, Object> params = new HashMap<>(parameterNames.size());
        parameterNames.forEach((key, value) -> {
            if (value.size() == 1) {
                params.put(key, value.getFirst());
            } else {
                params.put(key, value);
            }
        });
        return params;
    }

}
