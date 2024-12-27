package com.example.spring.boot.redisson.autoconfigure;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ConfigSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import com.example.spring.boot.redisson.properties.RedissonProperties;

import lombok.NonNull;

@Configuration
@ConditionalOnClass(RedissonClient.class)
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonAutoConfiguration implements ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    /**
     * Spring 容器关闭时销毁该 Bean 时要调用
     * 配置文件中存在以 "redisson.enabled" 为键的属性，并且值为 true时创建
     */
    @Bean(destroyMethod = "shutdown")
    @ConditionalOnProperty(prefix = RedissonProperties.REDISSON_YML_CONFIG_PREFIX, name = "enable")
    @ConditionalOnClass(RedissonClient.class)
    public RedissonClient redissonClient(RedissonProperties properties) throws IOException {
        Resource resource = resourceLoader.getResource(properties.getConfig());
        ConfigSupport support = new ConfigSupport();
        return Redisson.create(support.fromJSON(resource.getInputStream(), Config.class));
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        //获取本地文件使用
        this.resourceLoader = resourceLoader;
    }
}
