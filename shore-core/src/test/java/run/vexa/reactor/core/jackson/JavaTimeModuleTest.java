package run.vexa.reactor.core.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new run.vexa.reactor.core.jackson.JavaTimeModule());
    }

    @Test
    void testSerializeLocalDateTime() throws JsonProcessingException {
        LocalDateTime dateTime = LocalDateTime.of(2024, 8, 11, 14, 30);
        String json = objectMapper.writeValueAsString(dateTime);
        assertEquals("\"2024-08-11 14:30:00\"", json);
    }

    @Test
    void testDeserializeLocalDateTime() throws JsonProcessingException {
        String json = "\"2024-08-11 14:30:00\"";
        LocalDateTime dateTime = objectMapper.readValue(json, LocalDateTime.class);
        assertEquals(LocalDateTime.of(2024, 8, 11, 14, 30), dateTime);
    }

    @Test
    void testSerializeLocalDate() throws JsonProcessingException {
        LocalDate date = LocalDate.of(2024, 8, 11);
        String json = objectMapper.writeValueAsString(date);
        assertEquals("\"2024-08-11\"", json);
    }

    @Test
    void testDeserializeLocalDate() throws JsonProcessingException {
        String json = "\"2024-08-11\"";
        LocalDate date = objectMapper.readValue(json, LocalDate.class);
        assertEquals(LocalDate.of(2024, 8, 11), date);
    }

    @Test
    void testSerializeLocalTime() throws JsonProcessingException {
        LocalTime time = LocalTime.of(14, 30, 0);
        String json = objectMapper.writeValueAsString(time);
        assertEquals("\"14:30:00\"", json);
    }

    @Test
    void testDeserializeLocalTime() throws JsonProcessingException {
        String json = "\"14:30:00\"";
        LocalTime time = objectMapper.readValue(json, LocalTime.class);
        assertEquals(LocalTime.of(14, 30, 0), time);
    }
}
