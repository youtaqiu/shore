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
}
