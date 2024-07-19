package sh.rime.reactor.security.constants;

import cn.hutool.core.text.StrPool;
import org.springframework.http.HttpHeaders;


/**
 * @author youta
 **/
public class TokenConstants {

    /**
     * token缓存前缀
     */
    public static final String CACHE_PREFIX = HttpHeaders.AUTHORIZATION;

    /**
     * token缓存类型
     */
    public static final String CACHE_TYPE = "login";

    /**
     * token缓存名称
     */
    public static final String CACHE_NAME = "token";

    /**
     * 令牌集合缓存名称
     */
    public static final String CACHE_TOKEN_LIST = "user";

    /**
     * token session缓存名称
     */
    public static final String TOKEN_CACHE_NAME = "token-session";

    /**
     * 刷新token缓存名称
     */
    public static final String REFRESH_TOKEN_NAME = "refresh_token";

    /**
     * session缓存名称
     */
    public static final String SESSION_NAME = "session";

    /**
     * 获取token缓存key
     *
     * @param token token
     * @return 缓存key
     */
    public static String token(String token) {
        return String.join(StrPool.COLON, CACHE_PREFIX, CACHE_TYPE, CACHE_NAME, token);
    }


    /**
     * 获取token缓存key
     *
     * @param username 用户名
     * @return 缓存key
     */
    public static String tokenList(String username) {
        return String.join(StrPool.COLON, CACHE_PREFIX, CACHE_TYPE, CACHE_TOKEN_LIST, username);
    }

    /**
     * 获取token session缓存key
     *
     * @param token token
     * @return 缓存key
     */
    public static String tokenSession(String token) {
        return String.join(StrPool.COLON, CACHE_PREFIX, CACHE_TYPE, TOKEN_CACHE_NAME, token);
    }

    /**
     * 获取刷新token缓存key
     *
     * @param refreshToken 刷新token
     * @return 缓存key
     */
    public static String refresh(String refreshToken) {
        return String.join(StrPool.COLON, CACHE_PREFIX, CACHE_TYPE, REFRESH_TOKEN_NAME, refreshToken);
    }

    /**
     * 获取session缓存key
     *
     * @param token token
     * @return 缓存key
     */
    public static String session(String token) {
        return String.join(StrPool.COLON, CACHE_PREFIX, CACHE_TYPE, SESSION_NAME, token);
    }


}
