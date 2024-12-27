package com.example.spring.boot.cache.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import com.example.spring.boot.cache.config.RedissonCaffeineCacheConfig;

import lombok.Data;

/**
 * <p>
 * 多级缓存yml配置文件
 * <p>
 *
 * @author : 21
 * @since : 2023/10/17 14:55
 */

@Data
@ConfigurationProperties(prefix = MultiCacheConfigProperties.MULTI_CACHE_YML_CONFIG_PREFIX)
public class MultiCacheConfigProperties {

    public static final String MULTI_CACHE_YML_CONFIG_PREFIX = "spring.cache.multi";

    /**
     * 是否根据cacheName动态创建Cache 与 SpringCache中dynamic保持一致
     * 解释：根据cacheName获取缓存 缓存不存在时 -> 查询数据库 -> 并设置到该cacheName中 -> 下次调用时存在直接返回
     */
    private boolean dynamic = true;

    /**
     * 锁的租赁时间(单位毫秒)
     */
    private long lockLeaseTime = 1000L;

    /**
     * 配置文件路径
     */
    private String cacheConfig = "classpath:cache-config.json";

    /**
     * 缓存更新时通知其他节点的topic名称
     */
    private String topic = "cache:caffeine:redisson:topic";

    /**
     * 默认缓存配置（防止key存在时 未设置时间）
     * 嵌套配置时，使用该注解
     */
    @NestedConfigurationProperty
    private RedissonCaffeineCacheConfig defaultCacheConfig = new RedissonCaffeineCacheConfig(900000L, 900000L);

}
