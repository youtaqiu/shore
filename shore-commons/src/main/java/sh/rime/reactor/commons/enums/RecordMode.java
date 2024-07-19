package sh.rime.reactor.commons.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 记录模式
 *
 * @author xiyu
 **/
@Getter
@AllArgsConstructor
public enum RecordMode {
    /**
     * 本地日志
     */
    LOCAL_LOG,
    /**
     * RocketMq
     */
    ROCKET_MQ,
    /**
     * 自定义
     */
    CUSTOMIZE
}
