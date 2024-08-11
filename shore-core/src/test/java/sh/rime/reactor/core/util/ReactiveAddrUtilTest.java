package sh.rime.reactor.core.util;


import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Reactive address util unit test.
 *
 * @author rained
 **/
class ReactiveAddrUtilTest {

    @Test
    void testGetRemoteAddrFromHeaders() {
        ServerHttpRequest request = MockServerHttpRequest.get("/")
                .header("X-Forwarded-For", "192.168.1.1")
                .build();

        String ip = ReactiveAddrUtil.getRemoteAddr(request);
        assertEquals("192.168.1.1", ip);
    }

    @Test
    void testGetRemoteAddrFromRemoteAddress() {
        InetAddress address = mock(InetAddress.class);
        when(address.getHostAddress()).thenReturn("192.168.1.100");

        ServerHttpRequest request = mock(ServerHttpRequest.class);
        when(request.getHeaders()).thenReturn(new HttpHeaders());
        when(request.getRemoteAddress()).thenReturn(new InetSocketAddress(address, 8080));

        String ip = ReactiveAddrUtil.getRemoteAddr(request);
        assertEquals("192.168.1.100", ip);
    }

    @Test
    void testGetLocalAddr() throws UnknownHostException {
        String ip = ReactiveAddrUtil.getLocalAddr();
        assertEquals(InetAddress.getLocalHost().getHostAddress(), ip);
    }

    @Test
    void testGetLocalAddrException() throws Exception {
        // 使用 Mockito 模拟 InetAddress.getLocalHost 抛出 UnknownHostException
        try (MockedStatic<InetAddress> mockedInetAddress = mockStatic(InetAddress.class)) {
            mockedInetAddress.when(InetAddress::getLocalHost).thenThrow(new UnknownHostException());

            String ip = ReactiveAddrUtil.getLocalAddr();
            assertEquals("", ip);
        }
    }
}

