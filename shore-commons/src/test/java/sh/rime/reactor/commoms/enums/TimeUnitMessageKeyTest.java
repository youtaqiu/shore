package sh.rime.reactor.commoms.enums;


import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.enums.TimeUnitMessageKey;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TimeUnitMessageKey unit test.
 *
 * @author rained
 **/
class TimeUnitMessageKeyTest {

    @Test
    void testGetKey() {
        assertEquals("nanoSeconds", TimeUnitMessageKey.getKey(TimeUnit.NANOSECONDS));
        assertEquals("microSeconds", TimeUnitMessageKey.getKey(TimeUnit.MICROSECONDS));
        assertEquals("milliSeconds", TimeUnitMessageKey.getKey(TimeUnit.MILLISECONDS));
        assertEquals("seconds", TimeUnitMessageKey.getKey(TimeUnit.SECONDS));
        assertEquals("minutes", TimeUnitMessageKey.getKey(TimeUnit.MINUTES));
        assertEquals("hours", TimeUnitMessageKey.getKey(TimeUnit.HOURS));
        assertEquals("days", TimeUnitMessageKey.getKey(TimeUnit.DAYS));
    }

    @Test
    void testGetKeyWithNull() {
        assertNull(TimeUnitMessageKey.getKey(null));
    }
}

