package me.youm.reactor.common.constants;

import lombok.experimental.UtilityClass;

/**
 * @author youta
 */
@UtilityClass
public class ShoreConstant {

    /**
     * Spring 应用名 prop key
     */
    public static final String SPRING_APP_NAME_KEY = "spring.application.name";


    /**
     * 默认为空消息
     */
    public static final String DEFAULT_NULL_MESSAGE = "承载数据为空";
    /**
     * 默认成功消息
     */
    public static final String DEFAULT_SUCCESS_MESSAGE = "处理成功";
    /**
     * 默认失败消息
     */
    public static final String DEFAULT_FAIL_MESSAGE = "处理失败";

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 微服务之间传递的唯一标识
     */
    public static final String SHORE_TRACE_ID = "shore-trace-id";

    /**
     * 日志链路追踪id日志标志
     */
    public static final String LOG_TRACE_ID = "traceId";

    /**
     * Java默认临时目录
     */
    public static final String JAVA_TEMP_DIR = "java.io.tmpdir";

    /**
     * 版本
     */
    public static final String VERSION = "version";

    /**
     * 默认版本号
     */
    public static final String DEFAULT_VERSION = "v1";

    /**
     * json类型报文，UTF-8字符集
     */
    public static final String JSON_UTF8 = "application/json;charset=UTF-8";


    public static final String ASC = "asc";

    public static final String DESC = "desc";

    public final static  String REDIS_CONNECTION_PREFIX = "redis://";

    public final static  String CLIENT_ID = "Client-Id";

}
