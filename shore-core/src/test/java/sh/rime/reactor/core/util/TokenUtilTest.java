package sh.rime.reactor.core.util;

import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.exception.TokenException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Token util unit test.
 *
 * @author rained
 **/
class TokenUtilTest {

    @Test
    void testGetTokenSuccess() {

        String authHeader = Optional.ofNullable(System.getenv("TOKEN")).orElse("Bearer 123");
        String token = TokenUtil.getToken(authHeader);
        assertEquals("123", token);
    }

    @Test
    void testGetTokenWithInvalidLength() {
        String authHeader = Optional.ofNullable(System.getenv("TOKEN")).orElse("Bear 123");
        assertThrows(TokenException.class, () -> TokenUtil.getToken(authHeader));
    }

    @Test
    void testGetTokenWithNullAuth() {
        assertThrows(TokenException.class, () -> TokenUtil.getToken(""));
    }

    @Test
    void testGetTokenWithEmptyAuth() {
        String authHeader = "";
        assertThrows(TokenException.class, () -> TokenUtil.getToken(authHeader));
    }

    @Test
    void testAuthTypeLength() {
        assertEquals(7, TokenUtil.AUTH_LENGTH);
    }

}

