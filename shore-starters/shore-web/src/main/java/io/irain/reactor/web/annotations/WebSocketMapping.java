package io.irain.reactor.web.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * webSocket映射注解.
 *
 * @author youta
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WebSocketMapping {

    /**
     * 路径
     * @return 路径
     */
    String value();
}
