package com.example.spring.boot.cache.support;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.map.event.EntryEvent;
import org.redisson.api.map.event.EntryExpiredListener;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.Cache;
import org.springframework.core.Ordered;

import com.example.spring.boot.cache.model.CacheMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 缓存监听器
 * <p>
 *
 * @author : 21
 * @since : 2023/10/19 11:17
 */

@Slf4j
public class RedissonCaffeineCacheListener implements ApplicationRunner, Ordered {

    private final RedissonCaffeineCacheManager cacheManager;

    private final RedissonClient redissonClient;

    private final String topic;

    private final long lockLeaseTime;

    public RedissonCaffeineCacheListener(RedissonCaffeineCacheManager cacheManager, RedissonClient redissonClient, String topic, long lockLeaseTime) {
        this.cacheManager = cacheManager;
        this.redissonClient = redissonClient;
        this.topic = topic;
        this.lockLeaseTime = lockLeaseTime;
    }

    @Override
    public void run(ApplicationArguments args) {
        //由于实现ApplicationRunner，所以在应用启动后就注册监听器（监听手动清理后 清理一级缓存）
        RTopic topic = RedissonTopicUtils.getTopic(redissonClient, this.topic);
        topic.addListenerAsync(CacheMessage.class, ((channel, msg) -> {
            boolean isClear = cacheManager.clearCaffeineCache(msg.getCacheName(), msg.getKey());
            if (isClear && log.isDebugEnabled()) {
                log.debug("caffeine cache clear finished, msg is [{}]", msg);
            }
        }));
        //监听缓存过期
        cacheManager.getExpireCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache instanceof RedissonCaffeineCache) {
                RedissonCaffeineCache redissonCaffeineCache = (RedissonCaffeineCache) cache;
                com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache = redissonCaffeineCache.getCaffeineCache();
                RMapCache<Object, Object> mapCache = redissonClient.getMapCache(cacheName);
                mapCache.addListener((EntryExpiredListener<Object, Object>) event -> entryExpired(cacheName, redissonCaffeineCache, caffeineCache, event));
                //TODO 还能再优化
            }
        });

    }

    private void entryExpired(String cacheName, RedissonCaffeineCache redissonCaffeineCache, com.github.benmanes.caffeine.cache.Cache<Object, Object> caffeineCache, EntryEvent<Object, Object> event) {
        Object key = event.getKey();
        // 缓存过期后清理本地缓存
        caffeineCache.invalidate(key);
        // 获取分布式锁
        RLock lock = redissonClient.getLock(cacheName + "_" + key);
        try {
            boolean tryLock = lock.tryLock(0L, lockLeaseTime, TimeUnit.MILLISECONDS);
            if (tryLock) {
                // 拿到锁的节点发送清理全局消息
                redissonCaffeineCache.publish(key, "entryExpired", false);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            //异步解锁
            if (lock.isHeldByCurrentThread()) {
                lock.forceUnlockAsync();
            }
        }
        if (log.isDebugEnabled()) {
            log.debug("caffeine cache entryExpired finished, cacheName is [{}], key is [{}]", cacheName, key);
        }
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
