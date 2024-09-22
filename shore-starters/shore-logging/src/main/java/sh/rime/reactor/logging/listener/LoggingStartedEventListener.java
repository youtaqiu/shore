package sh.rime.reactor.logging.listener;

import sh.rime.reactor.logging.properties.LoggingProperties;
import sh.rime.reactor.logging.utils.LoggingUtil;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

/**
 * 项目启动事件通知
 *
 * @author youta
 */
public class LoggingStartedEventListener {

    private final LoggingProperties properties;


    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     *
     * @param properties the properties
     */
    public LoggingStartedEventListener(LoggingProperties properties) {
        this.properties = properties;
    }

    /**
     * 项目启动后
     */
    @Async
    @Order
    @EventListener(WebServerInitializedEvent.class)
    public void afterStart() {
        LoggingProperties.Console console = properties.getConsole();
        if (console.isCloseAfterStart()) {
            LoggingUtil.detachAppender(LoggingUtil.CONSOLE_APPENDER_NAME);
        }
    }
}
