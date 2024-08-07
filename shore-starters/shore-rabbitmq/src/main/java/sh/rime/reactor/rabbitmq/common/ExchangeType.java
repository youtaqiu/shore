package sh.rime.reactor.rabbitmq.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交换机类型
 *
 * @author youta
 **/
@Getter
@AllArgsConstructor
public enum ExchangeType {

    /**
     * direct
     */
    DIRECT("direct"),
    /**
     * fanout
     */
    FANOUT("fanout"),
    /**
     * topic
     */
    TOPIC("topic"),
    /**
     * headers
     */
    HEADERS("headers"),

    /**
     * x-delayed-message
     */
    DELAYED("x-delayed-message");

    private final String type;

}
