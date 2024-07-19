package sh.rime.reactor.redis.supprot;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

/**
 * customize reactive redis template
 * @author youta
 **/
public class CustomizeReactiveRedisTemplate<K, V> extends ReactiveRedisTemplate<String, Object> {

    /**
     * Creates new {@link ReactiveRedisTemplate} using given {@link ReactiveRedisConnectionFactory} and
     *
     * @param connectionFactory    must not be {@literal null}.
     * @param serializationContext can be {@literal null}.
     */
    public CustomizeReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory,
                                          RedisSerializationContext<String, Object> serializationContext) {
        this(connectionFactory, serializationContext, false);
    }

    /**
     * Creates new {@link ReactiveRedisTemplate} using given {@link ReactiveRedisConnectionFactory} and
     *
     * @param connectionFactory    connection factory for creating new connections.
     * @param serializationContext serialization context
     * @param exposeConnection     expose the connection used.
     */
    public CustomizeReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory,
                                          RedisSerializationContext<String, Object> serializationContext, boolean exposeConnection) {
        super(connectionFactory, serializationContext, exposeConnection);
    }
}
