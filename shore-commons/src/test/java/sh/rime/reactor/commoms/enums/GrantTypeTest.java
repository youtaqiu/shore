package sh.rime.reactor.commoms.enums;


import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.enums.GrantType;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GrantType unit test.
 *
 * @author rained
 **/
class GrantTypeTest {

    @Test
    void testGrantTypeValues() {
        assertEquals("password", GrantType.PASSWORD.getValue());
        assertEquals("sms_code", GrantType.SMS_CODE.getValue());
        assertEquals("authorization_code", GrantType.AUTHORIZATION_CODE.getValue());
        assertEquals("client_credential", GrantType.CLIENT_CREDENTIAL.getValue());
        assertEquals("refresh_token", GrantType.REFRESH_TOKEN.getValue());
    }

    @Test
    void testGrantTypeLookup() {
        assertEquals(GrantType.PASSWORD, getGrantType("password"));
        assertEquals(GrantType.SMS_CODE, getGrantType("sms_code"));
        assertThrows(IllegalArgumentException.class, () -> getGrantType("invalid_type"));
    }

    private GrantType getGrantType(String value) {
        for (GrantType type : GrantType.values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid grant type: " + value);
    }
}

