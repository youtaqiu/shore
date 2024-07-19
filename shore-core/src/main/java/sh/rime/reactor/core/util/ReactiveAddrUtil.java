package sh.rime.reactor.core.util;

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
        String ip = getIpFromHeaders(headers);
        if (ip == null) {
            ip = Objects.requireNonNull(request.getRemoteAddress()).getAddress().getHostAddress();
            if (LOCAL_IP.equals(ip) || LOCAL_IP_STR.equals(ip)) {
                ip = getLocalAddr();
            }
        } else if (ip.length() > IP_MAX_LENGTH) {
            ip = extractFirstNonEmptyIp(ip);
        }
        return ip;
    }

    /**
     * get ip from headers.
     * @param headers headers
     * @return ip
     */
    private static String getIpFromHeaders(Map<String, String> headers) {
        String[] headerKeys = {"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "HTTP_X_FORWARDED_FOR"};
        for (String key : headerKeys) {
            String ip = headers.get(key);
            if (isEmpty(ip)) {
                return ip;
            }
        }
        return null;
    }

    /**
     * extract first not empty ip.
     * @param ip ip
     * @return ip
     */
    private static String extractFirstNonEmptyIp(String ip) {
        String[] ips = ip.split(",");
        for (String strIp : ips) {
            if (isEmpty(strIp)) {
                return strIp;
            }
        }
        return ip;
    }


    /**
     * 判断ip是否为空.
     * @param ip ip
     * @return boolean
     */
    private static boolean isEmpty(String ip) {
        return StringUtils.hasLength(ip) && !UNKNOWN_STR.equalsIgnoreCase(ip);
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
