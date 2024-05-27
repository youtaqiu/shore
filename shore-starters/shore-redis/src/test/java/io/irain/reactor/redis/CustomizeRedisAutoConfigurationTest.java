package io.irain.reactor.redis;

import io.irain.reactor.redis.supprot.CustomizeReactiveRedisTemplate;
import io.irain.reactor.redis.supprot.CustomizeRedisTemplate;
import io.irain.reactor.redis.util.ReactiveRedisUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author youta
 **/
class CustomizeRedisAutoConfigurationTest {

    @Mock
    private RedisConnectionFactory mockConnectionFactory;

    @Mock
    private ReactiveRedisConnectionFactory mockReactiveConnectionFactory;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCustomizeRedisTemplate() {
        CustomizeRedisTemplate customizeRedisTemplate = new CustomizeRedisAutoConfiguration().customizeRedisTemplate(mockConnectionFactory);
        assertNotNull(customizeRedisTemplate);
    }

    @Test
    void testCustomizeReactiveRedisTemplate() {
        CustomizeReactiveRedisTemplate<String, Object> customizeReactiveRedisTemplate = new CustomizeRedisAutoConfiguration.CustomizeReactiveRedisAutoConfiguration().customizeReactiveRedisTemplate(mockReactiveConnectionFactory);
        assertNotNull(customizeReactiveRedisTemplate);
    }

    @Test
    void testReactiveRedisUtil() {
        CustomizeReactiveRedisTemplate<String, Object> customizeReactiveRedisTemplate = new CustomizeRedisAutoConfiguration.CustomizeReactiveRedisAutoConfiguration().customizeReactiveRedisTemplate(mockReactiveConnectionFactory);
        ReactiveRedisUtil reactiveRedisUtil = new CustomizeRedisAutoConfiguration().reactiveRedisUtil(customizeReactiveRedisTemplate);
        assertNotNull(reactiveRedisUtil);
    }

}
