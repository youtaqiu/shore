package run.vexa.reactor.core.util;

import cn.hutool.core.util.ReflectUtil;
import tools.jackson.core.json.JsonReadFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import run.vexa.reactor.core.jackson.JavaTimeModule;

import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * bean util.
 *
 * @author youta
 */
@SuppressWarnings("unused")
public class BeanUtil extends org.springframework.beans.BeanUtils {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public BeanUtil() {
        // This constructor is intentionally empty.
        // Further initialization logic can be added here if needed in the future.
    }

    /**
     * source属性为空的不赋值给target
     *
     * @param source 源头
     * @param tClass 目标类型
     * @param <T>    目标类型
     * @return T 目标
     */
    public static <T> T copy(Object source, Class<T> tClass) {
        T target = ReflectUtil.newInstanceIfPossible(tClass);
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        return target;
    }

    /**
     * source属性为空的不赋值给target
     *
     * @param source 源头
     * @param target 目标类型
     * @param <T>    目标类型
     * @return T 目标
     */
    @SuppressWarnings("all")
    public static <T> T copy(Object source, T target) {
        org.springframework.beans.BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
        return target;
    }

    /**
     * source属性为空的不赋值给target
     *
     * @param bean 源头
     * @return Map 目标
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> beanToMap(Object bean) {
        return cn.hutool.core.bean.BeanUtil.copyProperties(bean, Map.class);
    }

    /**
     * 复制集合
     *
     * @param collection 集合
     * @param targetType 目标类型
     * @param <T>        目标类型
     * @return List
     */
    public static <T> List<T> copyToList(Collection<?> collection, Class<T> targetType) {
        if (null == collection) {
            return Collections.emptyList();
        }
        if (collection.isEmpty()) {
            return new ArrayList<>(0);
        }
        return collection.stream().map(s -> copy(s, targetType)).collect(Collectors.toList());
    }

    /**
     * 获取属性为空的属性名
     *
     * @param source 源头
     * @return String[]
     */
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }

    /**
     * 获取实例
     *
     * @return objectMapper
     */
    public static ObjectMapper getInstance() {
        return JacksonHolder.INSTANCE;
    }

    /**
     * JacksonHolder
     */
    private static final class JacksonHolder {
        private static final ObjectMapper INSTANCE = JsonMapper.builder()
                .configure(JsonReadFeature.ALLOW_SINGLE_QUOTES, true)
                .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
                .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
                .defaultLocale(Locale.CHINA)
                .defaultTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                .addModule(new JavaTimeModule())
                .build();
    }

}
