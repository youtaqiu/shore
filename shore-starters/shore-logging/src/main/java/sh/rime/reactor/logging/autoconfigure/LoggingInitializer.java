package sh.rime.reactor.logging.autoconfigure;

import sh.rime.reactor.logging.utils.LoggingUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * logging 日志初始化
 *
 * @author youta
 */
@Component
public class LoggingInitializer implements EnvironmentPostProcessor, Ordered {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public LoggingInitializer() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * 用于 spring boot admin 中展示日志
     */
    public static final String LOGGING_FILE_PATH_KEY = "logging.file.path";

    /**
     * 用于 spring boot admin 中展示日志
     */
    public static final String LOGGING_FILE_NAME_KEY = "logging.file.name";

    /**
     * 用于 spring boot admin 中展示日志
     */
    public static final String SHORE_LOGGING_PROPERTY_SOURCE_NAME = "shoreLoggingPropertySource";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // 读取系统配置的日志目录，默认为项目下 logs
        String logBase = environment.getProperty(LOGGING_FILE_PATH_KEY, LoggingUtil.DEFAULT_LOG_DIR);
        // 用于 spring boot admin 中展示日志
        if (!environment.containsProperty(LOGGING_FILE_NAME_KEY)) {
            Map<String, Object> map = new HashMap<>(2);
            map.put(LOGGING_FILE_NAME_KEY, logBase + "/${spring.application.name}/" + LoggingUtil.LOG_FILE_ALL);
            MapPropertySource propertySource = new MapPropertySource(SHORE_LOGGING_PROPERTY_SOURCE_NAME, map);
            environment.getPropertySources().addLast(propertySource);
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
