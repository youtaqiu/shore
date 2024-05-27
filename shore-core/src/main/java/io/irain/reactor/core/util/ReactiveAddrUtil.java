package io.irain.reactor.core.util;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;

/**
 * 响应式获取ip地址.
 *
 * @author youta
 **/
@Slf4j
public final class ReactiveAddrUtil {

    private ReactiveAddrUtil() {
    }

    private static final String UNKNOWN_STR = "unknown";
    private static final String LOCAL_IP = "127.0.0.1";
    private static final String LOCAL_IP_STR = "0:0:0:0:0:0:0:1";
    private static final int IP_MAX_LENGTH = 15;

    /**
     * 获取客户端IP地址.
     *
     * @param request request
     * @return ip
     */
    @SuppressFBWarnings("NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE")
    public static String getRemoteAddr(ServerHttpRequest request) {
        Map<String, String> headers = request.getHeaders().toSingleValueMap();
        String ip = headers.get("X-Forwarded-For");
        if (isEmpty(ip)) {
            ip = headers.get("Proxy-Client-IP");
            if (isEmpty(ip)) {
                ip = headers.get("WL-Proxy-Client-IP");
                if (isEmpty(ip)) {
                    ip = headers.get("HTTP_CLIENT_IP");
                    if (isEmpty(ip)) {
                        ip = headers.get("HTTP_X_FORWARDED_FOR");
                        if (isEmpty(ip)) {
                            ip = Objects.requireNonNull(request.getRemoteAddress())
                                    .getAddress().getHostAddress();
                            if (LOCAL_IP.equals(ip) || LOCAL_IP_STR.equals(ip)) {
                                ip = getLocalAddr();
                            }
                        }
                    }
                }
            }
        } else if (ip.length() > IP_MAX_LENGTH) {
            String[] ips = ip.split(",");
            for (String strIp : ips) {
                if (!isEmpty(ip)) {
                    ip = strIp;
                    break;
                }
            }
        }
        return ip;
    }

    private static boolean isEmpty(String ip) {
        return !StringUtils.hasLength(ip) || UNKNOWN_STR.equalsIgnoreCase(ip);
    }

    /**
     * 获取本机的IP地址.
     *
     * @return ip
     */
    public static String getLocalAddr() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("InetAddress.getLocalHost()-error", e);
        }
        return "";
    }

}
