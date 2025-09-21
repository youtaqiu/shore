package run.vexa.reactor.test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ShoreWebFluxTest is a class that config
 *
 * @author rained
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@WebFluxTest
@ExtendWith(SpringExtension.class)
@Import({ShoreSecurityTestAutoconfigure.class})
@SuppressWarnings("unused")
public @interface ShoreWebFluxTest {

    /**
     * Controllers to test.
     *
     * @return controllers to test
     */
    Class<?>[] controllers() default {};
}
