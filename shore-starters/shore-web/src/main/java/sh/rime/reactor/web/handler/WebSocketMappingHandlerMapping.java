package sh.rime.reactor.web.handler;

import sh.rime.reactor.web.annotations.WebSocketMapping;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ws映射注解处理
 *
 * @author youta
 **/
public class WebSocketMappingHandlerMapping extends SimpleUrlHandlerMapping {

    private final Map<String, WebSocketHandler> handlerMap = new LinkedHashMap<>();

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public WebSocketMappingHandlerMapping() {
    }

    @Override
    public void initApplicationContext() {
        Map<String, Object> beanMap = obtainApplicationContext()
                .getBeansWithAnnotation(WebSocketMapping.class);
        beanMap.values().forEach(bean -> {
            if (!(bean instanceof WebSocketHandler)) {
                throw new RuntimeException(
                        String.format("Controller [%s] doesn't implement WebSocketHandler interface.",
                                bean.getClass().getName()));
            }
            WebSocketMapping annotation = AnnotationUtils.getAnnotation(
                    bean.getClass(), WebSocketMapping.class);
            //webSocketMapping 映射到管理中
            handlerMap.put(Objects.requireNonNull(annotation).value(), (WebSocketHandler) bean);
        });

        super.setOrder(Ordered.HIGHEST_PRECEDENCE);
        super.setUrlMap(handlerMap);
        super.initApplicationContext();
    }

}
