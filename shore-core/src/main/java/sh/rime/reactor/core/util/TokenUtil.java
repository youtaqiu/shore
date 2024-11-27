package sh.rime.reactor.core.util;

import org.springframework.util.StringUtils;
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
public final class TokenUtil {

    /**
     * 私有构造函数
     */
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
        if (!StringUtils.hasLength(auth) || !auth.startsWith(Constants.TOKEN_TYPE)) {
            throw new TokenException("Invalid token type");
        }
        if ((auth.length() > AUTH_LENGTH)) {
            return auth.substring(AUTH_LENGTH);
        }
        throw new TokenException();
    }

}
