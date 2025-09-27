package run.vexa.reactor.http.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import run.vexa.reactor.http.core.HttpExchangeClientFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;

class HttpExchangeClientScannerRegistrarConfigurationTest {

    @Test
    void registersScannerRegistrarWhenFactoryBeanMissing() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            AutoConfigurationPackages.register(context, "run.vexa.reactor.http.testclients.scanner");
            context.register(HttpExchangeClientScannerRegistrarConfiguration.class);
            context.refresh();

            assertThat(context.containsBean("basicScanHttpClient")).isTrue();
        }
    }

    @Test
    void backsOffWhenFactoryBeanAlreadyPresent() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            AutoConfigurationPackages.register(context, "run.vexa.reactor.http.testclients.scanner");
            context.registerBean(HttpExchangeClientFactoryBean.class, () -> new HttpExchangeClientFactoryBean());
            context.register(HttpExchangeClientScannerRegistrarConfiguration.class);
            context.refresh();

            assertThat(context.containsBean("basicScanHttpClient")).isFalse();
        }
    }
}
