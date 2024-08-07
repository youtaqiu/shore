package sh.rime.reactor.security.service;

import sh.rime.reactor.security.domain.CurrentUser;
import reactor.core.publisher.Mono;

/**
 * 用户详情服务
 *
 * @author youta
 * @see UserDetailService
 **/
public class SimpleUserDetailServiceImpl implements UserDetailService {

    /**
     * Default constructor.
     * This constructor is used for serialization and other reflective operations.
     */
    public SimpleUserDetailServiceImpl() {
    }

    @Override
    public Mono<CurrentUser> loadByUsername(String username) {
        return null;
    }

}
