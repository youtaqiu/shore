package io.irain.reactor.rabbitmq.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import reactor.rabbitmq.RabbitFluxException;

/**
 * @author youta
 **/
public class QueueException extends RuntimeException {

    /**
     * @param message the exception message
     */
    public QueueException(String message) {
        super(message);
    }

    /**
     * @param exception the exception to wrap
     */
    public QueueException(RabbitFluxException exception) {
        super("Could not publish message to the broker", exception);
    }

    /**
     * @param exception the exception to wrap
     */
    public QueueException(JsonParseException exception) {
        this("deserialize", exception);
    }

    /**
     * @param exception the exception to wrap
     */
    public QueueException(JsonMappingException exception) {
        this("serialize", exception);
    }

    /**
     * @param operation the operation that failed
     * @param throwable the exception to wrap
     */
    private QueueException(String operation, Throwable throwable) {
        super("Could not " + operation + " the event payload", throwable);
    }

}
