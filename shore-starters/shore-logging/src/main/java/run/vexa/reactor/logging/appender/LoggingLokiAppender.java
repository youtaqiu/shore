package run.vexa.reactor.logging.appender;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.CoreConstants;
import cn.hutool.core.text.CharPool;
import cn.hutool.core.util.StrUtil;
import com.github.loki4j.logback.Loki4jAppender;
import com.github.loki4j.logback.PipelineConfigAppenderBase;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import run.vexa.reactor.logging.properties.LoggingProperties;

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


    /**
     * 重新加载
     *
     * @param context context
     */
    @Override
    public void reload(LoggerContext context) {
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
        PipelineConfigAppenderBase.BatchCfg batchCfg = new PipelineConfigAppenderBase.BatchCfg();
        batchCfg.setMaxItems(properties.getBatchMaxItems());
        batchCfg.setMaxBytes(properties.getBatchMaxBytes());
        batchCfg.setTimeoutMs(properties.getBatchTimeoutMs());
        batchCfg.setSendQueueMaxBytes(properties.getSendQueueMaxBytes());
        batchCfg.setUseDirectBuffers(properties.isUseDirectBuffers());
        batchCfg.setDrainOnStop(properties.isDrainOnStop());
        lokiAppender.setBatch(batchCfg);
        lokiAppender.setMetricsEnabled(properties.isMetricsEnabled());
        lokiAppender.setVerbose(properties.isVerbose());
        // message config
        PatternLayout patternLayout = new PatternLayout();
        String formatMessagePattern = properties.getFormatMessagePattern();
        if (StrUtil.isNotBlank(formatMessagePattern)) {
            patternLayout.setPattern(formatMessagePattern);
        }
        lokiAppender.setMessage(patternLayout);
        String labelsPattern = formatLabelPatternHandle(context, properties);
        lokiAppender.setLabels(labelsPattern);
        lokiAppender.setContext(context);
        // http
        lokiAppender.setHttp(getSender(properties));
        lokiAppender.start();
        // 先删除，再添加
        context.getLogger(Logger.ROOT_LOGGER_NAME).detachAppender(APPENDER_NAME);
        context.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(lokiAppender);
    }


    /**
     * 获取HttpSender
     *
     * @param properties properties
     * @return HttpSender
     */
    private static PipelineConfigAppenderBase.HttpCfg getSender(LoggingProperties.Loki properties) {
        PipelineConfigAppenderBase.HttpCfg httpCfg = new PipelineConfigAppenderBase.HttpCfg();
        httpCfg.setUrl(properties.getHttpUrl());
        httpCfg.setConnectionTimeoutMs(properties.getHttpConnectionTimeoutMs());
        httpCfg.setRequestTimeoutMs(properties.getHttpRequestTimeoutMs());
        String authUsername = properties.getHttpAuthUsername();
        String authPassword = properties.getHttpAuthPassword();
        if (StrUtil.isNotBlank(authUsername) && StrUtil.isNotBlank(authPassword)) {
            PipelineConfigAppenderBase.BasicAuth basicAuth = new PipelineConfigAppenderBase.BasicAuth();
            basicAuth.setUsername(authUsername);
            basicAuth.setPassword(authPassword);
            httpCfg.setAuth(basicAuth);
        }
        httpCfg.setTenantId(properties.getHttpTenantId());
        return httpCfg;
    }

    /**
     * 格式化标签
     *
     * @param context    context
     * @param properties properties
     * @return 格式化后的标签
     */
    private String formatLabelPatternHandle(LoggerContext context, LoggingProperties.Loki properties) {
        String labelsPattern = properties.getFormatLabelPattern();
        Assert.hasText(labelsPattern, "ShoreLoggingProperties shore.logging.loki.format-label-pattern is blank.");
        String labelPatternExtend = properties.getFormatLabelPatternExtend();
        if (StrUtil.isNotBlank(labelPatternExtend)) {
            labelsPattern = labelsPattern + CharPool.LF + labelPatternExtend;
        }
        return labelsPattern
                .replace("${appName}", appName)
                .replace("${profile}", profile)
                .replace("${HOSTNAME}", context.getProperty(CoreConstants.HOSTNAME_KEY));
    }

}
