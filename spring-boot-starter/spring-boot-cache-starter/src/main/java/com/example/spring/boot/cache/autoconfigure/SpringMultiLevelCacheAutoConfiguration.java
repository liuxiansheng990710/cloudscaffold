package com.example.spring.boot.cache.autoconfigure;

import java.util.Map;

import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.example.spring.boot.cache.config.RedissonCaffeineCacheConfig;
import com.example.spring.boot.cache.genertor.ToStringKeyGenerator;
import com.example.spring.boot.cache.properties.MultiCacheConfigProperties;
import com.example.spring.boot.cache.support.RedissonCaffeineCacheListener;
import com.example.spring.boot.cache.support.RedissonCaffeineCacheManager;
import com.example.spring.boot.redisson.autoconfigure.RedissonAutoConfiguration;
import com.google.common.base.Throwables;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({RedissonAutoConfiguration.class})
@EnableConfigurationProperties(MultiCacheConfigProperties.class)
public class SpringMultiLevelCacheAutoConfiguration extends CachingConfigurerSupport implements ResourceLoaderAware, ApplicationContextAware {

    private ApplicationContext applicationContext;

    private ResourceLoader resourceLoader;

    private final MultiCacheConfigProperties cacheConfigProperties;

    private final RedissonClient redissonClient;

    public SpringMultiLevelCacheAutoConfiguration(MultiCacheConfigProperties cacheConfigProperties,
                                                  RedissonClient redissonClient) {
        this.cacheConfigProperties = cacheConfigProperties;
        this.redissonClient = redissonClient;
    }

    @Bean
    @Primary
    @SneakyThrows
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnProperty(prefix = MultiCacheConfigProperties.MULTI_CACHE_YML_CONFIG_PREFIX + ".defaultCacheConfig", name = "multiLevelCache")
    public RedissonCaffeineCacheManager caffeineRedissonCacheManager() {
        Resource resource = resourceLoader.getResource(cacheConfigProperties.getCacheConfig());
        Map<String, RedissonCaffeineCacheConfig> cacheConfigMap = RedissonCaffeineCacheConfig.fromJSON(resource.getInputStream());
        return new RedissonCaffeineCacheManager(cacheConfigProperties, redissonClient, cacheConfigMap);
    }

    @Bean
    public ToStringKeyGenerator toStringKeyGenerator() {
        return new ToStringKeyGenerator();
    }

    @Bean
    @ConditionalOnBean(RedissonCaffeineCacheManager.class)
    @ConditionalOnProperty(prefix = MultiCacheConfigProperties.MULTI_CACHE_YML_CONFIG_PREFIX + ".defaultCacheConfig", name = "multiLevelCache")
    public RedissonCaffeineCacheListener cachelistener(RedissonCaffeineCacheManager cacheManager) {
        return new RedissonCaffeineCacheListener(cacheManager, redissonClient, cacheConfigProperties.getTopic(), cacheConfigProperties.getLockLeaseTime());
    }

    @Override
    public KeyGenerator keyGenerator() {
        return applicationContext.getBean(ToStringKeyGenerator.class);
    }

    @Override
    public CacheManager cacheManager() {
        return applicationContext.getBean(RedissonCaffeineCacheManager.class);
    }

    /**
     * redis数据操作异常处理 这里的处理：在日志中打印出错误信息，但是放行
     * 保证redis服务器出现连接等问题的时候不影响程序的正常运行，使得能够出问题时不用缓存
     *
     * @return
     */
    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache,
                                            Object key, Object value) {
                cacheError("handleCachePutError", exception, cache.getName(), key);
            }

            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache,
                                            Object key) {
                cacheError("handleCacheGetError", exception, cache.getName(), key);
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache,
                                              Object key) {
                cacheError("handleCacheEvictError", exception, cache.getName(), key);
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                cacheError("handleCacheClearError", exception, cache.getName(), null);
            }
        };
    }

    protected void cacheError(String method, Exception exception, String cachename, Object key) {
        log.error("caffeine redisson cache exception, the method is [{}], cache name is [{}], key is [{}], exception is [{}]", method, cachename, key, Throwables.getStackTraceAsString(exception));
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}
