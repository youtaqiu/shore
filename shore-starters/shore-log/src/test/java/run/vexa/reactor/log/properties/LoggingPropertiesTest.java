package run.vexa.reactor.log.properties;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingPropertiesTest {

    @Test
    void shouldExposeSensibleDefaults() {
        LoggingProperties properties = new LoggingProperties();

        assertThat(properties.getEnabled()).isTrue();
        assertThat(properties.getConsole()).isTrue();
    }

    @Test
    void shouldAllowMutatingFlags() {
        LoggingProperties properties = new LoggingProperties();

        properties.setEnabled(false);
        properties.setConsole(false);

        assertThat(properties.getEnabled()).isFalse();
        assertThat(properties.getConsole()).isFalse();
    }
}
