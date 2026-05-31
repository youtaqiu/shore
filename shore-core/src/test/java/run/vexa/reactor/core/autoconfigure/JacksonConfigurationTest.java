package run.vexa.reactor.core.autoconfigure;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JacksonConfiguration unit test.
 * @author rained
 **/
class JacksonConfigurationTest {

    private JacksonConfiguration jacksonConfiguration;

    @BeforeEach
    void setUp() {
        jacksonConfiguration = new JacksonConfiguration();
    }

    @Test
    void testObjectMapper() {
        ObjectMapper objectMapper = jacksonConfiguration.objectMapper();

        assertNotNull(objectMapper);

        // Test if WRITE_DATES_AS_TIMESTAMPS is disabled
        assertFalse(objectMapper.serializationConfig().isEnabled(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS));

        // Test JavaTimeModule (assuming LocalDateTime serialization pattern is "yyyy-MM-dd HH:mm:ss")
        LocalDateTime date = LocalDateTime.of(2024, 8, 11, 12, 0);
        String dateJson = objectMapper.writeValueAsString(date);
        assertEquals("\"2024-08-11 12:00:00\"", dateJson);
    }

    @Test
    void testSpecialCharactersSerialization() {
        ObjectMapper objectMapper = jacksonConfiguration.objectMapper();

        // Test special characters handling
        TestObject testObject = new TestObject();
        testObject.setSpecialField("特殊字符!@#$%^&*()");
        String json = objectMapper.writeValueAsString(testObject);
        TestObject deserializedObject = objectMapper.readValue(json, TestObject.class);
        assertEquals(testObject.getSpecialField(), deserializedObject.getSpecialField());
    }

    @Test
    void testLongToStringSerialization() {
        ObjectMapper objectMapper = jacksonConfiguration.objectMapper();

        // Test Long to String serialization (moved from Jackson2ObjectMapperBuilderCustomizer to JavaTimeModule)
        long longValue = 1234567890123456789L;
        String json = objectMapper.writeValueAsString(longValue);
        assertEquals("\"1234567890123456789\"", json);

        Long longObject = 1234567890123456789L;
        String jsonObject = objectMapper.writeValueAsString(longObject);
        assertEquals("\"1234567890123456789\"", jsonObject);
    }

    @Setter
    @Getter
    static class TestObject {
        private String nullField;
        private String specialField;

    }
}
