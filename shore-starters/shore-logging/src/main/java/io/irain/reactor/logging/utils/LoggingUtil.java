package io.irain.reactor.logging.utils;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.rolling.RollingPolicy;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.logback.RollingPolicySystemProperty;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * LoggingUtil：日志工具类
 * 包含日志文件的相关配置和操作
 */
public class LoggingUtil {
    /**
     * 默认日志文件夹
     */
    public static final String DEFAULT_LOG_DIR = "logs";
    /**
     * 所有日志文件名
     */
    public static final String LOG_FILE_ALL = "all.log";
    /**
     * 错误日志文件名
     */
    public static final String LOG_FILE_ERROR = "error.log";
    /**
     * 控制台输出 appender 名称
     */
    public static final String CONSOLE_APPENDER_NAME = "CONSOLE";
    /**
     * 文件输出 appender 名称
     */
    public static final String FILE_APPENDER_NAME = "FILE";
    /**
     * 错误日志文件输出 appender 名称
     */
    public static final String FILE_ERROR_APPENDER_NAME = "FILE_ERROR";
    /**
     * 默认文件日志输出格式
     */
    public static final String DEFAULT_FILE_LOG_PATTERN = "${FILE_LOG_PATTERN:%d{${LOG_DATEFORMAT_PATTERN:yyyy-MM-dd HH:mm:ss.SSS}} ${LOG_LEVEL_PATTERN:%5p} ${PID:} --- [%t] %-40.40logger{39} : %m%n${LOG_EXCEPTION_CONVERSION_WORD:%wEx}}";
    // 常量
    private static final String TRUE = "true";
    private static final String Y = "y";
    private static final String YES = "yes";
    private static final String ONE = "1";
    private static final String ON = "on";
    private static final String FALSE = "false";
    private static final String N = "n";
    private static final String NO = "no";
    private static final String OFF = "off";
    private static final String ZERO = "0";

    /**
     * 从根 logger 中移除指定 appender
     *
     * @param name appender 名称
     */
    public static void detachAppender(String name) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.getLogger(Logger.ROOT_LOGGER_NAME).detachAppender(name);
    }

    /**
     * 获取 rollingPolicy
     *
     * @param context      logger 上下文
     * @param appender     文件 appender
     * @param logErrorFile 错误日志文件名
     * @return rollingPolicy
     */
    public static RollingPolicy rollingPolicy(LoggerContext context,
                                              FileAppender<?> appender,
                                              String logErrorFile) {
        final SizeAndTimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new SizeAndTimeBasedRollingPolicy<>();
        rollingPolicy.setContext(context);
        rollingPolicy.setCleanHistoryOnStart(getPropToBool());
        rollingPolicy.setFileNamePattern(logErrorFile + ".%d{yyyy-MM-dd}.%i.gz");
        rollingPolicy.setMaxFileSize(FileSize.valueOf(getProp(RollingPolicySystemProperty.MAX_FILE_SIZE.getEnvironmentVariableName(), "10MB")));
        rollingPolicy.setMaxHistory(toInt(RollingPolicySystemProperty.MAX_HISTORY.getEnvironmentVariableName(), 7));
        rollingPolicy.setTotalSizeCap(FileSize.valueOf(getProp(RollingPolicySystemProperty.TOTAL_SIZE_CAP.getEnvironmentVariableName(), "0")));
        rollingPolicy.setParent(appender);
        rollingPolicy.start();
        return rollingPolicy;
    }

    /**
     * 获取指定 key 的配置项值，如果不存在则返回默认值
     *
     * @param key      配置项 key
     * @param defValue 默认值
     * @return 配置项值
     */
    public static String getProp(String key, String defValue) {
        return System.getProperty(key, defValue);
    }

    /**
     * 将对象转换为 int 类型，如果无法转换则返回默认值
     *
     * @param object       待转换对象
     * @param defaultValue 默认值
     * @return 转换结果
     */
    public static int toInt(@Nullable Object object, int defaultValue) {
        if (object instanceof Number number) {
            return number.intValue();
        }
        if (object instanceof CharSequence cs) {
            String value = cs.toString();
            try {
                return Integer.parseInt(value);
            } catch (final NumberFormatException nfe) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    /**
     * 将对象转换为 Boolean 类型，如果无法转换则返回默认值
     *
     * @param object       待转换对象
     * @param defaultValue 默认值
     * @return 转换结果
     */
    public static Boolean toBoolean(@Nullable Object object, @Nullable Boolean defaultValue) {
        if (object instanceof Boolean bool) {
            return bool;
        } else if (object instanceof CharSequence cs) {
            String value = cs.toString();
            if (TRUE.equalsIgnoreCase(value) ||
                    Y.equalsIgnoreCase(value) ||
                    YES.equalsIgnoreCase(value) ||
                    ON.equalsIgnoreCase(value) ||
                    ONE.equalsIgnoreCase(value)) {
                return true;
            } else if (FALSE.equalsIgnoreCase(value) ||
                    N.equalsIgnoreCase(value) ||
                    NO.equalsIgnoreCase(value) ||
                    OFF.equalsIgnoreCase(value) ||
                    ZERO.equalsIgnoreCase(value)) {
                return false;
            }
        }
        return defaultValue;
    }

    /**
     * 获取指定 key 的配置项值
     *
     * @param key 配置项 key
     * @return 配置项值
     */
    public static String getProp(String key) {
        return System.getProperty(key);
    }

    /**
     * 将指定 key 的配置项值转换为 boolean 类型
     *
     * @return 转换结果
     */
    private static boolean getPropToBool() {
        return Objects.requireNonNull(toBoolean(getProp(RollingPolicySystemProperty.CLEAN_HISTORY_ON_START.getEnvironmentVariableName()), false));
    }
}
