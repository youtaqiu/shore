package run.vexa.reactor.commons.enums;

import lombok.Getter;

import java.time.temporal.ChronoUnit;
import java.util.EnumMap;

/**
 * TimeUnitMessageKey is an enum that maps java.util.concurrent.TimeUnit values to corresponding message keys.
 * These message keys can be used for localization or other scenarios where time units need to be converted to strings.
 * Each enum value represents a specific time unit, and includes a key that can be used to retrieve a message or string representation of the time unit.
 * The class also includes a method for retrieving the key for a specific time unit.
 *
 * @author youta
 */
public enum TimeUnitMessageKey {

    /**
     * 纳秒.
     */
    NANOSECONDS(ChronoUnit.NANOS, "nanoSeconds"),

    /**
     * 微秒.
     */
    MICROSECONDS(ChronoUnit.MICROS, "microSeconds"),

    /**
     * 毫秒.
     */
    MILLISECONDS(ChronoUnit.MILLIS, "milliSeconds"),

    /**
     * 秒.
     */
    SECONDS(ChronoUnit.SECONDS, "seconds"),

    /**
     * 分.
     */
    MINUTES(ChronoUnit.MINUTES, "minutes"),

    /**
     * 小时.
     */
    HOURS(ChronoUnit.HOURS, "hours"),

    /**
     * 天.
     */
    DAYS(ChronoUnit.DAYS, "days");

    private static final EnumMap<ChronoUnit, String> KEY_MAP = new EnumMap<>(ChronoUnit.class);

    static {
        for (TimeUnitMessageKey value : TimeUnitMessageKey.values()) {
            KEY_MAP.put(value.timeUnit, value.key);
        }
    }

    private final ChronoUnit timeUnit;

    /**
     * -- GETTER --
     *  获取语言包key.
     *
     */
    @Getter
    private final String key;

    /**
     * 构造函数.
     *
     * @param timeUnit 时间单位
     * @param key      语言包key
     */
    TimeUnitMessageKey(ChronoUnit timeUnit, String key) {
        this.timeUnit = timeUnit;
        this.key = key;
    }

    /**
     * 根据时间单位获取语言包key.
     *
     * @param timeUnit 时间单位
     * @return 语言包key
     */
    public static String getKey(ChronoUnit timeUnit) {
        return KEY_MAP.get(timeUnit);
    }

}
