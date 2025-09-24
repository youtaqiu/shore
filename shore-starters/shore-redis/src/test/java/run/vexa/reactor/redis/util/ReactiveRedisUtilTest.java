package run.vexa.reactor.redis.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import run.vexa.reactor.redis.supprot.CustomizeReactiveRedisTemplate;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
    void testGetReturnsEmptyWhenKeyNull() {
        StepVerifier.create(util.get(null))
                .verifyComplete();
        verifyNoInteractions(mockTemplate);
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
    void testGetExpire() {
        String key = "expireKey";
        Duration expected = Duration.ofMinutes(5);
        when(mockTemplate.getExpire(key)).thenReturn(Mono.just(expected));

        Mono<Duration> result = util.getExpire(key);

        verify(mockTemplate).getExpire(key);
        assertEquals(expected, result.block());
    }

    @Test
    void testDelete() {
        String key = "deleteKey";
        when(mockTemplate.delete(key)).thenReturn(Mono.just(1L));

        Mono<Long> result = util.del(key);

        verify(mockTemplate).delete(key);
        assertEquals(1L, result.block());
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

        Boolean actual = result.block();

        verify(mockTemplate).opsForHash();
        verify(mockHashOps).putAll(key, map);
        verify(mockTemplate).expire(key, time);
        assertEquals(expectedValue, actual);
        verifyNoMoreInteractions(mockTemplate);
    }


    @Test
    void testHSetWithExpire() {
        String key = "hashKey";
        String item = "hashItem";
        Object value = "hashValue";
        long seconds = 30L;

        when(mockTemplate.opsForHash()).thenReturn(mockHashOps);
        when(mockHashOps.put(key, item, value)).thenReturn(Mono.just(true));
        when(mockTemplate.expire(key, Duration.ofSeconds(seconds))).thenReturn(Mono.just(true));

        Mono<Boolean> result = util.hSet(key, item, value, seconds);

        Boolean actual = result.block();

        verify(mockTemplate).opsForHash();
        verify(mockHashOps).put(key, item, value);
        verify(mockTemplate).expire(key, Duration.ofSeconds(seconds));
        assertEquals(Boolean.TRUE, actual);
        verifyNoMoreInteractions(mockTemplate);
    }

    @Test
    void testHSetWithoutExpireWhenTimeNonPositive() {
        String key = "hashKey";
        String item = "hashItem";
        Object value = "hashValue";

        when(mockTemplate.opsForHash()).thenReturn(mockHashOps);
        when(mockHashOps.put(key, item, value)).thenReturn(Mono.just(true));

        Mono<Boolean> result = util.hSet(key, item, value, 0L);

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        verify(mockTemplate).opsForHash();
        verify(mockHashOps).put(key, item, value);
        verifyNoMoreInteractions(mockTemplate);
    }

    @Test
    void testHmget() {
        String key = "hashKey";
        when(mockTemplate.opsForHash()).thenReturn(mockHashOps);
        when(mockHashOps.entries(key)).thenReturn(Flux.just(new AbstractMap.SimpleEntry<>("field", "value")));

        StepVerifier.create(util.hmget(key))
                .expectNextMatches(entry -> entry.getKey().equals("field") && entry.getValue().equals("value"))
                .verifyComplete();

        verify(mockTemplate).opsForHash();
        verify(mockHashOps).entries(key);
    }


}
