package run.vexa.reactor.limit.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.temporal.ChronoUnit;

/**
 * 限流注解
 *
 * @author youta
 **/
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Limit {

    /**
     * key
     *
     * @return String string
     */
    String key() default "";

    /**
     * 时间
     *
     * @return long 超时时间
     */
    long expire() default 1;

    /**
     * 单位(默认秒)
     *
     * @return ChronoUnit time unit
     */
    ChronoUnit unit() default ChronoUnit.SECONDS;

    /**
     * 单位时间产生的令牌个数
     *
     * @return int 个数
     */
    int rate() default 1;

    /**
     * 是否限制IP
     *
     * @return boolean 是否限制IP
     */
    boolean restrictIp() default false;

    /**
     * 是否限制用户
     *
     * @return boolean 是否限制用户
     */
    boolean restrictUser() default false;

}
