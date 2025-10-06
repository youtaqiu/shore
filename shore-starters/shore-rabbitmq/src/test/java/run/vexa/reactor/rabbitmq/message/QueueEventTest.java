package run.vexa.reactor.rabbitmq.message;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueueEventTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldHaveDefaultMetadataAndSerializePayload() throws IOException {
        TestQueueEvent event = new TestQueueEvent();
        event.setRoutingKey("rk");
        event.setExchange("exchange");

        byte[] payload = event.getPayload();
        JsonNode jsonNode = mapper.readTree(payload);

        assertThat(jsonNode.get("eventId").asText()).isEqualTo(event.getEventId());
        assertThat(jsonNode.get("queue").asText()).isEqualTo("test.queue");
        assertThat(jsonNode.get("routingKey").asText()).isEqualTo("rk");
        assertThat(jsonNode.get("exchange").asText()).isEqualTo("exchange");
        assertThat(jsonNode.get("eventTime").asLong()).isEqualTo(event.getEventTime().getTime());
    }

    @Test
    void getPayloadShouldWrapSerializationException() {
        UnserializableQueueEvent event = new UnserializableQueueEvent();

        assertThatThrownBy(event::getPayload)
                .isInstanceOf(IllegalStateException.class)
                .hasCauseInstanceOf(com.fasterxml.jackson.core.JsonProcessingException.class);
    }

    private static final class TestQueueEvent extends QueueEvent {
        private final Date fixedDate = new Date();

        private TestQueueEvent() {
            setEventId(UUID.randomUUID().toString());
            setEventTime(fixedDate);
            setQueue("test.queue");
        }

        @Override
        public String getQueue() {
            return "test.queue";
        }
    }

    private static final class UnserializableQueueEvent extends QueueEvent {
        @SuppressWarnings("FieldCanBeLocal")
        private final Object self;

        private UnserializableQueueEvent() {
            setQueue("thread.queue");
            this.self = this;
        }

        public Object getSelf() {
            return self;
        }

        @Override
        public String getQueue() {
            return "thread.queue";
        }
    }
}
