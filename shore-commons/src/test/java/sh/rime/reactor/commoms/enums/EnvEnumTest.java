package sh.rime.reactor.commoms.enums;

import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.enums.EnvEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * EnvEnum unit test.
 *
 * @author rained
 */
class EnvEnumTest {

    @Test
    void testEnvironment() {
        assertEquals(EnvEnum.LOCAL, EnvEnum.environment("local"));
        assertEquals(EnvEnum.DEV, EnvEnum.environment("dev"));
        assertEquals(EnvEnum.TEST, EnvEnum.environment("test"));
        assertEquals(EnvEnum.PROD, EnvEnum.environment("prod"));
    }

    @Test
    void testDefaultEnvironment() {
        assertEquals(EnvEnum.DEV, EnvEnum.environment("nonexistent"));
    }
}

