package run.vexa.reactor.commons.constants;

/**
 * Constants is a utility class that provides a centralized location for constants used throughout the application.
 * It includes constants for token types, current login user, current operator, login user key, request id header, client id header,
 * API version header, API key header, API sign header, API app id header, API timestamp header, API nonce header, load balance prefix,
 * properties policy, trace id, default version, API sign, API app id, API timestamp, and API nonce.
 * These constants are used in various parts of the application to ensure consistency and avoid hard-coding of values.
 *
 * @author youta
 */
@SuppressWarnings("unused")
public final class Constants {

    /**
     * 私有构造函数
     */
    private Constants() {
    }

    /**
     * token类型
     */
    public static final String TOKEN_TYPE = "Bearer ";

    /**
     * token类型
     */
    public static final String LOWER_CASE_TOKEN_TYPE = "bearer";

    /**
     * 当前登录用户
     */
    public static final String CURRENT_LOGIN_USER = "currentLoginUser";

    /**
     * 当前操作者
     */
    public static final String CURRENT_OPERATOR = "currentOperator";

    /**
     * 当前操作者请求头key
     */
    public static final String CURRENT_OPERATOR_HEADER = "X-Current-Operator";

    /**
     * 登录用户key
     */
    public static final String USER_SESSION_ID = "login-user";

    /**
     * 请求id请求头key
     */
    public static final String REQUEST_ID_HEADER = "X-Request-Id";

    /**
     * 客户端id请求头key
     */
    public static final String CLIENT_ID_HEADER = "X-Client-Id";

    /**
     * 服务版本号请求头key
     */
    public static final String API_VERSION_HEADER = "X-Api-Version";

    /**
     * 接口签名请求头key
     */
    public static final String API_KEY_HEADER = "X-Api-Key";

    /**
     * 接口签名请求头key
     */
    public static final String API_SIGN_HEADER = "Api-Sign";

    /**
     * 接口应用id请求头key
     */
    public static final String API_APP_ID_HEADER = "Api-App-Id";

    /**
     * 接口时间戳请求头key
     */
    public static final String API_TIMESTAMP_HEADER = "Api-Timestamp";

    /**
     * 接口随机数请求头key
     */
    public static final String API_NONCE_HEADER = "Api-Nonce";

    /**
     * 负载均衡前缀
     */
    public static final String LB_PREFIX = "shore.loadbalancer";

    /**
     * 策略key
     */
    public static final String PROPERTIES_POLICY = "policy";

    /**
     * traceId
     */
    public static final String TRACE_ID = "trace_id";

    /**
     * 默认版本号
     */
    public static final String DEFAULT_VERSION = "v1";

    /**
     * 接口签名请求头key
     */
    public static final String API_SIGN = "sign";

    /**
     * 接口应用id请求头key
     */
    public static final String API_APP_ID = "appId";

    /**
     * 接口时间戳请求头key
     */
    public static final String API_TIMESTAMP = "timestamp";

    /**
     * 接口随机数请求头key
     */
    public static final String API_NONCE = "nonce";

}
