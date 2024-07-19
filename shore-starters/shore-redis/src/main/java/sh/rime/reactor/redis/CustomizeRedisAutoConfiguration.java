package sh.rime.reactor.redis;

import sh.rime.reactor.redis.supprot.CustomizeReactiveRedisTemplate;
import sh.rime.reactor.redis.supprot.CustomizeRedisTemplate;
import sh.rime.reactor.redis.util.JacksonSerializerUtils;
import sh.rime.reactor.redis.util.ReactiveRedisUtil;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import reactor.core.publisher.Flux;

/**
 * @author youta
 **/
@ConditionalOnClass(RedisOperations.class)
@AutoConfiguration(before = {RedisAutoConfiguration.class})
public class CustomizeRedisAutoConfiguration {

    /**
     * Customize redis template customize redis template.
     * @param redisConnectionFactory the redis connection factory
     * @return the customize redis template
     */
    @Bean
    public CustomizeRedisTemplate customizeRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return new CustomizeRedisTemplate(redisConnectionFactory);
    }

    /**
     * The type Customize reactive redis autoconfiguration.
     */
    @AutoConfiguration(before = {RedisReactiveAutoConfiguration.class})
    @ConditionalOnClass({ReactiveRedisConnectionFactory.class, ReactiveRedisTemplate.class, Flux.class})
    public static class CustomizeReactiveRedisAutoConfiguration {

        /**
         * customize reactive redis template customize reactive redis template.
         *
         * @param redisConnectionFactory the redis connection factory
         * @return customize reactive redis template
         */
        @Bean("customizeReactiveRedisTemplate")
        public CustomizeReactiveRedisTemplate<String,Object> customizeReactiveRedisTemplate(ReactiveRedisConnectionFactory redisConnectionFactory) {
            RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext.
                    <String, Object>newSerializationContext()
                    .key(RedisSerializer.string()).value(JacksonSerializerUtils.json())
                    .hashKey(RedisSerializer.string()).hashValue(JacksonSerializerUtils.json()).build();
            return new CustomizeReactiveRedisTemplate<>(redisConnectionFactory, serializationContext);
        }
    }


    /**
     * Reactive redis util reactive redis util.
     * @param customizeReactiveRedisTemplate reactive redis template
     * @return the reactive redis util
     */
    @Bean
    @ConditionalOnBean(name = "customizeReactiveRedisTemplate")
    public ReactiveRedisUtil reactiveRedisUtil(CustomizeReactiveRedisTemplate<String,Object> customizeReactiveRedisTemplate) {
        return new ReactiveRedisUtil(customizeReactiveRedisTemplate);
    }

}
