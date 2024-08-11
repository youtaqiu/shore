package sh.rime.reactor.core.util;


import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.enums.CommonExceptionEnum;
import sh.rime.reactor.commons.exception.ServerException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Asserts unit test.
 *
 * @author rained
 **/
class AssertsTest {

    @Test
    void testState() {
        assertThrows(ServerException.class, () ->
                Asserts.state(false, CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testHasText() {
        assertThrows(ServerException.class, () ->
                Asserts.hasText("", CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testIsNotNull() {
        assertThrows(ServerException.class, () ->
                Asserts.isNotNull(null, CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testNotEmptyCollection() {
        assertThrows(ServerException.class, () ->
                Asserts.notEmpty(Collections.emptyList(), CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testNotEmptyMap() {
        assertThrows(ServerException.class, () ->
                Asserts.notEmpty(Collections.emptyMap(), CommonExceptionEnum.BAD_REQUEST));
    }

}

