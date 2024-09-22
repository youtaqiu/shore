package sh.rime.reactor.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import sh.rime.reactor.security.domain.CurrentUser;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户详情服务
 *
 * @author youta
 * @see UserDetailService
 **/
public class SimpleUserDetailServiceImpl implements UserDetailService {

    private final Map<String, CurrentUser> users;

    /**
     * Creates a new instance using a {@link Map} that must be non blocking.
     *
     * @param users a {@link Map} of users to use.
     */
    public SimpleUserDetailServiceImpl(Map<String, CurrentUser> users) {
        this.users = users;
    }

    /**
     * Creates a new instance
     *
     * @param users the {@link UserDetails} to use
     */
    public SimpleUserDetailServiceImpl(CurrentUser... users) {
        this(Arrays.asList(users));
    }

    /**
     * Creates a new instance
     *
     * @param users the {@link CurrentUser} to use
     */
    public SimpleUserDetailServiceImpl(Collection<CurrentUser> users) {
        Assert.notEmpty(users, "users cannot be null or empty");
        this.users = new ConcurrentHashMap<>();
        for (CurrentUser user : users) {
            this.users.put(getKey(user.getUsername()), user);
        }
    }


    @Override
    public Mono<CurrentUser> loadByUsername(String username) {
        String key = getKey(username);
        CurrentUser result = this.users.get(key);
        return (result != null) ? Mono.just(result) : Mono.empty();
    }

    private String getKey(String username) {
        return username.toLowerCase();
    }

}
