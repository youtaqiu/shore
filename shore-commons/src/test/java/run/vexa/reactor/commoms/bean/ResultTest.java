package run.vexa.reactor.commoms.bean;

import org.junit.jupiter.api.Test;
import run.vexa.reactor.commons.bean.Result;
import run.vexa.reactor.commons.constants.CommonConstant;
import run.vexa.reactor.commons.enums.CommonExceptionEnum;
import run.vexa.reactor.commons.exception.ServerException;

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

    @Test
    void testOk() {
        Result<String> result = Result.ok();
        assertEquals(CommonConstant.SUCCESS_CODE, result.getCode());
        assertEquals(CommonConstant.SUCCESS_MSG, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testFailed() {
        Result<Void> result = Result.failed();
        assertEquals(CommonConstant.ERROR_CODE, result.getCode());
        assertEquals(ServerException.DEFAULT_MSG, result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testFailedWithMsg() {
        Result<Void> result = Result.failed("error message");
        assertEquals(CommonConstant.ERROR_CODE, result.getCode());
        assertEquals("error message", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testFailedWithMsgAndException() {
        Result<Void> result = Result.failed("error message", "exception message");
        assertEquals(CommonConstant.ERROR_CODE, result.getCode());
        assertEquals("error message", result.getMessage());
        assertEquals("exception message", result.getException());
        assertNull(result.getData());
    }

    @Test
    void testFailedWithCodeAndMsg() {
        Result<Void> result = Result.failed(1001, "error message");
        assertEquals(1001, result.getCode());
        assertEquals("error message", result.getMessage());
        assertNull(result.getData());
    }

    @Test
    void testFailedWithCodeMsgAndData() {
        Result<String> result = Result.failed(1001, "error message", "data");
        assertEquals(1001, result.getCode());
        assertEquals("error message", result.getMessage());
        assertEquals("data", result.getData());
    }

    @Test
    void testFailedWithFailure() {
        Result<Void> result = Result.failed(CommonExceptionEnum.BAD_REQUEST);
        assertEquals(CommonExceptionEnum.BAD_REQUEST.code(), result.getCode());
        assertEquals(CommonExceptionEnum.BAD_REQUEST.message(), result.getMessage());
        assertNull(result.getData());
    }


    @Test
    void testCheckDataSuccess() {
        Result<String> result = Result.ok("data");
        assertEquals("data", result.checkData());
    }

    @Test
    void testCheckDataFailure() {
        Result<String> result = Result.failed(1001, "error message", "data");
        assertThrows(ServerException.class, result::checkData);
    }

    @Test
    void testDefaultConstructor() {
        Result<String> result = new Result<>();
        assertNull(result.getCode());
        assertNull(result.getMessage());
        assertNull(result.getException());
        assertNull(result.getData());
    }
}
