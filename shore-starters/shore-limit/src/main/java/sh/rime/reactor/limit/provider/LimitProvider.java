package sh.rime.reactor.limit.provider;

import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

/**
 * @author youta
 **/
public interface LimitProvider {

    /**
     * 在给定的时间段里最多的访问限制次数(超出次数返回false)；等下个时间段开始，才允许再次被访问(返回true)，周而复始
     *
     * @param key    资源Key
     * @param rate   最多的访问限制次数
     * @param expire 时间(单位秒)
     * @param unit   单位
     * @return boolean
     */
    Mono<Boolean> tryAcquire(String key, int rate, long expire, TimeUnit unit);

}
