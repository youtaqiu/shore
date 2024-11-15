package sh.rime.reactor.commoms.exception;


import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.exception.TokenException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TokenException unit test.
 * @author rained
 **/
class TokenExceptionTest {

    @Test
    void testTokenExceptionWithMessage() {
        TokenException exception = new TokenException("Token {0} is invalid.", "ABC123");
        assertEquals(401, exception.getErrorCode());
        assertEquals("Token ABC123 is invalid.", exception.getMessage());
    }

    @Test
    void testDefaultTokenException() {
        TokenException exception = new TokenException();
        assertEquals(401, exception.getErrorCode());
        assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    void testGetKey() {
        TokenException exception = new TokenException();
        assertEquals("unauthorized", exception.getKey());
    }
}
