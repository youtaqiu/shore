package sh.rime.reactor.log.annotation;

import java.lang.annotation.*;

/**
 * 日志注解.
 *
 * @author youta
 **/
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
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
}
