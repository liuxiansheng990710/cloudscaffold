package com.example.cloud.sentinel.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.alibaba.cloud.sentinel.SentinelProperties;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.SentinelWebInterceptor;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.config.SentinelWebMvcConfig;
import com.example.cloud.sentinel.interceptor.SentinelAuthInterceptor;

/**
 * <p>
 *  注册SentinelAuthInterceptor，为让其继承父类属性
 * {@link com.alibaba.cloud.sentinel.SentinelWebAutoConfiguration#sentinelWebInterceptor}
 * <p>
 *
 * @author : 21
 * @since : 2023/8/18 18:45
 */

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = "spring.cloud.sentinel.enabled", matchIfMissing = true)
@ConditionalOnClass({SentinelWebInterceptor.class, SentinelAuthInterceptor.class})
@EnableConfigurationProperties(SentinelProperties.class)
public class SentinelAuthConfiguration {

    @Bean
    @Primary
    @ConditionalOnProperty(name = "spring.cloud.sentinel.filter.enabled",
            matchIfMissing = true)
    public SentinelAuthInterceptor sentinelAuthInterceptor(
            SentinelWebMvcConfig sentinelWebMvcConfig) {
        return new SentinelAuthInterceptor(sentinelWebMvcConfig);
    }

}
