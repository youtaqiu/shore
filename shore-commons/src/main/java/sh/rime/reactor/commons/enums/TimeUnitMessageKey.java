package sh.rime.reactor.commons.enums;

import lombok.Getter;

import java.util.EnumMap;
import java.util.concurrent.TimeUnit;

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
    NANOSECONDS(TimeUnit.NANOSECONDS, "nanoSeconds"),

    /**
     * 微秒.
     */
    MICROSECONDS(TimeUnit.MICROSECONDS, "microSeconds"),

    /**
     * 毫秒.
     */
    MILLISECONDS(TimeUnit.MILLISECONDS, "milliSeconds"),

    /**
     * 秒.
     */
    SECONDS(TimeUnit.SECONDS, "seconds"),

    /**
     * 分.
     */
    MINUTES(TimeUnit.MINUTES, "minutes"),

    /**
     * 小时.
     */
    HOURS(TimeUnit.HOURS, "hours"),

    /**
     * 天.
     */
    DAYS(TimeUnit.DAYS, "days");

    private static final EnumMap<TimeUnit, String> KEY_MAP = new EnumMap<>(TimeUnit.class);

    static {
        for (TimeUnitMessageKey value : TimeUnitMessageKey.values()) {
            KEY_MAP.put(value.timeUnit, value.key);
        }
    }

    private final TimeUnit timeUnit;

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
    TimeUnitMessageKey(TimeUnit timeUnit, String key) {
        this.timeUnit = timeUnit;
        this.key = key;
    }

    /**
     * 根据时间单位获取语言包key.
     *
     * @param timeUnit 时间单位
     * @return 语言包key
     */
    public static String getKey(TimeUnit timeUnit) {
        return KEY_MAP.get(timeUnit);
    }

}
