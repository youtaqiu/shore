package io.irain.reactor.redis.util;

import io.irain.reactor.redis.supprot.CustomizeReactiveRedisTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author youta
 **/
class ReactiveRedisUtilTest {

    @Mock
    private CustomizeReactiveRedisTemplate<String, Object> mockTemplate;

    @Mock
    private ReactiveValueOperations<String, Object> mockValueOps;

    @Mock
    private ReactiveHashOperations<String, Object, Object> mockHashOps;
    private ReactiveRedisUtil util;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        util = new ReactiveRedisUtil(mockTemplate);
    }

    @Test
    void testIncrement() {
        String key = "testKey";
        long delta = 1L;
        long expectedValue = 2L;

        when(mockTemplate.opsForValue()).thenReturn(mockValueOps);
        when(mockValueOps.increment(key, delta)).thenReturn(Mono.just(expectedValue));

        Mono<Long> result = util.increment(key, delta);

        verify(mockTemplate).opsForValue();
        verify(mockValueOps).increment(key, delta);
        assertEquals(expectedValue, result.block());
    }

    @Test
    void testExpire() {
        String key = "testKey";
        Duration timeout = Duration.ofSeconds(10L);
        boolean expectedValue = true;

        when(mockTemplate.expire(key, timeout)).thenReturn(Mono.just(expectedValue));

        Mono<Boolean> result = util.expire(key, timeout);

        verify(mockTemplate).expire(key, timeout);
        assertEquals(expectedValue, result.block());
    }

    @Test
    void testHasKey() {
        String key = "testKey";
        boolean expectedValue = true;

        when(mockTemplate.hasKey(key)).thenReturn(Mono.just(expectedValue));

        Mono<Boolean> result = util.hasKey(key);

        verify(mockTemplate).hasKey(key);
        assertEquals(expectedValue, result.block());
    }

    @Test
    void testGet() {
        String key = "testKey";
        String expectedValue = "testValue";

        when(mockTemplate.opsForValue()).thenReturn(mockValueOps);
        when(mockValueOps.get(key)).thenReturn(Mono.just(expectedValue));

        Mono<String> result = util.get(key);

        verify(mockTemplate).opsForValue();
        assertEquals(expectedValue, result.block());
    }

    @Test
    void testSet() {
        String key = "testKey";
        Object value = "testValue";
        boolean expectedValue = true;

        when(mockTemplate.opsForValue()).thenReturn(mockValueOps);
        when(mockValueOps.set(key, value)).thenReturn(Mono.just(expectedValue));

        Mono<Boolean> result = util.set(key, value);

        verify(mockTemplate).opsForValue();
        verify(mockValueOps).set(key, value);
        assertEquals(expectedValue, result.block());
    }

    @Test
    void testSetWithTime() {
        String key = "testKey";
        Object value = "testValue";
        Duration time = Duration.ofSeconds(10L);
        boolean expectedValue = true;

        when(mockTemplate.opsForValue()).thenReturn(mockValueOps);
        when(mockValueOps.set(key, value, time)).thenReturn(Mono.just(expectedValue));

        Mono<Boolean> result = util.set(key, value, time);

        verify(mockTemplate).opsForValue();
        verify(mockValueOps).set(key, value, time);
        assertEquals(expectedValue, result.block());
    }

    @Test
    void testHashGet() {
        String key = "testKey";
        String item = "testItem";
        String expectedValue = "testValue";
        when(mockTemplate.opsForHash()).thenReturn(mockHashOps);
        when(mockHashOps.get(key, item)).thenReturn(Mono.just(expectedValue));

        Mono<String> result = util.hashGet(key, item);

        verify(mockTemplate).opsForHash();
        verify(mockHashOps).get(key, item);
        assertEquals(expectedValue, result.block());
    }

    @Test
    void testHashSet() {
        String key = "testKey";
        Map<String, Object> map = new HashMap<>();
        map.put("testItem1", "testValue1");
        map.put("testItem2", "testValue2");
        boolean expectedValue = true;

        when(mockTemplate.opsForHash()).thenReturn(mockHashOps);
        when(mockHashOps.putAll(key, map)).thenReturn(Mono.just(expectedValue));

        Mono<Boolean> result = util.hashSet(key, map);

        verify(mockTemplate).opsForHash();
        verify(mockHashOps).putAll(key, map);
        assertEquals(expectedValue, result.block());
    }

    @Test
    void testHashSetWithTime() {
        String key = "testKey";
        Map<String, Object> map = new HashMap<>();
        map.put("testItem1", "testValue1");
        map.put("testItem2", "testValue2");
        Duration time = Duration.ofSeconds(10L);
        boolean expectedValue = true;

        when(mockTemplate.opsForHash()).thenReturn(mockHashOps);
        when(mockHashOps.putAll(key, map)).thenReturn(Mono.just(expectedValue));
        when(mockTemplate.expire(key, time)).thenReturn(Mono.just(expectedValue));
        Mono<Boolean> result = util.hashSet(key, map, time);

        verify(mockTemplate).opsForHash();
        verify(mockHashOps).putAll(key, map);
        assertEquals(expectedValue, result.block());
    }


}
