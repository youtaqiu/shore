package run.vexa.reactor.rabbitmq.exception;

import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DatabindException;
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
    public QueueException(StreamReadException exception) {
        this("deserialize", exception);
    }

    /**
     * constructor.
     *
     * @param exception the exception to wrap
     */
    public QueueException(DatabindException exception) {
        this("serialize", exception);
    }

    /**
     * constructor.
     *
     * @param exception the exception to wrap
     */
    public QueueException(JacksonException exception) {
        this("process", exception);
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
