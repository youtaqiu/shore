package sh.rime.reactor.commoms.enums;

import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.enums.CommonExceptionEnum;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * CommonExceptionEnum unit test.
 *
 * @author rained
 */
class CommonExceptionEnumTest {

    @Test
    void testUnauthorized() {
        CommonExceptionEnum unauthorized = CommonExceptionEnum.UNAUTHORIZED;
        assertEquals(401, unauthorized.code());
        assertEquals("Unauthorized", unauthorized.message());
    }

    @Test
    void testForbidden() {
        CommonExceptionEnum forbidden = CommonExceptionEnum.FORBIDDEN;
        assertEquals(403, forbidden.code());
        assertEquals("Access Denied", forbidden.message());
    }

}

