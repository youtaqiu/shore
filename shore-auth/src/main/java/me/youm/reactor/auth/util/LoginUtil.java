package me.youm.reactor.auth.util;

import cn.dev33.satoken.session.SaSession;
import lombok.extern.slf4j.Slf4j;
import me.youm.reactor.common.entity.TokenInfo;
import me.youm.reactor.common.exception.TokenException;

import static me.youm.reactor.common.constants.AuthConstant.USER_SESSION_ID;


/**
 * @author youta
 **/
@Slf4j
public class LoginUtil {

    public static TokenInfo tokenSession(SaSession tokenSession){
        if (tokenSession ==null){
            throw new TokenException("Token has expired");
        }
        TokenInfo model = tokenSession.getModel(USER_SESSION_ID, TokenInfo.class);
        if (model == null)
            throw new TokenException("Token has expired");
        return model;
    }

}
