package sh.rime.reactor.logging.appender;

/**
 * Appender 枚举
 *
 * @author youta
 */
public enum Appender {

	/**
	 * 控制台
	 */
	CONSOLE,
	/**
	 * 文件
	 */
	FILE,
	/**
	 * loki
	 */
	LOKI,
	/**
	 * OpenTelemetry
	 */
	OPEN_TELEMETRY

}
