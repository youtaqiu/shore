package sh.rime.reactor.commoms.bean;

import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.bean.Result;
import sh.rime.reactor.commons.constants.CommonConstant;
import sh.rime.reactor.commons.enums.CommonExceptionEnum;
import sh.rime.reactor.commons.exception.ServerException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Result unit test.
 *
 * @author rained
 **/
class ResultTest {

    @Test
    void testOkWithoutData() {
        Result<Void> result = Result.ok();

        assertNotNull(result);
        assertEquals(CommonConstant.SUCCESS_CODE, result.getCode());
        assertEquals(CommonConstant.SUCCESS_MSG, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testOkWithData() {
        String data = "Sample Data";
        Result<String> result = Result.ok(data);

        assertNotNull(result);
        assertEquals(CommonConstant.SUCCESS_CODE, result.getCode());
        assertEquals(CommonConstant.SUCCESS_MSG, result.getMessage());
        assertEquals(data, result.getData());
    }

    @Test
    void testFailedWithoutArguments() {
        Result<Void> result = Result.failed();

        assertNotNull(result);
        assertEquals(CommonConstant.ERROR_CODE, result.getCode());
        assertEquals(ServerException.DEFAULT_MSG, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testFailedWithMessage() {
        String errorMsg = "Custom Error Message";
        Result<Void> result = Result.failed(errorMsg);

        assertNotNull(result);
        assertEquals(CommonConstant.ERROR_CODE, result.getCode());
        assertEquals(errorMsg, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testFailedWithCodeAndMessage() {
        int errorCode = 400;
        String errorMsg = "Bad Request";
        Result<Void> result = Result.failed(errorCode, errorMsg);

        assertNotNull(result);
        assertEquals(errorCode, result.getCode());
        assertEquals(errorMsg, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testFailedWithServerFailure() {
        Result<Void> result = Result.failed(CommonExceptionEnum.BAD_REQUEST);
        assertNotNull(result);
        assertEquals(CommonExceptionEnum.BAD_REQUEST.code(), result.getCode());
        assertEquals(CommonExceptionEnum.BAD_REQUEST.message(), result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testCheckSuccess() {
        Result<String> result = Result.ok("Sample Data");

        assertDoesNotThrow(result::check);
        assertEquals("Sample Data", result.checkData());
    }

    @Test
    void testCheckFailure() {
        Result<Void> result = Result.failed("Failure Message");

        ServerException exception = assertThrows(ServerException.class, result::check);
        assertEquals("Failure Message", exception.getMessage());
    }

}
