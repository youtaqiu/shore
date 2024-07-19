package sh.rime.reactor.logging.listener;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggerContextListener;
import ch.qos.logback.core.spi.ContextAwareBase;
import sh.rime.reactor.logging.appender.ILoggingAppender;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Logback LoggerContextListener
 * @author youta
 */
@RequiredArgsConstructor
public class LogbackLoggerContextListener extends ContextAwareBase implements LoggerContextListener {
	private final List<ILoggingAppender> appenderList;

	@Override
	public boolean isResetResistant() {
		return true;
	}

	@Override
	public void onStart(LoggerContext context) {
		for (ILoggingAppender appender : appenderList) {
			appender.start(context);
		}
	}

	@Override
	public void onReset(LoggerContext context) {
		for (ILoggingAppender appender : appenderList) {
			appender.reset(context);
		}
	}

	@Override
	public void onStop(LoggerContext context) {
		// Nothing to do.
	}

	@Override
	public void onLevelChange(Logger logger, Level level) {
		// Nothing to do.
	}
}
