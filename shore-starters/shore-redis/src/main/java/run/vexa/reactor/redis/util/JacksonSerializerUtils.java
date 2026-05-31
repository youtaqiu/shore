package run.vexa.reactor.redis.util;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.DefaultTyping;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * The type Jackson serializer utils.
 */
@SuppressWarnings("all")
public final class JacksonSerializerUtils {

    /**
     * Instantiates a new Jackson serializer utils.
     */
    private JacksonSerializerUtils() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Creates an ObjectMapper with default typing enabled for Redis serialization.
     *
     * @return ObjectMapper with default typing
     */
    private static ObjectMapper createTypingMapper() {
        return JsonMapper.builder()
                .activateDefaultTyping(
                        BasicPolymorphicTypeValidator.builder().allowIfBaseType(Object.class).build(),
                        DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY)
                .build();
    }

    /**
     * Json redis serializer.
     *
     * @param <T>         the type parameter
     * @param targetClass the target class
     * @param mapper      the mapper
     * @return the redis serializer
     */
    @SuppressWarnings("unchecked")
    public static <T> RedisSerializer<T> json(Class<T> targetClass, ObjectMapper mapper) {
        return (RedisSerializer<T>) new GenericJacksonJsonRedisSerializer(mapper);
    }

    /**
     * Json redis serializer.
     *
     * @param <T>         the type parameter
     * @param targetClass the target class
     * @return the redis serializer
     */
    public static <T> RedisSerializer<T> json(Class<T> targetClass) {
        return json(targetClass, createTypingMapper());
    }

    /**
     * Json redis serializer.
     *
     * @return the redis serializer
     */
    public static RedisSerializer<Object> json() {
        return new GenericJacksonJsonRedisSerializer(createTypingMapper());
    }

}
