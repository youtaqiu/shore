package sh.rime.reactor.security.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.text.StrPool;
import sh.rime.reactor.commons.exception.TokenException;
import sh.rime.reactor.core.util.TokenUtil;
import sh.rime.reactor.security.domain.ClientInfo;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;


/**
 * @author youta
 **/
public class ClientUtil {

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
