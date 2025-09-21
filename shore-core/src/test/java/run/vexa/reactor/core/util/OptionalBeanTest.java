package run.vexa.reactor.core.util;


import org.junit.jupiter.api.Test;
import run.vexa.reactor.commons.exception.ServerException;
import run.vexa.reactor.commons.exception.ServerFailure;

import static org.junit.jupiter.api.Assertions.*;

/**
 * OptionalBean unit test.
 *
 * @author rained
 **/
class OptionalBeanTest {

    @Test
    void testOf() {
        String value = "test";
        OptionalBean<String> optionalBean = OptionalBean.of(value);

        assertTrue(optionalBean.isPresent());
        assertEquals(value, optionalBean.get());

        assertThrows(NullPointerException.class, () -> OptionalBean.of(null));
    }

    @Test
    void testOfNullable() {
        String value = "test";
        OptionalBean<String> optionalBean = OptionalBean.ofNullable(value);

        assertTrue(optionalBean.isPresent());
        assertEquals(value, optionalBean.get());

        OptionalBean<String> emptyBean = OptionalBean.ofNullable(null);
        assertFalse(emptyBean.isPresent());
    }

    @Test
    void testOrElse() {
        String value = "test";
        OptionalBean<String> optionalBean = OptionalBean.ofNullable(value);
        assertEquals(value, optionalBean.orElse("default"));

        OptionalBean<String> emptyBean = OptionalBean.ofNullable(null);
        assertEquals("default", emptyBean.orElse("default"));
    }

    @Test
    void testOrElseThrow() {
        String value = "test";
        OptionalBean<String> optionalBean = OptionalBean.ofNullable(value);
        assertEquals(value, optionalBean.orElseThrow(IllegalStateException::new));

        OptionalBean<String> emptyBean = OptionalBean.ofNullable(null);
        assertThrows(IllegalStateException.class, () -> emptyBean.orElseThrow(IllegalStateException::new));
    }

    @Test
    void testIsPresent() {
        OptionalBean<String> optionalBean = OptionalBean.ofNullable("test");
        assertTrue(optionalBean.isPresent());

        OptionalBean<String> emptyBean = OptionalBean.ofNullable(null);
        assertFalse(emptyBean.isPresent());
    }

    @Test
    void testIfPresent() {
        OptionalBean<String> optionalBean = OptionalBean.ofNullable("test");
        optionalBean.ifPresent(value -> assertEquals("test", value));

        OptionalBean<String> emptyBean = OptionalBean.ofNullable(null);
        emptyBean.ifPresent(value -> fail("Should not be called"));
    }

    @Test
    void testOrElseThrowWithServerFailure() {
        OptionalBean<String> emptyBean = OptionalBean.ofNullable(null);
        ServerFailure failure = new ServerFailure() {
            @Override
            public int code() {
                return 500;
            }

            @Override
            public String message() {
                return "Server error";
            }
        };

        assertThrows(ServerException.class, () -> emptyBean.orElseThrow(failure));
    }
}

