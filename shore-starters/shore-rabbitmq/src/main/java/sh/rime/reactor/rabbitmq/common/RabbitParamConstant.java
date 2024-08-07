package sh.rime.reactor.rabbitmq.common;

/**
 * rabbitmq param constant
 *
 * @author youta
 **/
public class RabbitParamConstant {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public RabbitParamConstant() {
    }

    /**
     * delay header name
     */
    public static final String DELAY_HEADER = "x-delay";

    /**
     * delay exchange type
     */
    public static final String DELAYED_TYPE = "x-delayed-type";

    /**
     * message destination suffix
     */
    public static final String MESSAGE_DESTINATION_SUFFIX = "-exchange";

    /**
     * default routing key
     */
    public static final String DEFAULT_ROUTING_KEY = "#";
}
