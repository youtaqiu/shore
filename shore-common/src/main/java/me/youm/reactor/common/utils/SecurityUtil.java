package me.youm.reactor.common.utils;

import me.youm.reactor.common.constants.AuthConstant;
import me.youm.reactor.common.exception.TokenException;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;


/**
 * @author youta
 */
public class SecurityUtil {


    public static String getToken(ServerWebExchange exchange) {
        String headerToken = getHeaderToken(exchange);
        if (!StringUtils.hasText(headerToken)) {
            throw new TokenException("No token information");
        }
        return TokenUtil.getToken(headerToken);
    }

    public static String getHeaderToken(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(AuthConstant.HEADER_TOKEN);
    }
}
