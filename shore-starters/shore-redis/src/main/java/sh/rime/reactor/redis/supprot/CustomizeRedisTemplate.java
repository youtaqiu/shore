package sh.rime.reactor.redis.supprot;

import sh.rime.reactor.redis.util.JacksonSerializerUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

/**
 * The type Customize redis template.
 * @author youta
 **/
public class CustomizeRedisTemplate extends RedisTemplate<String, Object> {

    /**
     * Instantiates a new Universal redis template.
     */
    public CustomizeRedisTemplate() {
        RedisSerializer<Object> serializer = JacksonSerializerUtils.json();
        this.setKeySerializer(RedisSerializer.string());
        this.setHashKeySerializer(RedisSerializer.string());
        this.setValueSerializer(serializer);
        this.setHashValueSerializer(serializer);
    }

    /**
     * Instantiates a new Universal redis template.
     *
     * @param redisConnectionFactory the redis connection factory
     */
    public CustomizeRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        this();
        this.setConnectionFactory(redisConnectionFactory);
        this.afterPropertiesSet();
    }

}
