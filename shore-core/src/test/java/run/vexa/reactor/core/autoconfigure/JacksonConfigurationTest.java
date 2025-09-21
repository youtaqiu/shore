package run.vexa.reactor.core.autoconfigure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JacksonConfiguration unit test.
 * @author rained
 **/
class JacksonConfigurationTest {

    private JacksonConfiguration jacksonConfiguration;

    @BeforeEach
    public void setUp() {
        jacksonConfiguration = new JacksonConfiguration();
    }

    @Test
    void testSerializingObjectMapper() throws JsonProcessingException {
        ObjectMapper objectMapper = jacksonConfiguration.serializingObjectMapper();

        assertNotNull(objectMapper);

        // Test timezone
        assertEquals(TimeZone.getTimeZone("GMT+8"), objectMapper.getSerializationConfig().getTimeZone());

        // Test locale
        assertEquals(Locale.CHINA, objectMapper.getSerializationConfig().getLocale());

        // Test if WRITE_DATES_AS_TIMESTAMPS is disabled
        assertFalse(objectMapper.getSerializationConfig().isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));

        // Test JavaTimeModule (assuming LocalDateTime serialization pattern is "yyyy-MM-dd HH:mm:ss")
        LocalDateTime date = LocalDateTime.of(2024, 8, 11, 12, 0);
        String dateJson = objectMapper.writeValueAsString(date);
        assertEquals("\"2024-08-11 12:00:00\"", dateJson);
    }

    @Test
    void testJackson2ObjectMapperBuilderCustomizer() throws JsonProcessingException {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        jacksonConfiguration.jackson2ObjectMapperBuilderCustomizer().customize(builder);
        ObjectMapper objectMapper = builder.build();

        // Test Long to String serialization
        long longValue = 1234567890123456789L;
        String json = objectMapper.writeValueAsString(longValue);
        assertEquals("\"1234567890123456789\"", json);

        // Test if Long is correctly serialized
        Long longObject = 1234567890123456789L;
        String jsonObject = objectMapper.writeValueAsString(longObject);
        assertEquals("\"1234567890123456789\"", jsonObject);
    }
}

