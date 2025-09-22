package run.vexa.reactor.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import run.vexa.reactor.kafka.autoconfigure.KafkaAutoConfiguration;

class KafkaAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KafkaAutoConfiguration.class))
            .withPropertyValues(
                    "shore.kafka.enabled=true",
                    "shore.kafka.bootstrap-servers=localhost:9092");

    @Test
    void autoConfigurationLoadsBeans() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(SenderOptions.class);
            assertThat(context).hasSingleBean(ReceiverOptions.class);
            assertThat(context).hasSingleBean(KafkaSender.class);
        });
    }
}


