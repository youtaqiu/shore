package run.vexa.reactor.security.anonymous;

import cn.hutool.core.text.StrPool;
import cn.hutool.extra.spring.SpringUtil;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.stereotype.Controller;
import run.vexa.reactor.commons.annotation.Anonymous;
import run.vexa.reactor.commons.annotation.RequestMethodEnum;
import run.vexa.reactor.core.properties.AuthProperties;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AnonymousUrlLoader
 *
 * @author rained
 */
public class AnonymousUrlLoader {

    private final AuthProperties authProperties;

    /**
     * Default constructor.
     *
     * @param authProperties the properties
     */
    public AnonymousUrlLoader(AuthProperties authProperties) {
        this.authProperties = authProperties;
    }

    /**
     * Load anonymous urls
     */
    public void loadAnonymousUrls() {
        if (SpringUtil.getApplicationContext() == null) {
            return;
        }

        Map<String, Object> controllers = SpringUtil.getApplicationContext()
                .getBeansWithAnnotation(Controller.class);

        for (Object controller : controllers.values()) {
            Class<?> controllerClass = controller.getClass();
            RequestMapping classRequestMapping = AnnotatedElementUtils.findMergedAnnotation(controllerClass, RequestMapping.class);
            String classPath = classRequestMapping != null ? classRequestMapping.path()[0] : StrPool.SLASH;
            String finalClassPath = classPath.startsWith(StrPool.SLASH) ? classPath : StrPool.SLASH.concat(classPath);
            for (Method method : controllerClass.getDeclaredMethods()) {
                boolean hasAnnotation = AnnotatedElementUtils.hasAnnotation(method, Anonymous.class);
                if (hasAnnotation) {
                    RequestMapping methodRequestMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
                    if (methodRequestMapping != null) {
                        String[] paths = methodRequestMapping.path();
                        RequestMethod[] methods = methodRequestMapping.method();
                        Set<RequestMethod> methodSet = Arrays.stream(methods).collect(Collectors.toSet());
                        Set<String> pathSet = paths.length == 0 ? Set.of(finalClassPath) : Arrays.stream(paths)
                                .map(path -> finalClassPath + path)
                                .collect(Collectors.toSet());
                        RequestMethodEnum request = RequestMethodEnum.find(methodSet.isEmpty()
                                ? RequestMethodEnum.ALL.getType()
                                : methodSet.stream().findFirst()
                                .orElseThrow(() -> new RuntimeException("Method not found"))
                                .name());
                        setPath(request, pathSet);
                    }
                }
            }
        }
    }

    /**
     * 设置路径
     *
     * @param method  请求方法
     * @param pathSet 路径集合
     */
    private void setPath(RequestMethodEnum method, Set<String> pathSet) {
        if (pathSet.isEmpty()) {
            return;
        }
        switch (Objects.requireNonNull(method)) {
            case GET -> {
                Set<String> collect = new HashSet<>(pathSet);
                authProperties.getGetExcludePatterns().addAll(collect);
            }
            case POST -> {
                Set<String> collect = new HashSet<>(pathSet);
                authProperties.getPostExcludePatterns().addAll(collect);
            }
            case PUT -> {
                Set<String> collect = new HashSet<>(pathSet);
                authProperties.getPutExcludePatterns().addAll(collect);
            }
            case PATCH -> {
                Set<String> collect = new HashSet<>(pathSet);
                authProperties.getPatchExcludePatterns().addAll(collect);
            }
            case DELETE -> {
                Set<String> collect = new HashSet<>(pathSet);
                authProperties.getDeleteExcludePatterns().addAll(collect);
            }
            default -> {
                Set<String> collect = new HashSet<>(pathSet);
                authProperties.getExcludePatterns().addAll(collect);
            }
        }

    }
}
