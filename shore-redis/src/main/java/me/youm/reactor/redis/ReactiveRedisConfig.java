package me.youm.reactor.redis;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.youm.reactor.redis.service.ReactiveRedisService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;


/**
 * @author youta
 * Redis 配置类
 */
@Configuration
@SuppressWarnings("all")
public class ReactiveRedisConfig {

    @Bean
    @Primary
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        RedisSerializer<Object> redisSerializer = redisSerializer();
        RedisSerializationContext<String, Object> context = redisSerializationContext(redisSerializer);
        return new ReactiveRedisTemplate<String, Object>(factory, context);
    }

    private RedisSerializer<Object> redisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS, false);
        objectMapper.registerModule(new JavaTimeModule());
        GenericJackson2JsonRedisSerializer.registerNullValueSerializer(objectMapper, null);
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

    @SuppressWarnings("unchecked")
    private RedisSerializationContext<String, Object> redisSerializationContext(
            RedisSerializer<Object> redisSerializer) {
        return new RedisSerializationContext<String, Object>() {
            private SerializationPair<String> getStringPair() {
                // 字符串处理
                SerializationPair<String> serializationPair = SerializationPair
                        .fromSerializer(StringRedisSerializer.UTF_8);
                return serializationPair;
            }

            private SerializationPair<Object> getObjectPair() {
                // 对象处理
                SerializationPair<Object> serializationPair = SerializationPair.fromSerializer(redisSerializer);
                return serializationPair;
            }

            @Override
            public SerializationPair<String> getKeySerializationPair() {
                return getStringPair();
            }

            @Override
            public SerializationPair<Object> getValueSerializationPair() {
                return getObjectPair();
            }

            @Override
            public SerializationPair<Object> getHashKeySerializationPair() {
                return getObjectPair();
            }

            @Override
            public SerializationPair<Object> getHashValueSerializationPair() {
                return getObjectPair();
            }

            @Override
            public SerializationPair<String> getStringSerializationPair() {
                return getStringPair();
            }
        };
    }

    @Bean
    @ConditionalOnBean(name = "reactiveRedisTemplate")
    public ReactiveRedisService reactiveRedisService() {
        return new ReactiveRedisService();
    }
}
