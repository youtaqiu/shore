package io.irain.reactor.logging.listener;

import io.irain.reactor.logging.properties.LoggingProperties;
import io.irain.reactor.logging.utils.LoggingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

/**
 * 项目启动事件通知
 *
 * @author youta
 */
@RequiredArgsConstructor
public class LoggingStartedEventListener {
	private final LoggingProperties properties;


	/**
	 * 项目启动后
	 */
	@Async
	@Order
	@EventListener(WebServerInitializedEvent.class)
	public void afterStart() {
		// 1. 关闭控制台
		LoggingProperties.Console console = properties.getConsole();
		if (console.isCloseAfterStart()) {
			LoggingUtil.detachAppender(LoggingUtil.CONSOLE_APPENDER_NAME);
		}
	}
}
