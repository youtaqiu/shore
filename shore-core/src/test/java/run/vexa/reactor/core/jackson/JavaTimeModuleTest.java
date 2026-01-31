package run.vexa.reactor.core.jackson;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.core.json.JsonWriteFeature;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Locale;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tools.jackson.databind.cfg.DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS;

/**
 * Java time module unit test.
 *
 * @author rained
 **/
class JavaTimeModuleTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = JsonMapper.builder()
                .disable(WRITE_DATES_AS_TIMESTAMPS)
                .defaultLocale(Locale.CHINA)
                .defaultTimeZone(TimeZone.getTimeZone("GMT+8"))
                .addModule(new JavaTimeModule())
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
                .enable(JsonWriteFeature.ESCAPE_NON_ASCII)
                .build();
    }

    @Test
    void testSerializeLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2024, 8, 11, 14, 30);
        String json = objectMapper.writeValueAsString(dateTime);
        assertEquals("\"2024-08-11 14:30:00\"", json);
    }

    @Test
    void testDeserializeLocalDateTime() {
        String json = "\"2024-08-11 14:30:00\"";
        LocalDateTime dateTime = objectMapper.readValue(json, LocalDateTime.class);
        assertEquals(LocalDateTime.of(2024, 8, 11, 14, 30), dateTime);
    }

    @Test
    void testSerializeLocalDate() {
        LocalDate date = LocalDate.of(2024, 8, 11);
        String json = objectMapper.writeValueAsString(date);
        assertEquals("\"2024-08-11\"", json);
    }

    @Test
    void testDeserializeLocalDate() {
        String json = "\"2024-08-11\"";
        LocalDate date = objectMapper.readValue(json, LocalDate.class);
        assertEquals(LocalDate.of(2024, 8, 11), date);
    }

    @Test
    void testSerializeLocalTime() {
        LocalTime time = LocalTime.of(14, 30, 0);
        String json = objectMapper.writeValueAsString(time);
        assertEquals("\"14:30:00\"", json);
    }

    @Test
    void testDeserializeLocalTime() {
        String json = "\"14:30:00\"";
        LocalTime time = objectMapper.readValue(json, LocalTime.class);
        assertEquals(LocalTime.of(14, 30, 0), time);
    }
}
