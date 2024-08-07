package sh.rime.reactor.web.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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

    private static String parseRequestUri0(RequestPath requestPath) {
        String contextPath = StringUtils.trimToNull(requestPath.contextPath().value());
        String requestUri = requestPath.value();

        if (contextPath != null) {
            String newRequestUri = requestUri.replaceFirst(contextPath, "");
            log.debug("replace requestUri[form={}, to={}]", requestUri, newRequestUri);
            requestUri = newRequestUri;
        }

        return requestUri;
    }

}
