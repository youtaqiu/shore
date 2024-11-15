package sh.rime.reactor.commoms.exception;


import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.enums.CommonExceptionEnum;
import sh.rime.reactor.commons.exception.ServerException;
import sh.rime.reactor.commons.exception.ServerFailure;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ServerException unit test.
 *
 * @author rained
 **/
class ServerExceptionTest {

    @Test
    void testDefaultConstructor() {
        ServerException exception = new ServerException();
        assertEquals(ServerException.DEFAULT_MSG, exception.getMessage());
        assertEquals(500, exception.getErrorCode());
    }

    @Test
    void testMessageConstructor() {
        ServerException exception = new ServerException("Custom error message");
        assertEquals("Custom error message", exception.getMessage());
        assertEquals(500, exception.getErrorCode());
    }

    @Test
    void testErrorCodeConstructor() {
        ServerException exception = new ServerException("Not Found", 404);
        assertEquals("Not Found", exception.getMessage());
        assertEquals(404, exception.getErrorCode());
    }

    @Test
    void testServerFailureConstructor() {
        ServerFailure failure = CommonExceptionEnum.BAD_REQUEST;
        ServerException exception = new ServerException(failure);
        assertEquals("Bad Request", exception.getMessage());
        assertEquals(400, exception.getErrorCode());
    }


    @Test
    void testConstructorWithErrorCode() {
        ServerException exception = new ServerException(404);
        assertEquals(404, exception.getErrorCode());
        assertEquals(ServerException.DEFAULT_MSG, exception.getMessage());
    }

    @Test
    void testConstructorWithMessageCauseEnableSuppressionWritableStackTrace() {
        Throwable cause = new RuntimeException("test cause");
        ServerException exception = new ServerException("test message", cause, true, false);
        assertEquals(500, exception.getErrorCode());
        assertEquals("test message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithMessageCauseEnableSuppressionWritableStackTraceAndErrorCode() {
        Throwable cause = new RuntimeException("test cause");
        ServerException exception = new ServerException("test message", cause, true, false, 400);
        assertEquals(400, exception.getErrorCode());
        assertEquals("test message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithMessageAndCause() {
        Throwable cause = new RuntimeException("test cause");
        ServerException exception = new ServerException("test message", cause);
        assertEquals(500, exception.getErrorCode());
        assertEquals("test message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithMessageCauseAndErrorCode() {
        Throwable cause = new RuntimeException("test cause");
        ServerException exception = new ServerException("test message", cause, 401);
        assertEquals(401, exception.getErrorCode());
        assertEquals("test message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithMessage() {
        ServerException exception = new ServerException("test message");
        assertEquals(500, exception.getErrorCode());
        assertEquals("test message", exception.getMessage());
    }

    @Test
    void testConstructorWithMessageAndErrorCode() {
        ServerException exception = new ServerException("test message", 403);
        assertEquals(403, exception.getErrorCode());
        assertEquals("test message", exception.getMessage());
    }

    @Test
    void testConstructorWithErrorCodeAndMessage() {
        ServerException exception = new ServerException(403, "test message");
        assertEquals(403, exception.getErrorCode());
        assertEquals("test message", exception.getMessage());
    }

    @Test
    void testConstructorWithFailure() {
        ServerException exception = new ServerException(CommonExceptionEnum.BAD_REQUEST);
        assertEquals(CommonExceptionEnum.BAD_REQUEST.code(), exception.getErrorCode());
        assertEquals(CommonExceptionEnum.BAD_REQUEST.message(), exception.getMessage());
    }

    @Test
    void testConstructorWithFailureAndArgs() {
        ServerException exception = new ServerException(CommonExceptionEnum.LOGIN_BODY_PARSE_ERROR, "test user");
        assertEquals(CommonExceptionEnum.LOGIN_BODY_PARSE_ERROR.code(), exception.getErrorCode());
        assertEquals("Login failed", exception.getMessage());
    }

    @Test
    void testConstructorWithCause() {
        Throwable cause = new RuntimeException("test cause");
        ServerException exception = new ServerException(cause);
        assertEquals(500, exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testConstructorWithCauseAndErrorCode() {
        Throwable cause = new RuntimeException("test cause");
        ServerException exception = new ServerException(cause, 409);
        assertEquals(409, exception.getErrorCode());
        assertEquals(cause, exception.getCause());
    }
}
