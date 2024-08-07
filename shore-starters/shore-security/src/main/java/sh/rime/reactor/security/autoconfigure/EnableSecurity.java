package sh.rime.reactor.security.autoconfigure;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * EnableSecurity is a class that config
 *
 * @author rained
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SecurityConfiguration.class)
public @interface EnableSecurity {
}
