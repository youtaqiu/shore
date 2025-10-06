package run.vexa.reactor.rabbitmq.properties;

import com.rabbitmq.client.Address;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RabbitMQPropertiesTest {

    @Test
    void shouldExposeReasonableDefaults() {
        RabbitMQProperties properties = new RabbitMQProperties();

        assertThat(properties.getHost()).isEqualTo("localhost");
        assertThat(properties.getPort()).isEqualTo(5672);
        assertThat(properties.getUsername()).isEqualTo("guest");
        assertThat(properties.getRetry()).isEqualTo(2L);
        assertThat(properties.getMinBackoff()).isEqualTo(1L);
        assertThat(properties.getParsedAddresses()).isNull();
    }

    @Test
    void setAddressesShouldParseMultipleEntries() {
        RabbitMQProperties properties = new RabbitMQProperties();

        properties.setAddresses("127.0.0.1:5672,example.com:5673");

        Address[] parsed = properties.getParsedAddresses();
        assertThat(parsed)
                .hasSize(2)
                .extracting(Address::getHost)
                .containsExactly("127.0.0.1", "example.com");
        assertThat(parsed)
                .extracting(Address::getPort)
                .containsExactly(5672, 5673);
    }

    @Test
    void setAddressesShouldRejectInvalidFormat() {
        RabbitMQProperties properties = new RabbitMQProperties();

        assertThatThrownBy(() -> properties.setAddresses("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid address");
    }
}
