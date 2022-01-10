package me.youm.reactor.auth.context;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import me.youm.reactor.common.context.ReactiveRequestContextHolder;
import me.youm.reactor.common.entity.TokenInfo;
import me.youm.reactor.common.utils.SecurityUtil;
import me.youm.reactor.common.exception.TokenException;
import reactor.core.publisher.Mono;

import java.io.Serializable;

import static me.youm.reactor.common.constants.AuthConstant.USER_SESSION_ID;


/**
 * @author youta
 **/
public class CurrentUserContext implements Serializable {

    public static SaTokenInfo login(String username) {
        StpUtil.login(username);
        return StpUtil.getTokenInfo();
    }

    public static void logout() {
        if (StpUtil.isLogin()){
            StpUtil.logout();
        }
    }


    public static Mono<TokenInfo> reactiveUser() {
        return ReactiveRequestContextHolder.getExchange()
                .map(SecurityUtil::getToken)
                .onErrorResume(Mono::error)
                .map(StpUtil::getTokenSessionByToken)
                .map(CurrentUserContext::tokenSession)
                ;
    }


    private static TokenInfo tokenSession(SaSession tokenSession){
        TokenInfo model = tokenSession.getModel(USER_SESSION_ID, TokenInfo.class);
        if (model == null) {
            throw new TokenException("Token has expired");
        }
        return model;
    }

    public static void user(TokenInfo user) {
        SaSession saSession = StpUtil.getTokenSessionByToken(user.getAccessToken());
        saSession.set(USER_SESSION_ID, user);
    }

    public static TokenInfo refresh(String access_token) {
        StpUtil.checkActivityTimeout();
        StpUtil.updateLastActivityToNow();
        SaSession saSession = StpUtil.getTokenSessionByToken(access_token);
        TokenInfo user = saSession.getModel(USER_SESSION_ID, TokenInfo.class);
        long timeout = saSession.getTimeout();
        user.setExpiresIn(timeout);
        saSession.set(USER_SESSION_ID, user);
        return user;
    }
}
