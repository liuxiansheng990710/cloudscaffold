package com.example.spring.boot.cache.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.redisson.spring.cache.CacheConfig;

import com.example.spring.boot.cache.support.RedissonCaffeineCacheConfigSupport;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * 多级缓存配置类 （基于Redisson）
 * <p>
 *
 * @author : 21
 * @since : 2023/10/16 10:28
 */

@Getter
@Setter
@NoArgsConstructor
public class RedissonCaffeineCacheConfig extends CacheConfig {

    /**
     * 是否支持模糊清理，当缓存KEY为String并且KEY中包含'*'，以及该字段设置为true时模糊清理才会生效
     */
    private boolean vagueEvict = false;

    /**
     * 是否允许缓存null值（作用于缓存穿透）
     */
    private boolean allowNullValues = true;

    /**
     * 是否开启多级缓存
     */
    private boolean multiLevelCache = true;

    /**
     * 是否开启克隆（获取到克隆后的缓存对象 不直接操作原缓存对象）
     * 只针对一级缓存
     */
    private boolean clone = false;

    /**
     * 一级缓存初始化大小
     */
    private int initialCapacity = 20;

    /**
     * 最大缓存对象个数，超过此数量时之前放入的缓存将失效
     */
    private long maximumSize = 1000;

    /**
     * ttl（毫秒）：缓存条目的存活时间，即缓存条目在被创建后保持有效的时间。超过存活时间后，缓存条目将被认为是过期的，并且可能会被从缓存中移除
     * <p>
     * maxIdleTime（毫秒）：表示缓存条目的最大空闲时间，即缓存条目在被访问后保持有效的时间。如果缓存条目在指定的空闲时间内没有被访问过，它可能会被认为是过期的，并且可能会被从缓存中移除。
     * <p>
     * maxSize：表示缓存的最大容量大小，即缓存可以容纳的最大条目数量。当缓存中的条目数量达到最大容量时，新的条目可能会导致旧的条目被从缓存中移除，以便为新条目腾出空间。
     */
    public RedissonCaffeineCacheConfig(long ttl, long maxIdleTime) {
        super(ttl, maxIdleTime);
    }

    /**
     * 因暂用该方法 所以只提取该方法，若需要可自行提取
     * {@link org.redisson.spring.cache.CacheConfig}
     */
    public static Map<String, RedissonCaffeineCacheConfig> fromJSON(InputStream inputStream) throws IOException {
        return new RedissonCaffeineCacheConfigSupport().fromJSON(inputStream);
    }

}
