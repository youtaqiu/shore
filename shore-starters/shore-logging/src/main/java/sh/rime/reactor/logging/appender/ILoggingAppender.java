package sh.rime.reactor.logging.appender;

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
	void start(LoggerContext context);

	/**
	 * 重置
	 *
	 * @param context LoggerContext
	 */
	void reset(LoggerContext context);

}
