package sh.rime.reactor.log.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import sh.rime.reactor.core.util.ReactiveAddrUtil;
import sh.rime.reactor.log.annotation.Log;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Serialises a join point.
 *
 * @author rained
 */
public final class JoinPointSerialise {

    /**
     * The cache of logged methods.
     */
    private static final Map<UniqueMethodSignature, LoggedMethod> CACHE = new ConcurrentHashMap<>();

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public JoinPointSerialise() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * Serialises the given join point.
     *
     * @param joinPoint The join point to serialise.
     * @param logContent The content of the log.
     * @param serverHttpRequest The request.
     * @param ex The exception.
     * @param result The result.
     * @return The serialised join point.
     */
    public String serialise(JoinPoint joinPoint, String logContent, ServerHttpRequest serverHttpRequest,
                            Throwable ex, Object result) {

        // ANSI 转义序列
        final String RESET = "\033[0m";        // 重置颜色
        final String CYAN = "\033[36m";        // 青色
        final String YELLOW = "\033[33m";     // 黄色
        final String GREEN = "\033[32m";      // 绿色
        final String RED = "\033[31m";        // 红色
        final String MAGENTA = "\033[35m";    // 紫色

        // 构建基础信息
        var loggedMethod = getLoggedMethod(joinPoint, logContent, serverHttpRequest);
        StringBuilder output = new StringBuilder();

        // 标题
        output.append(CYAN).append("\n===== Log Entry Start =====\n").append(RESET);

        // 日志内容
        output.append(YELLOW).append("Logged Content    : ").append(RESET)
                .append(loggedMethod.logContent()).append("\n")
                .append(YELLOW).append("Method            : ").append(RESET)
                .append(loggedMethod.className()).append("#").append(loggedMethod.methodName()).append("\n");

        if (serverHttpRequest != null) {
            output.append(YELLOW).append("Request URI       : ").append(RESET)
                    .append(loggedMethod.requestUri()).append("\n")
                    .append(YELLOW).append("Request Real IP   : ").append(RESET)
                    .append(loggedMethod.remoteAddr()).append("\n");
        }

        // 参数部分
        if (loggedMethod.params() != null && !loggedMethod.params().isEmpty()) {
            output.append(GREEN).append("Parameters        : ").append(RESET).append("\n");

            // 遍历 Map 并格式化每个参数项
            for (Map.Entry<String, Object> entry : loggedMethod.params().entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();

                // 格式化值：如果是复杂对象，转换为 JSON 字符串（可以自定义格式化方式）
                String formattedValue = formatAsJson(value);

                // 输出每个参数
                output.append("  ").append(YELLOW).append(key).append(RESET)
                        .append(" = ").append(MAGENTA).append(formattedValue).append(RESET).append("\n");
            }
        }

        // 查询参数
        if (!loggedMethod.queryParamMap().isEmpty()) {
            output.append(GREEN).append("Query Parameters  : ").append(RESET)
                    .append(loggedMethod.queryParamMap()).append("\n");
        }

        // 异常或结果
        if (ex != null) {
            output.append(RED).append("Exception         : ").append(RESET)
                    .append(ex.getMessage()).append("\n");
        } else if (result != null) {
            output.append(MAGENTA).append("Result            : ").append(RESET)
                    .append(formatAsJson(result)).append("\n");
        }

        // 尾部
        output.append(CYAN).append("===== Log Entry End =====").append(RESET);
        return output.toString();
    }


    // 使用 Jackson 将对象格式化为漂亮的 JSON
    private String formatAsJson(Object object) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            return "Error serializing result: " + e.getMessage();
        }
    }


    /**
     * Gets the logged method for the given join point.
     *
     * @param joinPoint The join point.
     * @return The logged method.
     */
    private LoggedMethod getLoggedMethod(JoinPoint joinPoint, String logContent, ServerHttpRequest serverHttpRequest) {
        var methodSignature = (MethodSignature) joinPoint.getSignature();
        Object[] args = joinPoint.getArgs();
        var uniqueMethodSignature = new UniqueMethodSignature(methodSignature);
        return CACHE.computeIfAbsent(uniqueMethodSignature, key ->
                this.buildLoggedMethod(key, logContent, serverHttpRequest, args));
    }

    /**
     * Builds a logged method for the given unique method signature.
     *
     * @param uniqueMethodSignature The unique method signature.
     * @param request               The request.
     * @return The logged method.
     */
    private LoggedMethod buildLoggedMethod(UniqueMethodSignature uniqueMethodSignature,
                                           String logContent, ServerHttpRequest request, Object[] args) {
        var methodSignature = uniqueMethodSignature.getMethodSignature();
        var method = methodSignature.getMethod();
        var className = method.getDeclaringClass().getName();
        var methodName = method.getName();
        var parameterNames = methodSignature.getParameterNames();
        var includedParameterIndexes = getIncludedParameterIndexes(method);
        Map<String, Object> parameterMap = this.buildParameterMap(parameterNames, args, includedParameterIndexes);
        if (request == null) {
            return new LoggedMethod(logContent, "", className, methodName, parameterMap, "", Map.of());
        }
        var uri = request.getPath().value();
        String remoteAddr = ReactiveAddrUtil.getRemoteAddr(request);
        Map<String, Object> queryParamMap = Map.of();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        if (!queryParams.isEmpty()) {
            queryParamMap = buildParamMap(queryParams);
        }
        return new LoggedMethod(logContent, uri, className, methodName, parameterMap, remoteAddr, queryParamMap);
    }

    /**
     * Builds a parameter map.
     *
     * @param parameterNames The parameter names.
     * @param parameterValues The parameter values.
     * @param includedIndexes The indexes of the parameters that should be included in the log.
     * @return The parameter map.
     */
    private Map<String, Object> buildParameterMap(String[] parameterNames, Object[] parameterValues, Set<Integer> includedIndexes) {
        Map<String, Object> paramMap = new HashMap<>();

        if (parameterNames != null && parameterValues != null && includedIndexes != null) {
            int length = Math.min(parameterNames.length, parameterValues.length);
            for (int i = 0; i < length; i++) {
                if (includedIndexes.contains(i)) {
                    paramMap.put(parameterNames[i], parameterValues[i]);
                }
            }
        }

        return paramMap;
    }

    /**
     * build parameter map
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

    /**
     * Gets the indexes of the parameters of the given method that should be included in the log.
     *
     * @param method The method.
     * @return The indexes of the parameters that should be included in the log.
     */
    private Set<Integer> getIncludedParameterIndexes(Method method) {
        var parameterAnnotations = method.getParameterAnnotations();
        var includedParameterIndexes = new HashSet<Integer>();
        for (int i = 0, n = parameterAnnotations.length; i < n; i++) {
            var annotations = parameterAnnotations[i];
            var log = Stream.of(annotations)
                    .map(Annotation::annotationType)
                    .noneMatch(Log.Exclude.class::equals);
            if (log) {
                includedParameterIndexes.add(i);
            }
        }
        return includedParameterIndexes;
    }
}

