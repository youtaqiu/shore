package run.vexa.reactor.redis.supprot;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author youta
 **/
class CustomizeRedisTemplateTest {

    @Mock
    private RedisConnectionFactory mockConnectionFactory;

    private CustomizeRedisTemplate template;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        template = new CustomizeRedisTemplate(mockConnectionFactory);
    }

    @Test
    void testConstructorWithConnectionFactory() {
        assertNotNull(template.getKeySerializer());
        assertNotNull(template.getHashKeySerializer());
        assertNotNull(template.getValueSerializer());
        assertNotNull(template.getHashValueSerializer());
        assertNotNull(template.getConnectionFactory());
        assertNotNull(template.getRequiredConnectionFactory());
        assertNotNull(template.getStringSerializer());
        assertNotNull(template.getHashKeySerializer());
        assertNotNull(template.getHashValueSerializer());
    }

}
