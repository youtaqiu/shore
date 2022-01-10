package me.youm.reactor.security.annotation;

import java.lang.annotation.*;

/**
 * @author youta
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface PreAuth {

    /**
     * 是否启用
     *
     * @return boolean
     */
    boolean enabled() default true;

    /**
     * 验证用户是否授权
     *
     * @return String
     */
    String value() default "";

}
