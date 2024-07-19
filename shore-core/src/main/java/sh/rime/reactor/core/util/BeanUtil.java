package sh.rime.reactor.core.util;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.util.ReflectUtil;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.io.Serial;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author youta
 */
public class BeanUtil extends org.springframework.beans.BeanUtils {

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
            if (srcValue == null) emptyNames.add(pd.getName());
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
    private static class JacksonHolder {
        private static final ObjectMapper INSTANCE = new JacksonObjectMapper();
    }

    /**
     * A custom ObjectMapper for configuring JSON serialization and deserialization settings.
     */
    private static class JacksonObjectMapper extends ObjectMapper {
        @Serial
        private static final long serialVersionUID = 4288193147502386170L;

        private static final Locale CHINA = Locale.CHINA;

        /**
         * Default constructor.
         */
        JacksonObjectMapper() {
            super(jsonFactory());
            super.setLocale(CHINA);
            super.setDateFormat(new SimpleDateFormat(DatePattern.NORM_DATETIME_PATTERN, CHINA));
            // 单引号
            super.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
            // 忽略json字符串中不识别的属性
            super.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            // 忽略无法转换的对象
            super.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
            super.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
            super.findAndRegisterModules();
        }

        /**
         * Copy constructor.
         *
         * @param src the source ObjectMapper to copy from
         */
        JacksonObjectMapper(ObjectMapper src) {
            super(src);
        }

        /**
         * Creates a custom JsonFactory with specific settings.
         *
         * @return a configured JsonFactory instance
         */
        private static JsonFactory jsonFactory() {
            return JsonFactory.builder()
                    .configure(JsonReadFeature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true)
                    .configure(JsonReadFeature.ALLOW_UNESCAPED_CONTROL_CHARS, true)
                    .build();
        }

        @Override
        public ObjectMapper copy() {
            return new JacksonObjectMapper(this);
        }
    }

}
