package run.vexa.reactor.rabbitmq.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import reactor.rabbitmq.RabbitFluxException;

/**
 * Queue exception.
 *
 * @author youta
 **/
public class QueueException extends RuntimeException {

    /**
     * Default constructor.
     *
     * @param message the exception message
     */
    public QueueException(String message) {
        super(message);
    }

    /**
     * constructor.
     *
     * @param exception the exception to wrap
     */
    public QueueException(RabbitFluxException exception) {
        super("Could not publish message to the broker", exception);
    }

    /**
     * constructor.
     *
     * @param exception the exception to wrap
     */
    public QueueException(JsonParseException exception) {
        this("deserialize", exception);
    }

    /**
     * constructor.
     *
     * @param exception the exception to wrap
     */
    public QueueException(JsonMappingException exception) {
        this("serialize", exception);
    }

    /**
     * constructor.
     *
     * @param operation the operation that failed
     * @param throwable the exception to wrap
     */
    private QueueException(String operation, Throwable throwable) {
        super("Could not " + operation + " the event payload", throwable);
    }

}
