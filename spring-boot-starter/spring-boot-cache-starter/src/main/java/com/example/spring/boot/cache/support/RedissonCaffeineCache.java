package com.example.spring.boot.cache.support;

import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Callable;

import org.redisson.api.RMap;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.NullValue;
import org.redisson.spring.cache.RedissonCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.context.ApplicationContext;

import com.example.commons.core.utils.ThreadUtils;
import com.example.spring.boot.cache.model.CacheMessage;
import com.github.benmanes.caffeine.cache.Cache;

import cn.hutool.core.util.SerializeUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * SpringCahce扩展
 * <p>
 *
 * @author : 21
 * @since : 2023/10/19 9:19
 */

@Slf4j
public class RedissonCaffeineCache extends RedissonCache {

    @Getter
    private final Cache<Object, Object> caffeineCache;

    private final String topic;

    private final boolean vagueEvict;

    private final boolean clone;

    private final RedissonClient redissonClient;

    private final ApplicationContext applicationContext;

    private final boolean allowNullValues;

    public RedissonCaffeineCache(ApplicationContext applicationContext, RedissonClient redissonClient, RMapCache<Object, Object> mapCache, CacheConfig config, Cache<Object, Object> caffeineCache, String topic, boolean allowNullValues, boolean vagueEvict, boolean clone) {
        super(mapCache, config, allowNullValues);
        this.applicationContext = applicationContext;
        this.caffeineCache = caffeineCache;
        this.allowNullValues = allowNullValues;
        this.redissonClient = redissonClient;
        this.topic = topic;
        this.vagueEvict = vagueEvict;
        this.clone = clone;
    }

    public RedissonCaffeineCache(ApplicationContext applicationContext, RedissonClient redissonClient, RMap<Object, Object> map, Cache<Object, Object> caffeineCache, String topic, boolean allowNullValues, boolean vagueEvict, boolean clone) {
        super(map, allowNullValues);
        this.applicationContext = applicationContext;
        this.caffeineCache = caffeineCache;
        this.allowNullValues = allowNullValues;
        this.redissonClient = redissonClient;
        this.topic = topic;
        this.vagueEvict = vagueEvict;
        this.clone = clone;
    }

    @Override
    public ValueWrapper get(Object key) {
        Object value = getCaffeineCacheIfClone(key);
        if (Objects.nonNull(value)) {
            printCaffeineDebugLog(key);
            return reValueWrapper(value);
        }
        //获取二级缓存
        ValueWrapper redisValue = super.get(key);
        if (Objects.nonNull(redisValue)) {
            Object redisObj = redisValue.get();
            if (Objects.nonNull(redisObj)) {
                //填充一级缓存
                caffeineCache.put(key, redisObj);
                printRedisDebugLog(key);
            }
        }
        return redisValue;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        Object value = getCaffeineCacheIfClone(key);
        if (Objects.nonNull(value)) {
            printCaffeineDebugLog(key);
            return (T) value;
        }
        T redisValue = super.get(key, type);
        if (Objects.nonNull(redisValue)) {
            caffeineCache.put(key, redisValue);
            printRedisDebugLog(key);
        }

        return redisValue;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        Object value = getCaffeineCacheIfClone(key);
        if (Objects.nonNull(value)) {
            printCaffeineDebugLog(key);
            return (T) value;
        }
        T redisValue = super.get(key, valueLoader);
        if (Objects.nonNull(redisValue)) {
            caffeineCache.put(key, redisValue);
            printRedisDebugLog(key);
        }
        return redisValue;
    }

    @Override
    public void put(Object key, Object value) {
        if (!allowNullValues && value == null) {
            this.evict(key);
            return;
        }
        super.put(key, value);
        //一级二级缓存都不存在时，这里可能会导致第一次放入一级缓存时 刚放就被删除
        //但是考虑到会存在集群模式，为了保证数据一致性，第二次调用get方法时，才会将一级缓存放进去
        //第一次：get() -> 一级二级都不存在 -> 调用put（将返回值存到一级二级） -> 订阅先执行（没问题）/ 订阅后执行（刚放的一级缓存会被删除）-> 执行下一行注释
        //第一次：get() -> 一级不存在 二级存在 -> 二级放一级 -> 返回二级 -> 下次再查 没问题
        publish(key, "put");
        caffeineCache.put(key, toStoreValue(value));
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper valueWrapper = super.putIfAbsent(key, value);
        publish(key, "putIfAbsent");
        caffeineCache.put(key, toStoreValue(value));
        return valueWrapper;
    }

    @Override
    public void evict(Object key) {
        // 先清理redis，在清理本地缓存，避免短时间内请求会从redis里加载到本地缓存
        if (isVagueAction(key)) {
            RMap<Object, Object> nativeCache = (RMap<Object, Object>) super.getNativeCache();
            doVagueEvictAsync(nativeCache, key);
        } else {
            super.evict(key);
            publish(key, "evict");
        }
    }

    @Override
    public void clear() {
        // 先清理redis，在清理本地缓存，避免短时间内请求会从redis里加载到本地缓存
        super.clear();
        publish(null, "clear");
    }

    /**
     * 是否为模糊清理操作
     *
     * @param key
     * @return
     */
    private boolean isVagueAction(Object key) {
        return vagueEvict && key instanceof String && ((String) key).contains("*");
    }

    /**
     * 执行模糊删除，只对Key类型为String生效 Async
     *
     * @param nativeCache
     * @param key
     */
    protected void doVagueEvictAsync(RMap<Object, Object> nativeCache, Object key) {
        ThreadUtils.submit(() -> {
            Set<Object> objs = nativeCache.keySet("\"" + key, 64);
            Iterator<Object> iterator = objs.iterator();
            while (iterator.hasNext()) {
                nativeCache.fastRemoveAsync(iterator.next());
            }
            publish(key, "evictAsync");
        });
    }

    /**
     * 发送缓存变动消息
     *
     * @param key
     * @param reason
     * @param isSibling 是否清理兄弟节点
     */
    public void publish(Object key, String reason, boolean isSibling) {
        CacheMessage message = new CacheMessage(super.getName(), key);
        if (log.isDebugEnabled()) {
            log.debug("caffeine redisson cache push message, topic is [{}], reason is [{}], message is [{}]", this.topic, reason, message);
        }
        if (isSibling) {
            RedissonTopicUtils.getTopic(redissonClient, this.topic).publishAsync(message);
        }
    }

    /**
     * 发送缓存变动消息
     *
     * @param key
     * @param reason
     */
    public void publish(Object key, String reason) {
        publish(key, reason, true);
    }

    private void printCaffeineDebugLog(Object key) {
        if (log.isDebugEnabled()) {
            log.debug("get cache from caffeine, the cache name is [{}], key is [{}]", super.getName(), key);
        }
    }

    private void printRedisDebugLog(Object key) {
        if (log.isDebugEnabled()) {
            log.debug("get cache from redisson and put in caffeine, the cache name is [{}], key is [{}]", super.getName(), key);
        }

    }

    /**
     * 缓存值的包装
     *
     * @param value
     * @return
     */
    private ValueWrapper reValueWrapper(Object value) {
        if (Objects.isNull(value)) {
            return null;
        }
        if (value.getClass().isAssignableFrom(NullValue.class)) {
            return NullValue.INSTANCE;
        }
        return new SimpleValueWrapper(value);
    }

    /**
     * 获取一级缓存值
     * 如果开启克隆，则返回克隆值，不操作元数据
     *
     * @param key
     * @return
     */
    private Object getCaffeineCacheIfClone(Object key) {
        Object value = caffeineCache.getIfPresent(key);
        if (Objects.nonNull(value) && clone) {
            value = SerializeUtil.clone(value);
        }
        return value;
    }

    /**
     * 清理CaffeineCache
     *
     * @param key
     */
    public void clearCaffeineCache(Object key) {
        if (key == null || isVagueAction(key)) {
            caffeineCache.invalidateAll();
        } else {
            caffeineCache.invalidate(key);
        }
        if (log.isDebugEnabled()) {
            log.debug("clear caffeine cache, the cache name is [{}], key is [{}]", super.getName(), key);
        }
    }

}
