package run.vexa.reactor.commons.constants;

/**
 * Keys is a utility class that provides a centralized location for error messages used throughout the application.
 * It includes constants for default error, unsupported HTTP request method, method argument type mismatch, missing request parameter,
 * no handler found, and required request body is missing.
 * These constants are used in various parts of the application to ensure consistency and avoid hard-coding of error messages.
 *
 * @author youta
 */
@SuppressWarnings("unused")
public final class Keys {

    /**
     * 默认错误.
     */
    public static final String DEFAULT = "default error";

    /**
     * 不支持的HTTP请求方式.
     */
    public static final String HTTP_REQUEST_METHOD_NOT_SUPPORTED = "Http request method not supported";

    /**
     * 方法参数不匹配.
     */
    public static final String METHOD_ARGUMENT_TYPE_MISMATCH = "Method argument type mismatch";

    /**
     * 方法参数不匹配.
     */
    public static final String METHOD_ARGUMENT_TYPE_MISMATCH_WITHOUT_TYPE = "Method argument type mismatch without type";

    /**
     * 缺失请求参数.
     */
    public static final String MISSING_REQUEST_PARAMETER = "Missing request parameter";

    /**
     * 未发现处理程序(404).
     */
    public static final String NO_HANDLER_FOUND = "No handler found";

    /**
     * 必要的请求body缺失.
     */
    public static final String HTTP_MESSAGE_NOT_READABLE = "Required request body is missing";

    /**
     * 私有构造函数
     */
    private Keys() {
    }

}
