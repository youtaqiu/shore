package run.vexa.reactor.rabbitmq.exception;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
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
    void constructorShouldWrapJsonParseException() throws Exception {
        JsonFactory factory = new JsonFactory();
        try (JsonParser parser = factory.createParser("{}")) {
            QueueException exception = new QueueException(new JsonParseException(parser, "boom"));
            assertThat(exception.getMessage()).isEqualTo("Could not deserialize the event payload");
            assertThat(exception.getCause()).isInstanceOf(JsonParseException.class);
        }
    }

    @Test
    void constructorShouldWrapJsonMappingException() throws Exception {
        JsonFactory factory = new JsonFactory();
        try (JsonParser parser = factory.createParser("{}")) {
            QueueException exception = new QueueException(new JsonMappingException(parser, "boom"));
            assertThat(exception.getMessage()).isEqualTo("Could not serialize the event payload");
            assertThat(exception.getCause()).isInstanceOf(JsonMappingException.class);
        }
    }
}
