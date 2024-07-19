package sh.rime.reactor.commons.annotation;

import java.lang.annotation.*;


/**
 * The Anonymous annotation is used to mark classes, methods, or packages that do not require authentication.
 * This annotation can be inherited by subclasses and is retained at runtime.
 * It is also documented, meaning it should be included in the Javadoc for the annotated element.
 *
 * @author youta
 */
@Target({ElementType.PACKAGE, ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface Anonymous {
}

