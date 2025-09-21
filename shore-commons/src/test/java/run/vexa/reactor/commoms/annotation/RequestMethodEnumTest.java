package run.vexa.reactor.commoms.annotation;


import org.junit.jupiter.api.Test;
import run.vexa.reactor.commons.annotation.RequestMethodEnum;

import static org.junit.jupiter.api.Assertions.*;

/**
 * RequestMethodEnum unit test.
 *
 * @author rained
 **/
class RequestMethodEnumTest {

    @Test
    void testFind() {
        assertEquals(RequestMethodEnum.GET, RequestMethodEnum.find("GET"));
        assertEquals(RequestMethodEnum.POST, RequestMethodEnum.find("POST"));
        assertEquals(RequestMethodEnum.PUT, RequestMethodEnum.find("PUT"));
        assertEquals(RequestMethodEnum.PATCH, RequestMethodEnum.find("PATCH"));
        assertEquals(RequestMethodEnum.DELETE, RequestMethodEnum.find("DELETE"));
        assertEquals(RequestMethodEnum.ALL, RequestMethodEnum.find("HEAD")); // HEAD is not defined, should return ALL
    }

    @Test
    void testFindNull() {
        assertEquals(RequestMethodEnum.ALL, RequestMethodEnum.find(null));
    }

    @Test
    void testFindEmptyString() {
        assertEquals(RequestMethodEnum.ALL, RequestMethodEnum.find(""));
    }
}

