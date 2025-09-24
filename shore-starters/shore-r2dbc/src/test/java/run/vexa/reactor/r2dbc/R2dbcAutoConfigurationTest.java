package run.vexa.reactor.r2dbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import reactor.test.StepVerifier;
import run.vexa.reactor.r2dbc.autoconfigure.R2dbcAutoConfiguration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * R2dbcAutoConfiguration 测试
 *
 * @author youta
 **/
class R2dbcAutoConfigurationTest {

    private R2dbcAutoConfiguration r2dbcAutoConfiguration;
    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        r2dbcAutoConfiguration = new R2dbcAutoConfiguration();
    }
    
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testConstructor() {
        // 验证类的注解存在
        assertTrue(R2dbcAutoConfiguration.class.isAnnotationPresent(Configuration.class),
                "Should have @Configuration annotation");
        assertTrue(R2dbcAutoConfiguration.class.isAnnotationPresent(EnableConfigurationProperties.class),
                "Should have @EnableConfigurationProperties annotation");
        assertTrue(R2dbcAutoConfiguration.class.isAnnotationPresent(EnableTransactionManagement.class),
                "Should have @EnableTransactionManagement annotation");
        assertTrue(R2dbcAutoConfiguration.class.isAnnotationPresent(EnableR2dbcAuditing.class),
                "Should have @EnableR2dbcAuditing annotation");

        // 验证 EnableConfigurationProperties 注解的值
        EnableConfigurationProperties configProps = R2dbcAutoConfiguration.class.getAnnotation(EnableConfigurationProperties.class);
        assertArrayEquals(new Class[]{R2dbcProperties.class}, configProps.value(),
                "EnableConfigurationProperties should be configured for R2dbcProperties.class");
    }

    @Test
    void testAuditorAwareWithEmptyUserId() {
        ReactiveAuditorAware<String> auditorAware = r2dbcAutoConfiguration.auditorAware();
        StepVerifier.create(auditorAware.getCurrentAuditor())
                .expectNext("system")
                .verifyComplete();
    }

}
