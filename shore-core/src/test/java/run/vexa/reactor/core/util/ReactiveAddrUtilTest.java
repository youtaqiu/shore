package run.vexa.reactor.core.util;


import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Reactive address util unit test.
 *
 * @author rained
 **/
class ReactiveAddrUtilTest {


    @Test
    void testGetRemoteAddrFromXForwardedFor() {
        ServerHttpRequest request = MockServerHttpRequest.get("/")
                .header("X-Forwarded-For", "192.168.1.1")
                .build();

        String remoteAddr = ReactiveAddrUtil.getRemoteAddr(request);
        assertEquals("192.168.1.1", remoteAddr);
    }


    @Test
    void testGetRemoteAddrFromRemoteAddress() {
        // 获取回环地址（通常为127.0.0.1或其他本地地址）
        InetAddress inetAddress = InetAddress.getLoopbackAddress();
        InetSocketAddress inetSocketAddress = new InetSocketAddress(inetAddress, 8080);

        // 模拟请求，设置远程地址为回环地址
        ServerHttpRequest request = MockServerHttpRequest.get("/")
                .remoteAddress(inetSocketAddress)
                .build();

        // 调用方法获取远程地址
        ReactiveAddrUtil.getRemoteAddr(request);

        // 断言返回的地址确实是回环地址
        assertTrue(inetAddress.isLoopbackAddress(), "The returned address should be a loopBack address.");
    }


    @Test
    void testGetRemoteAddrFromUnknownHeaders() {
        // 模拟一个远程地址（可以是任意的有效IP）
        InetSocketAddress remoteAddress = new InetSocketAddress("198.18.2.16", 8080);

        // 模拟请求，包含无效的 X-Forwarded-For 头，设置远程地址
        ServerHttpRequest request = MockServerHttpRequest.get("/")
                .header("X-Forwarded-For", "unknown")
                .remoteAddress(remoteAddress)
                .build();

        // 调用方法
        String remoteAddr = ReactiveAddrUtil.getRemoteAddr(request);

        // 断言返回值等于设置的远程地址
        assertEquals(remoteAddress.getAddress().getHostAddress(), remoteAddr,
                "Remote address should match the IP set in the request's remote address.");
    }


    @Test
    void testGetLocalAddr() {
        String localAddr = ReactiveAddrUtil.getLocalAddr();
        assertNotNull(localAddr);
        assertFalse(localAddr.isEmpty());
    }

    @Test
    void testExtractFirstNonEmptyIp() {
        // 使用ReflectionTestUtils来调用private方法
        String result = ReflectionTestUtils.invokeMethod(ReactiveAddrUtil.class,
                "extractFirstNonEmptyIp", "192.168.1.1, 192.168.1.2");
        assert result != null;
        assertEquals("192.168.1.1", result.trim());

        result = ReflectionTestUtils.invokeMethod(ReactiveAddrUtil.class, "extractFirstNonEmptyIp",
                "unknown, 192.168.1.1");
        assert result != null;
        assertEquals("192.168.1.1", result.trim());
    }


    @Test
    void testGetIpFromHeadersWithValidIp() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Forwarded-For", "192.168.1.1");

        String result = ReflectionTestUtils.invokeMethod(ReactiveAddrUtil.class,
                "getIpFromHeaders", headers);
        assertEquals("192.168.1.1", result, "The method should return the valid IP address from the headers");
    }


    @Test
    void testGetIpFromHeadersWithUnknownIp() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Forwarded-For", "unknown");

        String result = ReflectionTestUtils.invokeMethod(ReactiveAddrUtil.class,
                "getIpFromHeaders", headers);
        assertNull(result, "The method should return null when all IPs are unknown");
    }

    @Test
    void testGetIpFromHeadersWithEmptyIp() {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Forwarded-For", "");

        String result = ReflectionTestUtils.invokeMethod(ReactiveAddrUtil.class,
                "getIpFromHeaders", headers);
        assertNull(result, "The method should return null when all IPs are empty");
    }

    @Test
    void testGetIpFromHeadersWithNoIpHeaders() {
        Map<String, String> headers = new HashMap<>();

        String result = ReflectionTestUtils.invokeMethod(ReactiveAddrUtil.class,
                "getIpFromHeaders", headers);
        assertNull(result, "The method should return null when there are no IP headers");
    }

}

