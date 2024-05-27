package io.irain.reactor.security.service;

import io.irain.reactor.security.domain.CurrentUser;
import reactor.core.publisher.Mono;

/**
 * @author youta
 **/
public class SimpleUserDetailServiceImpl implements UserDetailService{
    @Override
    public Mono<CurrentUser> loadByUsername(String username) {
        return null;
    }

}
