package run.vexa.reactor.logging.appender;

import ch.qos.logback.classic.LoggerContext;


/**
 * logging Appender 抽象
 *
 * @author youta
 */
public interface ILoggingAppender {

    /**
     * 启动
     *
     * @param context LoggerContext
     */
    default void start(LoggerContext context) {
        this.reload(context);
    }

    /**
     * 重置
     *
     * @param context LoggerContext
     */
    default void reset(LoggerContext context) {
        this.reload(context);
    }

    /**
     * 重新加载
     *
     * @param context LoggerContext
     */
    void reload(LoggerContext context);
}
