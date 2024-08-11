package sh.rime.reactor.commoms.domain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.domain.Pair;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author rained
 **/
class PairTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreation() {
        Pair<String, Integer> pair = Pair.of("key", 42);
        assertNotNull(pair);
        assertEquals("key", pair.key());
        assertEquals(42, pair.value());
    }

    @Test
    void testToMap() {
        Pair<String, Integer> pair = Pair.of("key", 42);
        var map = pair.toMap();

        assertEquals(1, map.size());
        assertEquals(42, map.get("key"));
    }

    @Test
    void testSerialization() throws JsonProcessingException {
        Pair<String, Integer> pair = Pair.of("key", 42);
        String json = objectMapper.writeValueAsString(pair);

        // Expected JSON format: {"key":42}
        assertEquals("{\"key\":42}", json);
    }

    @Test
    void testDeserialization() throws JsonProcessingException {
        // Correct JSON format: key is a String, value is an Integer
        String json = "{\"key\":\"key\",\"value\":42}";
        // Using TypeReference to specify the type parameters of Pair
        Pair<String, Integer> pair = objectMapper.readValue(json, new TypeReference<Pair<String, Integer>>() {
        });
        assertNotNull(pair);
        assertEquals("key", pair.key());
        assertEquals(42, pair.value());
    }

    @Test
    void testMapFunction() {
        Pair<String, Integer> pair = Pair.of("key", 42);
        Pair<String, String> newPair = pair.map(k -> k + "_new", v -> "Value: " + v);

        assertEquals("key_new", newPair.key());
        assertEquals("Value: 42", newPair.value());
    }

    @Test
    @SuppressWarnings("unchecked")
    void testClone() throws Exception {
        Pair<String, Integer> pair = Pair.of("key", 42);

        Method cloneMethod = Pair.class.getDeclaredMethod("clone");
        cloneMethod.setAccessible(true);
        Pair<String, Integer> clonedPair = (Pair<String, Integer>) cloneMethod.invoke(pair);

        assertNotSame(pair, clonedPair);
        assertEquals(pair, clonedPair);
    }

    @Test
    void testEmptyPair() {
        Pair<?, ?> emptyPair = Pair.EMPTY;

        assertNull(emptyPair.key());
        assertNull(emptyPair.value());
    }
}
