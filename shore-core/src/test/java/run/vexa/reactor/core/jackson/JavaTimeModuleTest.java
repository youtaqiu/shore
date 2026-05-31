package run.vexa.reactor.core.jackson;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

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
                .addModule(new run.vexa.reactor.core.jackson.JavaTimeModule())
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
