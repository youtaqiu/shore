package run.vexa.reactor.core.autoconfigure;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;
import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS;

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
    void testObjectMapper() throws JsonProcessingException {
        ObjectMapper objectMapper = jacksonConfiguration.objectMapper();

        assertNotNull(objectMapper);

        // Test timezone
        assertEquals(TimeZone.getTimeZone("GMT+8"), objectMapper.serializationConfig().getTimeZone());

        // Test locale
        assertEquals(Locale.CHINA, objectMapper.serializationConfig().getLocale());

        // Test if WRITE_DATES_AS_TIMESTAMPS is disabled
        assertFalse(objectMapper.serializationConfig().isEnabled(WRITE_DATES_AS_TIMESTAMPS));

        // Test JavaTimeModule (assuming LocalDateTime serialization pattern is "yyyy-MM-dd HH:mm:ss")
        LocalDateTime date = LocalDateTime.of(2024, 8, 11, 12, 0);
        String dateJson = objectMapper.writeValueAsString(date);
        assertEquals("\"2024-08-11 12:00:00\"", dateJson);
    }


    @Test
    void testSpecialCharactersSerialization() throws JsonProcessingException {
        ObjectMapper objectMapper = jacksonConfiguration.objectMapper();
        
        // Test special characters handling
        TestObject testObject = new TestObject();
        testObject.setSpecialField("特殊字符!@#$%^&*()");
        String json = objectMapper.writeValueAsString(testObject);
        TestObject deserializedObject = objectMapper.readValue(json, TestObject.class);
        assertEquals(testObject.getSpecialField(), deserializedObject.getSpecialField());
    }

    @Setter
    @Getter
    static class TestObject {
        private String nullField;
        private String specialField;

    }
}

