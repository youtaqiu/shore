package sh.rime.reactor.log.annotation;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 日志注解.
 *
 * @author youta
 **/
@Target(METHOD)
@Retention(RUNTIME)
public @interface Log {

    /**
     * 操作内容.
     *
     * @return 操作内容
     */
    String value();

    /**
     * 是否记录
     *
     * @return 是否记录
     */
    boolean enable() default true;

    /**
     * Excludes a parameter from the logged message, see {@link Log}.
     */
    @Target(PARAMETER)
    @Retention(RUNTIME)
    @interface Exclude {
    }
}
