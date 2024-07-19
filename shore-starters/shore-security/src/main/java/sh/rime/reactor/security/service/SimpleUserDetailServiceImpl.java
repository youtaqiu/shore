package sh.rime.reactor.security.service;

import sh.rime.reactor.security.domain.CurrentUser;
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
