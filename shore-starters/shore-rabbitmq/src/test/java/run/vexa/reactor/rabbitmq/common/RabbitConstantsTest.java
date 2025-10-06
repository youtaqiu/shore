package run.vexa.reactor.rabbitmq.common;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RabbitConstantsTest {

    @Test
    void rabbitParamConstantsShouldMatchExpectedValues() {
        assertThat(RabbitParamConstant.DELAY_HEADER).isEqualTo("x-delay");
        assertThat(RabbitParamConstant.DELAYED_TYPE).isEqualTo("x-delayed-type");
        assertThat(RabbitParamConstant.MESSAGE_DESTINATION_SUFFIX).isEqualTo("-exchange");
        assertThat(RabbitParamConstant.DEFAULT_ROUTING_KEY).isEqualTo("#");
    }

    @Test
    void exchangeTypesShouldExposeRabbitmqValues() {
        assertThat(ExchangeType.DIRECT.getType()).isEqualTo("direct");
        assertThat(ExchangeType.FANOUT.getType()).isEqualTo("fanout");
        assertThat(ExchangeType.TOPIC.getType()).isEqualTo("topic");
        assertThat(ExchangeType.HEADERS.getType()).isEqualTo("headers");
        assertThat(ExchangeType.DELAYED.getType()).isEqualTo("x-delayed-message");
    }
}
