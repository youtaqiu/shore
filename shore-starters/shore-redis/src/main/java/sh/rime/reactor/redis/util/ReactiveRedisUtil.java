package sh.rime.reactor.redis.util;

import sh.rime.reactor.redis.supprot.CustomizeReactiveRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;

/**
 * Redis工具类
 *
 * @author youta
 **/
@SuppressWarnings(value = {"unchecked","unused"})
@Slf4j
public class ReactiveRedisUtil {

    private final CustomizeReactiveRedisTemplate<String, Object> customizeReactiveRedisTemplate;

    /**
     * Instantiates a new Reactive redis util.
     *
     * @param customizeReactiveRedisTemplate the reactive redis template
     */
    public ReactiveRedisUtil(CustomizeReactiveRedisTemplate<String, Object> customizeReactiveRedisTemplate) {
        this.customizeReactiveRedisTemplate = customizeReactiveRedisTemplate;
    }


    /**
     * increment
     *
     * @param key   键
     * @param delta 时间(秒)  time要大于0 如果time小于等于0 将设置无限期
     * @return Long
     */
    public Mono<Long> increment(String key, long delta) {
        return customizeReactiveRedisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key     键
     * @param timeout 时间(秒)
     * @return Boolean
     */
    public Mono<Boolean> expire(String key, Duration timeout) {
        return customizeReactiveRedisTemplate.expire(key, timeout);
    }

    /**
     * 获取过期时间
     *
     * @param key 键
     * @return Duration
     */
    public Mono<Duration> getExpire(String key) {
        return customizeReactiveRedisTemplate.getExpire(key);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Mono<Boolean> hasKey(String key) {
        return customizeReactiveRedisTemplate.hasKey(key);
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值
     * @return Long
     */
    public Mono<Long> del(String key) {
        return customizeReactiveRedisTemplate.delete(key);
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @param <T> 类型
     * @return 值
     */
    public <T> Mono<T> get(String key) {
        if (key == null) {
            return Mono.empty();
        }
        ReactiveValueOperations<String, T> stringObjectReactiveValueOperations = (ReactiveValueOperations<String, T>) customizeReactiveRedisTemplate.opsForValue();
        return stringObjectReactiveValueOperations.get(key)
                .doOnSuccess(o -> log.debug("get key:{} value:{}", key, o));
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public Mono<Boolean> set(String key, Object value) {
        return customizeReactiveRedisTemplate.opsForValue().set(key, value);
    }


    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间
     * @return true成功 false 失败
     */
    public Mono<Boolean> set(String key, Object value, Duration time) {
        return customizeReactiveRedisTemplate.opsForValue().set(key, value, time);
    }

    /**
     * HashGet
     *
     * @param key  键 不能为 null
     * @param item 项 不能为 null
     * @param <T>  类型
     * @return 值
     */
    public <T> Mono<T> hashGet(String key, String item) {
        ReactiveHashOperations<String, String, T> stringObjectObjectReactiveHashOperations = customizeReactiveRedisTemplate.opsForHash();
        return stringObjectObjectReactiveHashOperations.get(key, item);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public Mono<Boolean> hashSet(String key, Map<String, Object> map) {
        return customizeReactiveRedisTemplate.opsForHash().putAll(key, map);
    }


    /**
     * HashSet 并设置时间
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒)
     * @return true成功 false失败
     */
    public Mono<Boolean> hSet(String key, String item, Object value, Long time) {
        return customizeReactiveRedisTemplate.opsForHash().put(key, item, value)
                .filter(flag -> time > 0)
                .flatMap(flag -> expire(key, Duration.ofSeconds(time)));
    }


    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public Mono<Boolean> hashSet(String key, Map<String, Object> map, Duration time) {
        return customizeReactiveRedisTemplate.opsForHash().putAll(key, map).flatMap(flag -> expire(key, time));
    }

    /**
     * 获取 hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Flux<Map.Entry<Object, Object>> hmget(String key) {
        return customizeReactiveRedisTemplate.opsForHash().entries(key);
    }

}
