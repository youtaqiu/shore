package me.youm.reactor.common.utils;

import me.youm.reactor.common.constants.AuthConstant;
import me.youm.reactor.common.exception.TokenException;


/**
 * Token工具类
 *
 * @author youta
 */
public class TokenUtil {

    public final static Integer AUTH_LENGTH = AuthConstant.OAUTH2_TOKEN_TYPE.length() + 1;

    /**
     * 获取token串
     *
     * @param auth token
     * @return String
     */
    public static String getToken(String auth) {
        if ((auth != null) && (auth.length() > AUTH_LENGTH)) {
            String headStr = auth.substring(0, AUTH_LENGTH -1).toLowerCase();
            if (headStr.compareTo(AuthConstant.OAUTH2_TOKEN_TYPE) == 0) {
                auth = auth.substring(AUTH_LENGTH);
            }
            return auth;
        }
        throw new TokenException("invalid token information");
    }

}
