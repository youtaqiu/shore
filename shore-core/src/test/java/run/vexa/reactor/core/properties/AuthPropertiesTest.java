package run.vexa.reactor.core.properties;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import run.vexa.reactor.commons.annotation.RequestMethodEnum;

import java.util.HashSet;
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
        // 模拟不同方法的排除路径
        authProperties.setGetExcludePatterns(Set.of("/get/excluded/**"));
        authProperties.setPostExcludePatterns(Set.of("/post/excluded/**"));
        authProperties.setPutExcludePatterns(Set.of("/put/excluded/**"));
        authProperties.setDeleteExcludePatterns(Set.of("/delete/excluded/**"));
        authProperties.setPatchExcludePatterns(Set.of("/patch/excluded/**"));
        authProperties.getExcludePatterns().add("/all/excluded/**");
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
    void testExcludeWithPostMethod() {
        authProperties.setPostExcludePatterns(Set.of("/api/test/**"));

        boolean result = authProperties.exclude(RequestMethodEnum.POST, "/api/test/resource");

        assertTrue(result);
    }


    @Test
    void testCustomLoginLogoutPatterns() {
        authProperties.setLoginPattern("/custom/login");
        authProperties.setLogoutPattern("/custom/logout");

        assertEquals("/custom/login", authProperties.getLoginPattern());
        assertEquals("/custom/logout", authProperties.getLogoutPattern());
    }

    @Test
    void testSetPostExcludePatterns() {
        Set<String> patterns = new HashSet<>();
        patterns.add("/api/users");
        authProperties.setPostExcludePatterns(patterns);
        assertTrue(authProperties.exclude(RequestMethodEnum.POST, "/api/users"));
        // 测试unmodifiableSet
        assertFalse(authProperties.exclude(RequestMethodEnum.POST, "/api/test"));
    }

    @Test
    void testSetPutExcludePatterns() {
        Set<String> patterns = new HashSet<>();
        patterns.add("/api/items/*");
        authProperties.setPutExcludePatterns(patterns);
        assertTrue(authProperties.exclude(RequestMethodEnum.PUT, "/api/items/123"));
        // 测试unmodifiableSet
        assertFalse(authProperties.exclude(RequestMethodEnum.PUT, "/api/test"));
    }

    @Test
    void testSetPatchExcludePatterns() {
        Set<String> patterns = new HashSet<>();
        patterns.add("/api/products");
        authProperties.setPatchExcludePatterns(patterns);
        assertTrue(authProperties.exclude(RequestMethodEnum.PATCH, "/api/products"));
        // 测试unmodifiableSet
        assertFalse(authProperties.exclude(RequestMethodEnum.PATCH, "/api/test"));
    }

    @Test
    void testSetDeleteExcludePatterns() {
        Set<String> patterns = new HashSet<>();
        patterns.add("/api/orders/**");
        authProperties.setDeleteExcludePatterns(patterns);
        assertTrue(authProperties.exclude(RequestMethodEnum.DELETE, "/api/orders/456"));
        // 测试unmodifiableSet
        assertFalse(authProperties.exclude(RequestMethodEnum.DELETE, "/api/test"));
    }

    @Test
    void testSetGetExcludePatterns() {
        Set<String> patterns = new HashSet<>();
        patterns.add("/api/orders/**");
        authProperties.setGetExcludePatterns(patterns);
        assertTrue(authProperties.exclude(RequestMethodEnum.GET, "/api/orders/456"));
        // 测试unmodifiableSet
        assertFalse(authProperties.exclude(RequestMethodEnum.GET, "/api/test"));
    }


    @Test
    void testGettersAndSetters() {
        // 测试 enable
        authProperties.setEnable(false);
        assertFalse(authProperties.getEnable());

        // 测试 loginPattern
        authProperties.setLoginPattern("/api/auth/login");
        assertEquals("/api/auth/login", authProperties.getLoginPattern());

        // 测试 logoutPattern
        authProperties.setLogoutPattern("/api/auth/logout");
        assertEquals("/api/auth/logout", authProperties.getLogoutPattern());

        // 测试 getExcludePatterns
        Set<String> patterns = Set.of("/test1", "/test2");
        authProperties.setGetExcludePatterns(patterns);
        assertEquals(patterns, authProperties.getGetExcludePatterns());
    }

    @Test
    void testGetExclude() {
        assertTrue(authProperties.exclude(RequestMethodEnum.GET, "/get/excluded/test"),
                "GET request should be excluded by the pattern");
        assertFalse(authProperties.exclude(RequestMethodEnum.GET, "/get/included/test"),
                "GET request should not be excluded by the pattern");
    }

    @Test
    void testPostExclude() {
        assertTrue(authProperties.exclude(RequestMethodEnum.POST, "/post/excluded/test"),
                "POST request should be excluded by the pattern");
        assertFalse(authProperties.exclude(RequestMethodEnum.POST, "/post/included/test"),
                "POST request should not be excluded by the pattern");
    }

    @Test
    void testPutExclude() {
        assertTrue(authProperties.exclude(RequestMethodEnum.PUT, "/put/excluded/test"),
                "PUT request should be excluded by the pattern");
        assertFalse(authProperties.exclude(RequestMethodEnum.PUT, "/put/included/test"),
                "PUT request should not be excluded by the pattern");
    }

    @Test
    void testDeleteExclude() {
        assertTrue(authProperties.exclude(RequestMethodEnum.DELETE, "/delete/excluded/test"),
                "DELETE request should be excluded by the pattern");
        assertFalse(authProperties.exclude(RequestMethodEnum.DELETE, "/delete/included/test"),
                "DELETE request should not be excluded by the pattern");
    }

    @Test
    void testPatchExclude() {
        assertTrue(authProperties.exclude(RequestMethodEnum.PATCH, "/patch/excluded/test"),
                "PATCH request should be excluded by the pattern");
        assertFalse(authProperties.exclude(RequestMethodEnum.PATCH, "/patch/included/test"),
                "PATCH request should not be excluded by the pattern");
    }

    @Test
    void testAllExclude() {
        assertTrue(authProperties.exclude(RequestMethodEnum.ALL, "/all/excluded/test"),
                "ALL request should be excluded by the pattern");
        assertFalse(authProperties.exclude(RequestMethodEnum.ALL, "/all/included/test"),
                "ALL request should not be excluded by the pattern");
    }


    @Test
    void testExcludePrivateMethodWhenPatternsMatch() {
        // 通过反射调用私有方法
        boolean result = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(
                authProperties, "exclude", "/docs"));
        assertTrue(result, "Path should be excluded by the pattern");

        result = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(
                authProperties, "exclude", "/swagger-resources"));
        assertTrue(result, "Path should be excluded by the pattern");
    }

    @Test
    void testExcludePrivateMethodWhenPatternsDoNotMatch() {
        // 通过反射调用私有方法
        boolean result = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(
                authProperties, "exclude", "/included/test"));
        assertFalse(result, "Path should not be excluded by the pattern");

        result = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(
                authProperties, "exclude", "/some/other/path"));
        assertFalse(result, "Path should not be excluded by the pattern");
    }

    @Test
    void testExcludePrivateMethodWhenNoPatternsSet() {
        // 清空排除模式集合
        authProperties.getExcludePatterns().clear();

        // 当没有设置任何排除模式时，所有路径都不应该被排除
        boolean result = Boolean.TRUE.equals(ReflectionTestUtils.invokeMethod(
                authProperties, "exclude", "/any/path"));
        assertFalse(result, "Path should not be excluded because no patterns are set");
    }

}

