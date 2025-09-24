package run.vexa.reactor.core.util;

import cn.hutool.core.util.IdUtil;
import org.junit.jupiter.api.Test;
import run.vexa.reactor.commons.constants.Constants;
import run.vexa.reactor.commons.exception.TokenException;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Token util unit test.
 *
 * @author rained
 **/
class TokenUtilTest {

    @Test
    void testGetTokenSuccess() {
        var authStr = IdUtil.fastSimpleUUID();
        String authHeader = Constants.TOKEN_TYPE.concat(authStr);
        String token = TokenUtil.getToken(authHeader);
        assertEquals(authStr, token);
    }

    @Test
    void testGetTokenWithInvalidLength() {
        var authStr = IdUtil.fastSimpleUUID();
        String authHeader = "Bear ".concat(authStr);
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
    void testGetTokenWithOnlyPrefix() {
        assertThrows(TokenException.class, () -> TokenUtil.getToken(Constants.TOKEN_TYPE));
    }

    @Test
    void testAuthTypeLength() {
        assertEquals(7, TokenUtil.AUTH_LENGTH);
    }

}
