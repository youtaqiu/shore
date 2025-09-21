package run.vexa.reactor.security.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.text.StrPool;
import run.vexa.reactor.commons.exception.TokenException;
import run.vexa.reactor.core.util.TokenUtil;
import run.vexa.reactor.security.domain.ClientInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;


/**
 * ClientUtil is a utility class that provides methods for getting client information.
 *
 * @author youta
 **/
public class ClientUtil {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public ClientUtil() {
    }

    /**
     * 获取客户端信息
     *
     * @param exchange 请求
     * @return ClientInfo
     */
    public static ClientInfo getClient(ServerWebExchange exchange) {
        String headerToken = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        String base64Token = TokenUtil.getToken(headerToken);
        String token;
        try {
            token = Base64.decodeStr(base64Token);
        } catch (IllegalArgumentException var7) {
            throw new TokenException("Failed to decode basic authentication token");
        }
        int index = token.indexOf(StrPool.COLON);
        if (index == -1) {
            throw new TokenException("Invalid basic authentication token");
        } else {
            String[] strings = {token.substring(0, index), token.substring(index + 1)};
            return ClientInfo.base(strings[0], strings[1]);
        }

    }

}
