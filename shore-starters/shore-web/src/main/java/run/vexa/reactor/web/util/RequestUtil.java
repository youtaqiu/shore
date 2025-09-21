package run.vexa.reactor.web.util;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.function.server.ServerRequest;

/**
 * RequestUtil is a utility class that provides methods for parsing request paths.
 * It includes methods for parsing the request path from a ServerHttpRequest and a ServerRequest.
 *
 * @author youta
 */
@Slf4j
@SuppressWarnings("unused")
public class RequestUtil {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public RequestUtil() {
    }

    /**
     * 解析请求路径.
     *
     * @param request 请求对象
     * @return 请求路径
     */
    public static String parseRequestUri(ServerHttpRequest request) {
        return parseRequestUri0(request.getPath());
    }

    /**
     * 解析请求路径.
     *
     * @param request 请求对象
     * @return 请求路径
     */
    public static String parseRequestUri(ServerRequest request) {
        return parseRequestUri0(request.requestPath());
    }

    /**
     * 解析请求路径.
     *
     * @param requestPath 请求路径
     * @return 请求路径
     */
    private static String parseRequestUri0(RequestPath requestPath) {
        String contextPath = StrUtil.trimToNull(requestPath.contextPath().value());
        String requestUri = requestPath.value();

        if (contextPath != null) {
            String newRequestUri = requestUri.replaceFirst(contextPath, "");
            log.debug("replace requestUri[form={}, to={}]", requestUri, newRequestUri);
            requestUri = newRequestUri;
        }

        return requestUri;
    }

}
