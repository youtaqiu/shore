package sh.rime.reactor.logging.appender;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.CoreConstants;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.StrUtil;
import com.github.loki4j.logback.*;
import sh.rime.reactor.logging.loki.Loki4jOkHttpSender;
import sh.rime.reactor.logging.properties.LoggingProperties;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * loki 日志接收
 *
 * @author youta
 */
@Slf4j
public class LoggingLokiAppender implements ILoggingAppender {
    private static final String APPENDER_NAME = "LOKI";
    private final LoggingProperties properties;
    private final String appName;
    private final String profile;

    /**
     * 构造器
     *
     * @param environment environment
     * @param properties  properties
     */
    public LoggingLokiAppender(Environment environment,
                               LoggingProperties properties) {
        this.properties = properties;
        // 1. 服务名和环境
        this.appName = environment.getRequiredProperty("spring.application.name");
        this.profile = environment.getRequiredProperty("spring.profiles.active");
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        this.start(context);
    }

    @Override
    public void start(LoggerContext context) {
        log.info("Loki logging start.");
        reload(context);
    }

    @Override
    public void reset(LoggerContext context) {
        log.info("Loki logging reset.");
        reload(context);
    }

    /**
     * 重新加载
     *
     * @param context context
     */
    private void reload(LoggerContext context) {
        LoggingProperties.Loki loki = properties.getLoki();
        if (loki.isEnabled()) {
            addLokiAppender(context, loki);
        }
    }

    /**
     * 添加LokiAppender
     *
     * @param context    context
     * @param properties properties
     */
    private void addLokiAppender(LoggerContext context, LoggingProperties.Loki properties) {
        Loki4jAppender lokiAppender = new Loki4jAppender();
        lokiAppender.setName(APPENDER_NAME);
        lokiAppender.setContext(context);
        // 通用配置
        lokiAppender.setBatchMaxItems(properties.getBatchMaxItems());
        lokiAppender.setBatchMaxBytes(properties.getBatchMaxBytes());
        lokiAppender.setBatchTimeoutMs(properties.getBatchTimeoutMs());
        lokiAppender.setSendQueueMaxBytes(properties.getSendQueueMaxBytes());
        lokiAppender.setUseDirectBuffers(properties.isUseDirectBuffers());
        lokiAppender.setDrainOnStop(properties.isDrainOnStop());
        lokiAppender.setMetricsEnabled(properties.isMetricsEnabled());
        lokiAppender.setVerbose(properties.isVerbose());
        // format
        Loki4jEncoder loki4jEncoder = getFormat(context, properties);
        lokiAppender.setFormat(loki4jEncoder);
        // http
        lokiAppender.setHttp(getSender(properties));
        lokiAppender.start();
        // 先删除，再添加
        context.getLogger(Logger.ROOT_LOGGER_NAME).detachAppender(APPENDER_NAME);
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(lokiAppender);
    }

    /**
     * 获取格式化
     *
     * @param context    context
     * @param properties properties
     * @return Loki4jEncoder
     */
    private Loki4jEncoder getFormat(LoggerContext context,
                                    LoggingProperties.Loki properties) {
        LoggingProperties.LokiEncoder encoder = properties.getEncoder();
        AbstractLoki4jEncoder loki4jEncoder = LoggingProperties.LokiEncoder.ProtoBuf == encoder
                ? new ProtobufEncoder() : new JsonEncoder();
        // label config
        AbstractLoki4jEncoder.LabelCfg labelCfg = new AbstractLoki4jEncoder.LabelCfg();
        labelCfg.setPattern(formatLabelPatternHandle(context, properties));
        labelCfg.setPairSeparator(properties.getFormatLabelPairSeparator());
        labelCfg.setKeyValueSeparator(properties.getFormatLabelKeyValueSeparator());
        labelCfg.setNopex(properties.isFormatLabelNoPex());
        loki4jEncoder.setLabel(labelCfg);
        // message config
        PatternLayout patternLayout = new PatternLayout();
        String formatMessagePattern = properties.getFormatMessagePattern();
        if (StrUtil.isNotBlank(formatMessagePattern)) {
            patternLayout.setPattern(formatMessagePattern);
        }
        loki4jEncoder.setMessage(patternLayout);

        // 其他配置
        loki4jEncoder.setStaticLabels(properties.isFormatStaticLabels());
        loki4jEncoder.setSortByTime(properties.isFormatSortByTime());
        loki4jEncoder.setContext(context);
        loki4jEncoder.start();
        return loki4jEncoder;
    }

    /**
     * 获取HttpSender
     *
     * @param properties properties
     * @return HttpSender
     */
    private static HttpSender getSender(LoggingProperties.Loki properties) {
        LoggingProperties.HttpSender httpSenderType = getHttpSender(properties);
        AbstractHttpSender httpSender;
        if (LoggingProperties.HttpSender.OKHttp == httpSenderType) {
            httpSender = new Loki4jOkHttpSender();
        } else if (LoggingProperties.HttpSender.ApacheHttp == httpSenderType) {
            httpSender = new ApacheHttpSender();
        } else {
            httpSender = new JavaHttpSender();
        }
        httpSender.setUrl(properties.getHttpUrl());
        httpSender.setConnectionTimeoutMs(properties.getHttpConnectionTimeoutMs());
        httpSender.setRequestTimeoutMs(properties.getHttpRequestTimeoutMs());
        String authUsername = properties.getHttpAuthUsername();
        String authPassword = properties.getHttpAuthPassword();
        if (StrUtil.isNotBlank(authUsername) && StrUtil.isNotBlank(authPassword)) {
            AbstractHttpSender.BasicAuth basicAuth = new AbstractHttpSender.BasicAuth();
            basicAuth.setUsername(authUsername);
            basicAuth.setPassword(authPassword);
            httpSender.setAuth(basicAuth);
        }
        httpSender.setTenantId(properties.getHttpTenantId());
        return httpSender;
    }

    /**
     * 格式化标签
     *
     * @param context    context
     * @param properties properties
     * @return 格式化后的标签
     */
    private String formatLabelPatternHandle(LoggerContext context, LoggingProperties.Loki properties) {
        String labelPattern = properties.getFormatLabelPattern();
        Assert.hasText(labelPattern, "ShoreLoggingProperties shore.logging.loki.format-label-pattern is blank.");
        String labelPatternExtend = properties.getFormatLabelPatternExtend();
        if (StrUtil.isNotBlank(labelPatternExtend)) {
            labelPattern = labelPattern + CharPool.COMMA + labelPatternExtend;
        }
        return labelPattern
                .replace("${appName}", appName)
                .replace("${profile}", profile)
                .replace("${HOSTNAME}", context.getProperty(CoreConstants.HOSTNAME_KEY));
    }

    /**
     * 获取HttpSender
     *
     * @param properties properties
     * @return HttpSender
     */
    private static LoggingProperties.HttpSender getHttpSender(LoggingProperties.Loki properties) {
        LoggingProperties.HttpSender httpSenderProp = properties.getHttpSender();
        if (httpSenderProp != null && httpSenderProp.isAvailable()) {
            log.debug("shore logging use {} HttpSender", httpSenderProp);
            return httpSenderProp;
        }
        if (httpSenderProp == null) {
            LoggingProperties.HttpSender[] httpSenders = LoggingProperties.HttpSender.values();
            for (LoggingProperties.HttpSender httpSender : httpSenders) {
                if (httpSender.isAvailable()) {
                    log.debug("shore logging use {} HttpSender", httpSender);
                    return httpSender;
                }
            }
            throw new IllegalArgumentException("Not java11 and no okHttp or apache http dependency.");
        }
        throw new NoClassDefFoundError(httpSenderProp.getSenderClass());
    }
}
