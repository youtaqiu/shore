package run.vexa.reactor.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import run.vexa.reactor.kafka.autoconfigure.KafkaAutoConfiguration;
import run.vexa.reactor.kafka.properties.KafkaProperties;

class KafkaPropertiesBindingTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(KafkaAutoConfiguration.class))
            .withPropertyValues(
                    "shore.kafka.enabled=true",
                    "shore.kafka.bootstrap-servers=broker:9092",
                    "shore.kafka.client-id=test-client",
                    "shore.kafka.acks=1",
                    "shore.kafka.linger=10ms",
                    "shore.kafka.batch-size=1024",
                    "shore.kafka.buffer-memory=2048",
                    "shore.kafka.max-in-flight=10",
                    "shore.kafka.group-id=g",
                    "shore.kafka.auto-offset-reset=earliest",
                    "shore.kafka.enable-auto-commit=false",
                    "shore.kafka.max-poll-records=10",
                    "shore.kafka.session-timeout=5s",
                    "shore.kafka.request-timeout=20s");

    @Test
    void bindProperties() {
        contextRunner.run(ctx -> {
            KafkaProperties p = ctx.getBean(KafkaProperties.class);
            assertThat(p.isEnabled()).isTrue();
            assertThat(p.getBootstrapServers()).isEqualTo("broker:9092");
            assertThat(p.getClientId()).isEqualTo("test-client");
            assertThat(p.getAcks()).isEqualTo("1");
            assertThat(p.getLinger()).isEqualTo(Duration.ofMillis(10));
            assertThat(p.getBatchSize()).isEqualTo(1024);
            assertThat(p.getBufferMemory()).isEqualTo(2048);
            assertThat(p.getMaxInFlight()).isEqualTo(10);
            assertThat(p.getGroupId()).isEqualTo("g");
            assertThat(p.getAutoOffsetReset()).isEqualTo("earliest");
            assertThat(p.isEnableAutoCommit()).isFalse();
            assertThat(p.getMaxPollRecords()).isEqualTo(10);
            assertThat(p.getSessionTimeout()).isEqualTo(Duration.ofSeconds(5));
            assertThat(p.getRequestTimeout()).isEqualTo(Duration.ofSeconds(20));
        });
    }
}


