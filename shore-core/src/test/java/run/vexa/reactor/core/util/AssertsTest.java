package run.vexa.reactor.core.util;


import org.junit.jupiter.api.Test;
import run.vexa.reactor.commons.enums.CommonExceptionEnum;
import run.vexa.reactor.commons.exception.ServerException;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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

        assertDoesNotThrow(() ->
                Asserts.state(true, CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testIsTrueWithExceptionEnum() {
        assertThrows(ServerException.class, () ->
                Asserts.isTrue(false, CommonExceptionEnum.BAD_REQUEST));

        assertDoesNotThrow(() ->
                Asserts.isTrue(true, CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testIsTrueWithException() {
        assertThrows(ServerException.class, () ->
                Asserts.isTrue(false, new ServerException(CommonExceptionEnum.BAD_REQUEST)));

        assertDoesNotThrow(() ->
                Asserts.isTrue(true, new ServerException(CommonExceptionEnum.BAD_REQUEST)));
    }

    @Test
    void testIsNotTrue() {
        assertThrows(ServerException.class, () ->
                Asserts.isNotTrue(true, CommonExceptionEnum.BAD_REQUEST));

        assertDoesNotThrow(() ->
                Asserts.isNotTrue(false, CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testHasTextWithString() {
        assertThrows(ServerException.class, () ->
                Asserts.hasText("", CommonExceptionEnum.BAD_REQUEST));

        assertDoesNotThrow(() ->
                Asserts.hasText("valid text", CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testHasLengthWithString() {
        assertThrows(ServerException.class, () ->
                Asserts.hasLength("", CommonExceptionEnum.BAD_REQUEST));

        assertDoesNotThrow(() ->
                Asserts.hasLength("valid text", CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testHasLengthWithStringAndException() {
        assertThrows(ServerException.class, () ->
                Asserts.hasLength("", new ServerException(CommonExceptionEnum.BAD_REQUEST)));

        assertDoesNotThrow(() ->
                Asserts.hasLength("valid text", new ServerException(CommonExceptionEnum.BAD_REQUEST)));
    }

    @Test
    void testHasTextWithCharSequence() {
        assertThrows(ServerException.class, () ->
                Asserts.hasText((CharSequence) "", CommonExceptionEnum.BAD_REQUEST));

        assertDoesNotThrow(() ->
                Asserts.hasText((CharSequence) "valid text", CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testHasLengthWithCharSequence() {
        assertThrows(ServerException.class, () ->
                Asserts.hasLength((CharSequence) "", CommonExceptionEnum.BAD_REQUEST));

        assertDoesNotThrow(() ->
                Asserts.hasLength((CharSequence) "valid text", CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testIsNull() {
        assertThrows(ServerException.class, () ->
                Asserts.isNull("not null", CommonExceptionEnum.BAD_REQUEST));

        assertDoesNotThrow(() ->
                Asserts.isNull(null, CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testIsNotNull() {
        assertThrows(ServerException.class, () ->
                Asserts.isNotNull(null, CommonExceptionEnum.BAD_REQUEST));

        assertDoesNotThrow(() ->
                Asserts.isNotNull("not null", CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testNotEmptyArray() {
        assertThrows(ServerException.class, () ->
                Asserts.notEmpty(new Object[]{}, CommonExceptionEnum.BAD_REQUEST));

        assertDoesNotThrow(() ->
                Asserts.notEmpty(new Object[]{"not empty"}, CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testNotEmptyCollection() {
        assertThrows(ServerException.class, () ->
                Asserts.notEmpty(Collections.emptyList(), CommonExceptionEnum.BAD_REQUEST));

        assertDoesNotThrow(() ->
                Asserts.notEmpty(Collections.singletonList("not empty"), CommonExceptionEnum.BAD_REQUEST));
    }

    @Test
    void testNotEmptyMap() {
        assertThrows(ServerException.class, () ->
                Asserts.notEmpty(Collections.emptyMap(), CommonExceptionEnum.BAD_REQUEST));

        assertDoesNotThrow(() ->
                Asserts.notEmpty(Collections.singletonMap("key", "value"), CommonExceptionEnum.BAD_REQUEST));
    }
}

