package sh.rime.reactor.core.properties;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import sh.rime.reactor.commons.annotation.RequestMethodEnum;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Auth properties unit test.
 *
 * @author rained
 **/
class AuthPropertiesTest {

    private AuthProperties authProperties;

    @BeforeEach
    void setUp() {
        authProperties = new AuthProperties();
        authProperties.initIgnoreUrl();
    }

    @Test
    void testDefaultExcludePatterns() {
        Set<String> excludePatterns = authProperties.getExcludePatterns();

        assertTrue(excludePatterns.contains("/actuator/**"));
        assertTrue(excludePatterns.contains("/swagger-ui/**"));
        // 继续添加其他默认排除路径的断言
    }

    @Test
    void testExcludeWithGetMethod() {
        authProperties.getGetExcludePatterns().add("/api/test/**");

        boolean result = authProperties.exclude(RequestMethodEnum.GET, "/api/test/resource");

        assertTrue(result);
    }

    @Test
    void testExcludeWithPostMethod() {
        authProperties.setPostExcludePatterns(Set.of("/api/test/**"));

        boolean result = authProperties.exclude(RequestMethodEnum.POST, "/api/test/resource");

        assertTrue(result);
    }

    @Test
    void testExcludeWithNonMatchingPath() {
        authProperties.getGetExcludePatterns().add("/api/test/**");

        boolean result = authProperties.exclude(RequestMethodEnum.GET, "/api/other/resource");

        assertFalse(result);
    }

    @Test
    void testExcludeWithEmptyExcludePatterns() {
        authProperties.getGetExcludePatterns().clear(); // 清空GET排除路径

        boolean result = authProperties.exclude(RequestMethodEnum.GET, "/api/test/resource");

        assertFalse(result);
    }

    @Test
    void testCustomLoginLogoutPatterns() {
        authProperties.setLoginPattern("/custom/login");
        authProperties.setLogoutPattern("/custom/logout");

        assertEquals("/custom/login", authProperties.getLoginPattern());
        assertEquals("/custom/logout", authProperties.getLogoutPattern());
    }
}

