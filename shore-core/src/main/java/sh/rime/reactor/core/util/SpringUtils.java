package sh.rime.reactor.core.util;

import lombok.Getter;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring工具类.
 *
 * @author youta
 **/
@Configuration(proxyBeanMethods = false)
public class SpringUtils implements ApplicationContextAware {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public SpringUtils() {
    }

    /**
     * 获取ApplicationContext.
     */
    @Getter
    private static ApplicationContext context = null;

    /**
     * 根据bean类型获取bean实例列表.
     *
     * @param clazz bean类型
     * @param <T>   bean类型
     * @return bean实例列表
     */
    public static <T> Collection<T> getBeans(Class<T> clazz) {
        return getContext().getBeansOfType(clazz).values();
    }

    /**
     * 根据bean类型获取bean实例.
     *
     * @param clazz bean类型
     * @param <T>   bean类型
     * @return bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return getContext().getBean(clazz);
    }

    /**
     * 根据注解类型获取bean实例.
     *
     * @param annotationType 注解类型
     * @return bean实例和实例名称散列表
     */
    public static Map<String, Object> getBeanMapWithAnnotation(Class<? extends Annotation> annotationType) {
        return getContext().getBeansWithAnnotation(annotationType);
    }

    /**
     * 根据注解类型获取bean实例.
     *
     * @param clazz          实例类型
     * @param annotationType 注解类型
     * @param <T>            实例类型
     * @return bean实例和实例名称散列表
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getBeanMapWithAnnotation(Class<T> clazz,
                                                              Class<? extends Annotation> annotationType) {
        Map<String, Object> beanMap = getBeanMapWithAnnotation(annotationType);
        if (beanMap.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, T> result = new HashMap<>(beanMap.size());
        for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
            String beanName = entry.getKey();
            Object bean = entry.getValue();

            if (clazz.isInstance(bean)) {
                result.put(beanName, (T) bean);
            }
        }

        return result;
    }

    /**
     * 根据注解类型获取bean实例.
     *
     * @param annotationType 注解类型
     * @return bean实例列表
     */
    public static Collection<Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return getBeanMapWithAnnotation(annotationType).values();
    }

    /**
     * 根据注解类型获取bean实例.
     *
     * @param clazz          bean类型
     * @param annotationType 注解类型
     * @param <T>            bean类型
     * @return bean实例列表
     */
    public static <T> Collection<T> getBeansWithAnnotation(Class<T> clazz, Class<? extends Annotation> annotationType) {
        return getBeanMapWithAnnotation(clazz, annotationType).values();
    }

    /**
     * 根据指定类型获取bean实例和实例名称.
     *
     * @param clazz bean实例类
     * @param <T>   bean实例类型
     * @return bean实例和实例名称散列表
     */
    public static <T> Map<String, T> getBeanMap(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    /**
     * 获取环境信息.
     *
     * @return 环境信息
     */
    private static Environment getEnvironment() {
        return getContext().getEnvironment();
    }

    /**
     * 获取属性.
     *
     * @param propertyName 属性名
     * @return 属性值
     */
    @Nullable
    public static String getProperty(String propertyName) {
        return getEnvironment().getProperty(propertyName);
    }

    /**
     * 获取应用名称.
     *
     * @return 应用名称
     */
    public static String getApplicationName() {
        String name = getProperty("spring.application.name");
        return name == null ? "" : name;
    }

    /**
     * 推送事件.
     *
     * @param event 事件
     */
    public static void publishEvent(ApplicationEvent event) {
        getContext().publishEvent(event);
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

}
