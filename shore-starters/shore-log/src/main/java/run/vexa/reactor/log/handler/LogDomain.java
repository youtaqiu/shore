package run.vexa.reactor.log.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 日志实体.
 *
 * @author rained
 **/
@Builder
@AllArgsConstructor
@Data
@SuppressWarnings("unused")
public class LogDomain {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public LogDomain() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * 日志内容.
     */
    private String logContent;
    /**
     * 请求方法.
     */
    private String requestMethod;
    /**
     * 请求uri.
     */
    private String requestUri;
    /**
     * 请求id.
     */
    private String requestId;
    /**
     * 跟踪id.
     */
    private String traceId;
    /**
     * 客户端id.
     */
    private String clientId;
    /**
     * ip.
     */
    private String ip;
    /**
     * 查询参数.
     */
    private Object queryParams;
    /**
     * 操作参数.
     */
    private Object operationParam;
    /**
     * 响应.
     */
    private Object result;
    /**
     * 异常.
     */
    private Throwable ex;
}
