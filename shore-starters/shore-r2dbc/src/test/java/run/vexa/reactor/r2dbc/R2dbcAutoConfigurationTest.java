package run.vexa.reactor.r2dbc;

import run.vexa.reactor.r2dbc.autoconfigure.R2dbcAutoConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.ReactiveAuditorAware;
import reactor.test.StepVerifier;


/**
 * @author youta
 **/
class R2dbcAutoConfigurationTest {

    private R2dbcAutoConfiguration r2dbcAutoConfiguration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        r2dbcAutoConfiguration = new R2dbcAutoConfiguration();
    }

    @Test
    void testAuditorAwareWithEmptyUserId() {
        ReactiveAuditorAware<String> auditorAware = r2dbcAutoConfiguration.auditorAware();
        StepVerifier.create(auditorAware.getCurrentAuditor())
                .expectNext("system")
                .verifyComplete();
    }

}
