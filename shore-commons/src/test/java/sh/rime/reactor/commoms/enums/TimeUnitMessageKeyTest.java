package sh.rime.reactor.commoms.enums;


import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.enums.TimeUnitMessageKey;

import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TimeUnitMessageKey unit test.
 *
 * @author rained
 **/
class TimeUnitMessageKeyTest {

    @Test
    void testGetKey() {
        assertEquals("nanoSeconds", TimeUnitMessageKey.getKey(ChronoUnit.NANOS));
        assertEquals("microSeconds", TimeUnitMessageKey.getKey(ChronoUnit.MICROS));
        assertEquals("milliSeconds", TimeUnitMessageKey.getKey(ChronoUnit.MILLIS));
        assertEquals("seconds", TimeUnitMessageKey.getKey(ChronoUnit.SECONDS));
        assertEquals("minutes", TimeUnitMessageKey.getKey(ChronoUnit.MINUTES));
        assertEquals("hours", TimeUnitMessageKey.getKey(ChronoUnit.HOURS));
        assertEquals("days", TimeUnitMessageKey.getKey(ChronoUnit.DAYS));
    }

    @Test
    void testGetKeyWithNull() {
        assertNull(TimeUnitMessageKey.getKey(null));
    }
}

