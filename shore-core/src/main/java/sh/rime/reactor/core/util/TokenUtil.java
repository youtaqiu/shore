package sh.rime.reactor.core.util;

import cn.hutool.core.util.StrUtil;
import sh.rime.reactor.commons.constants.Constants;
import sh.rime.reactor.commons.exception.TokenException;
import lombok.extern.slf4j.Slf4j;


/**
 * Token工具类
 *
 * @author youta
 */
@SuppressWarnings("unused")
@Slf4j
public class TokenUtil {

    private TokenUtil() {
    }

    /**
     * token长度
     */
    public static final Integer AUTH_LENGTH = Constants.TOKEN_TYPE.length();

    /**
     * 获取token串
     *
     * @param auth token
     * @return String
     */
    public static String getToken(String auth) {
        if ((auth != null) && (auth.length() > AUTH_LENGTH)) {
            String headStr = StrUtil.trim(auth);
            return auth.substring(AUTH_LENGTH);
        }
        throw new TokenException();
    }

}
