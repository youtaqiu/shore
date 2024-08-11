package sh.rime.reactor.logging.autoconfigure;

import sh.rime.reactor.logging.appender.*;
import sh.rime.reactor.logging.listener.LogbackLoggerContextListener;
import sh.rime.reactor.logging.listener.LoggingStartedEventListener;
import sh.rime.reactor.logging.properties.LoggingProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.ClassUtils;

import java.lang.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * logging 日志配置
 *
 * @author youta
 */
@AutoConfiguration
@Order(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties(LoggingProperties.class)
public class LoggingConfiguration {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public LoggingConfiguration() {
    }

    /**
     * loggingStartedEventListener
     *
     * @param loggingProperties loggingProperties
     * @return {@link LoggingStartedEventListener}
     */
    @Bean
    public LoggingStartedEventListener loggingStartedEventListener(LoggingProperties loggingProperties) {
        return new LoggingStartedEventListener(loggingProperties);
    }

    /**
     * logbackLoggerContextListenerl
     *
     * @param loggingAppenderObjectProvider loggingAppenderObjectProvider
     * @return {@link LogbackLoggerContextListener}
     */
    @Bean
    public LogbackLoggerContextListener logbackLoggerContextListener(ObjectProvider<ILoggingAppender> loggingAppenderObjectProvider) {
        List<ILoggingAppender> loggingAppenderList = loggingAppenderObjectProvider.orderedStream().toList();
        return new LogbackLoggerContextListener(loggingAppenderList);
    }

    /**
     * logging file config
     */
    @AutoConfiguration
    @ConditionalOnAppender(Appender.FILE)
    public static class LoggingFileConfiguration {

        /**
         * Default constructor.
         * This constructor is used for serialization and other reflective operations.
         */
        public LoggingFileConfiguration() {
        }

        /**
         * loggingFileAppender
         *
         * @param environment environmente
         * @param properties  properties
         * @return loggingFileAppender
         */
        @Bean
        public LoggingFileAppender loggingFileAppender(Environment environment,
                                                       LoggingProperties properties) {
            return new LoggingFileAppender(environment, properties);
        }
    }


    /**
     * LoggingLokiConfiguration
     */
    @AutoConfiguration
    @ConditionalOnAppender(Appender.LOKI)
    public static class LoggingLokiConfiguration {

        /**
         * Default constructor.
         * This constructor is used for serialization and other reflective operations.
         */
        public LoggingLokiConfiguration() {
        }

        /**
         * loggingLokiAppender
         *
         * @param environment environment
         * @param properties  properties
         * @return LoggingLokiAppender
         */
        @Bean
        public LoggingLokiAppender loggingLokiAppender(Environment environment,
                                                       LoggingProperties properties) {
            return new LoggingLokiAppender(environment, properties);
        }
    }

    /**
     * ConditionalOnAppender
     */
    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Conditional(LoggingCondition.class)
    private @interface ConditionalOnAppender {

        /**
         * Appender
         *
         * @return Appender
         */
        Appender value();

    }

    /**
     * LoggingCondition
     */
    @Order(Ordered.HIGHEST_PRECEDENCE)
    private static final class LoggingCondition extends SpringBootCondition {
        private static final String LOG_STASH_CLASS_NAME = "net.logstash.logback.encoder.LoggingEventCompositeJsonEncoder";
        private static final String LOKI_CLASS_NAME = "com.github.loki4j.logback.Loki4jAppender";

        @Override
        public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Map<String, Object> attributes = metadata.getAnnotationAttributes(ConditionalOnAppender.class.getName());
            Object value = Objects.requireNonNull(attributes).get("value");
            Appender appender = Appender.valueOf(value.toString());
            Environment environment = context.getEnvironment();
            ClassLoader classLoader = context.getClassLoader();
            Boolean fileEnabled = environment.getProperty(LoggingProperties.Files.PREFIX + ".enabled", Boolean.class, Boolean.TRUE);
            Boolean lokiEnabled = environment.getProperty(LoggingProperties.Loki.PREFIX + ".enabled", Boolean.class, Boolean.FALSE);
            ConditionOutcome conditionOutcome;
            if (Appender.LOKI == appender) {
                if (!lokiEnabled) {
                    return ConditionOutcome.noMatch("Logging loki is not enabled.");
                }
                if (hasLokiDependencies(classLoader)) {
                    return ConditionOutcome.match();
                }
                throw new IllegalStateException("Logging loki is enabled, please add com.github.loki4j loki-logback-appender dependencies.");
            } else if (Appender.FILE_JSON == appender) {
                Boolean isUseJsonFormat = environment.getProperty(LoggingProperties.Files.PREFIX + ".use-json-format", Boolean.class, Boolean.FALSE);
                // 没有开启文件或者没有开启 json 格式化
                if (!fileEnabled || !isUseJsonFormat) {
                    return ConditionOutcome.noMatch("Logging json file is not enabled.");
                }
                if (hasLogStashDependencies(classLoader)) {
                    return ConditionOutcome.match();
                }
                throw new IllegalStateException("Logging file json format is enabled, please add logstash-logback-encoder dependencies.");
            } else if (Appender.FILE == appender) {
                if (!fileEnabled) {
                    conditionOutcome = ConditionOutcome.noMatch("Logging logstash is not enabled.");
                } else {
                    conditionOutcome = ConditionOutcome.match();
                }
            } else {
                conditionOutcome = ConditionOutcome.match();
            }
            return conditionOutcome;
        }

        /**
         * 是否有 logstash 依赖
         *
         * @param classLoader 类加载器
         * @return 是否有 logstash 依赖
         */
        private static boolean hasLogStashDependencies(ClassLoader classLoader) {
            return ClassUtils.isPresent(LOG_STASH_CLASS_NAME, classLoader);
        }

        /**
         * 是否有 loki 依赖
         *
         * @param classLoader 类加载器
         * @return 是否有 loki 依赖
         */
        private static boolean hasLokiDependencies(ClassLoader classLoader) {
            return ClassUtils.isPresent(LOKI_CLASS_NAME, classLoader);
        }
    }

}
