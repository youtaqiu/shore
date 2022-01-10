package me.youm.reactor.redis.service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Map;

/**
 * @author youta
 */
public class ReactiveRedisService {

    @Resource
    private ReactiveRedisTemplate<String, Object> reactiveRedisTemplate;

    /**
     * 指定缓存失效时间
     *
     * @param key     键
     * @param timeout 时间(秒)
     * @return Boolean
     */
    public Mono<Boolean> expire(String key, Duration timeout) {
        return reactiveRedisTemplate.expire(key, timeout);
    }

    /**
     * 判断 key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public Mono<Boolean> hasKey(String key) {
        return reactiveRedisTemplate.hasKey(key);
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    public Mono<Long> del(String key) {
        return reactiveRedisTemplate.delete(key);
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Mono<Object> get(String key) {
        return key == null ? Mono.empty() : reactiveRedisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public Mono<Boolean> set(String key, Object value) {
        return reactiveRedisTemplate.opsForValue().set(key, value);
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
        return reactiveRedisTemplate.opsForValue().set(key, value, time);
    }

    /**
     * HashGet
     *
     * @param key  键 不能为 null
     * @param item 项 不能为 null
     * @return 值
     */
    public Mono<Object> hashGet(String key, String item) {
        return reactiveRedisTemplate.opsForHash().get(key, item);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public Mono<Boolean> hashSet(String key, Map<String, Object> map) {
        return reactiveRedisTemplate.opsForHash().putAll(key, map);
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
        return reactiveRedisTemplate.opsForHash().putAll(key, map).flatMap(flag -> expire(key,time));
    }

    /**
     * 获取 hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Flux<Map.Entry<Object, Object>> hmget(String key) {
        return reactiveRedisTemplate.opsForHash().entries(key);
    }

}
