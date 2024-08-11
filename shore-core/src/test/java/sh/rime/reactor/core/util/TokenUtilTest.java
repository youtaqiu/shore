package sh.rime.reactor.core.util;

import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.exception.TokenException;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Token util unit test.
 *
 * @author rained
 **/
class TokenUtilTest {

    @Test
    void testGetTokenSuccess() {
        String authHeader = "Bearer abcdefghijklmnopqrstuvwxyz";
        String token = TokenUtil.getToken(authHeader);
        assertEquals("abcdefghijklmnopqrstuvwxyz", token);
    }

    @Test
    void testGetTokenWithInvalidLength() {
        String authHeader = "Bear abcdefghijklmnopqrstuvwxyz";
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
}

