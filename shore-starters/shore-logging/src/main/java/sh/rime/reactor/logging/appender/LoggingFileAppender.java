package sh.rime.reactor.logging.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.encoder.Encoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import sh.rime.reactor.logging.properties.LoggingProperties;
import sh.rime.reactor.logging.utils.LoggingUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.boot.logging.LoggingSystemProperty;
import org.springframework.core.env.Environment;

import java.nio.charset.Charset;

/**
 * 纯文件日志输出，all.log 和 error.log
 *
 * @author youta
 */
@Slf4j
public class LoggingFileAppender implements ILoggingAppender {
    private final LoggingProperties properties;
    private final String logAllFile;
    private final String logErrorFile;
    private static final char SLASH = '/';

    /**
     * 构造器
     *
     * @param environment environment
     * @param properties  properties
     */
    public LoggingFileAppender(Environment environment,
                               LoggingProperties properties) {
        this.properties = properties;
        // 1. 服务名和日志目录
        String appName = environment.getRequiredProperty("spring.application.name");
        // 2. 文件日志格式
        String fileLogPattern = environment.resolvePlaceholders(LoggingUtil.DEFAULT_FILE_LOG_PATTERN);
        System.setProperty(LoggingSystemProperty.FILE_PATTERN.getEnvironmentVariableName(), fileLogPattern);
        // 3. 生成日志文件的文件
        String logDir = environment.getProperty("logging.file.path", LoggingUtil.DEFAULT_LOG_DIR);
        this.logAllFile = logDir + SLASH + appName + SLASH + LoggingUtil.LOG_FILE_ALL;
        this.logErrorFile = logDir + SLASH + appName + SLASH + LoggingUtil.LOG_FILE_ERROR;
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.start(context);
    }

    @Override
    public void start(LoggerContext context) {
        log.info("File logging start.");
        reload(context);
    }

    @Override
    public void reset(LoggerContext context) {
        log.info("File logging reset.");
        reload(context);
    }

    /**
     * 重新加载
     *
     * @param context context
     */
    private void reload(LoggerContext context) {
        LoggingProperties.Files files = properties.getFiles();
        if (files.isEnabled() && !files.isUseJsonFormat()) {
            addAllFileAppender(context, logAllFile);
            addErrorFileAppender(context, logErrorFile);
        }
    }

    /**
     * addAllFileAppender
     *
     * @param context a {@link LoggerContext} object.
     * @param logFile a {@link String} object.
     */
    private static void addAllFileAppender(LoggerContext context, String logFile) {
        RollingFileAppender<ILoggingEvent> allFileAppender = new RollingFileAppender<>();
        allFileAppender.setContext(context);
        allFileAppender.setEncoder(patternLayoutEncoder(context));
        setFileAppender(context, logFile, allFileAppender);
    }

    /**
     * setFileAppender
     *
     * @param context         a {@link LoggerContext} object.
     * @param logFile         a {@link String} object.
     * @param allFileAppender a {@link RollingFileAppender} object.
     */
    static void setFileAppender(LoggerContext context, String logFile, RollingFileAppender<ILoggingEvent> allFileAppender) {
        allFileAppender.setName(LoggingUtil.FILE_APPENDER_NAME);
        allFileAppender.setFile(logFile);
        allFileAppender.setRollingPolicy(LoggingUtil.rollingPolicy(context, allFileAppender, logFile));
        allFileAppender.start();
        context.getLogger(Logger.ROOT_LOGGER_NAME).detachAppender(LoggingUtil.FILE_APPENDER_NAME);
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(allFileAppender);
    }

    /**
     * addErrorFileAppender
     *
     * @param context      a {@link LoggerContext} object.
     * @param logErrorFile a {@link String} object.
     */
    private static void addErrorFileAppender(LoggerContext context, String logErrorFile) {
        final RollingFileAppender<ILoggingEvent> errorFileAppender = new RollingFileAppender<>();
        errorFileAppender.setContext(context);
        errorFileAppender.addFilter(errorLevelFilter(context));
        errorFileAppender.setEncoder(patternLayoutEncoder(context));
        errorFileAppender.setName(LoggingUtil.FILE_ERROR_APPENDER_NAME);
        errorFileAppender.setFile(logErrorFile);
        errorFileAppender.setRollingPolicy(LoggingUtil.rollingPolicy(context, errorFileAppender, logErrorFile));
        errorFileAppender.start();
        context.getLogger(Logger.ROOT_LOGGER_NAME).detachAppender(LoggingUtil.FILE_ERROR_APPENDER_NAME);
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(errorFileAppender);
    }

    /**
     * patternLayoutEncoder
     *
     * @param context a {@link LoggerContext} object.
     * @return a {@link Encoder} object.
     */
    private static Encoder<ILoggingEvent> patternLayoutEncoder(LoggerContext context) {
        final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(LoggingUtil.getProp(LoggingSystemProperty.FILE_PATTERN.getEnvironmentVariableName()));
        String charsetName = LoggingUtil.getProp(LoggingSystemProperty.FILE_CHARSET.getEnvironmentVariableName(), "default");
        encoder.setCharset(Charset.forName(charsetName));
        encoder.start();
        return encoder;
    }

    /**
     * errorLevelFilter
     *
     * @param context a {@link LoggerContext} object.
     * @return a {@link ThresholdFilter} object.
     */
    private static ThresholdFilter errorLevelFilter(LoggerContext context) {
        final ThresholdFilter filter = new ThresholdFilter();
        filter.setContext(context);
        filter.setLevel(Level.ERROR.levelStr);
        filter.start();
        return filter;
    }

}
