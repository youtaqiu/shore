package run.vexa.reactor.rabbitmq.exception;

import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.StreamReadException;
import org.junit.jupiter.api.Test;
import reactor.rabbitmq.RabbitFluxException;

import static org.assertj.core.api.Assertions.assertThat;

class QueueExceptionTest {

    @Test
    void constructorShouldStoreMessage() {
        QueueException exception = new QueueException("test message");

        assertThat(exception.getMessage()).isEqualTo("test message");
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void constructorShouldWrapRabbitFluxException() {
        RabbitFluxException rabbitFluxException = new RabbitFluxException("broker down");

        QueueException exception = new QueueException(rabbitFluxException);

        assertThat(exception.getMessage()).isEqualTo("Could not publish message to the broker");
        assertThat(exception.getCause()).isSameAs(rabbitFluxException);
    }

    @Test
    void constructorShouldWrapStreamReadException() {
        QueueException exception = new QueueException(new StreamReadException(null, "boom"));
        assertThat(exception.getMessage()).isEqualTo("Could not deserialize the event payload");
        assertThat(exception.getCause()).isInstanceOf(StreamReadException.class);
    }

    @Test
    void constructorShouldWrapJacksonException() {
        QueueException exception = new QueueException((JacksonException) new StreamReadException(null, "boom"));
        assertThat(exception.getMessage()).isEqualTo("Could not process the event payload");
        assertThat(exception.getCause()).isInstanceOf(JacksonException.class);
    }
}
