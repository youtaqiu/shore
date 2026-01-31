package run.vexa.reactor.redis;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.data.redis.autoconfigure.DataRedisAutoConfiguration;
import org.springframework.boot.data.redis.autoconfigure.DataRedisReactiveAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import reactor.core.publisher.Flux;
import run.vexa.reactor.redis.supprot.CustomizeReactiveRedisTemplate;
import run.vexa.reactor.redis.supprot.CustomizeRedisTemplate;
import run.vexa.reactor.redis.util.JacksonSerializerUtils;
import run.vexa.reactor.redis.util.ReactiveRedisUtil;

/**
 * The type Customize redis autoconfiguration.
 *
 * @author youta
 **/
@ConditionalOnClass(RedisOperations.class)
@AutoConfiguration(before = {DataRedisAutoConfiguration.class})
public class CustomizeRedisAutoConfiguration {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public CustomizeRedisAutoConfiguration() {
        // No initialization needed
    }

    /**
     * Customize redis template customize redis template.
     *
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
    @AutoConfiguration(before = {DataRedisReactiveAutoConfiguration.class})
    @ConditionalOnClass({ReactiveRedisConnectionFactory.class, ReactiveRedisTemplate.class, Flux.class})
    public static class CustomizeReactiveRedisAutoConfiguration {

        /**
     * Default constructor used by Spring's dependency injection framework.
     * This class only provides static configuration through @Bean methods,
     * so no instance fields need to be initialized.
     */
    public CustomizeReactiveRedisAutoConfiguration() {
        // No initialization needed - configuration is done through @Bean methods
    }

        /**
         * customize reactive redis template customize reactive redis template.
         *
         * @param redisConnectionFactory the redis connection factory
         * @return customize reactive redis template
         */
        @Bean("customizeReactiveRedisTemplate")
        public CustomizeReactiveRedisTemplate<String, Object> customizeReactiveRedisTemplate(ReactiveRedisConnectionFactory redisConnectionFactory) {
            var serializationContext = RedisSerializationContext.
                    <String, Object>newSerializationContext()
                    .key(RedisSerializer.string()).value(JacksonSerializerUtils.json())
                    .hashKey(RedisSerializer.string()).hashValue(JacksonSerializerUtils.json()).build();
            return new CustomizeReactiveRedisTemplate<>(redisConnectionFactory, serializationContext);
        }
    }


    /**
     * Reactive redis util reactive redis util.
     *
     * @param customizeReactiveRedisTemplate reactive redis template
     * @return the reactive redis util
     */
    @Bean
    @ConditionalOnBean(name = "customizeReactiveRedisTemplate")
    public ReactiveRedisUtil reactiveRedisUtil(CustomizeReactiveRedisTemplate<String, Object> customizeReactiveRedisTemplate) {
        return new ReactiveRedisUtil(customizeReactiveRedisTemplate);
    }

}
