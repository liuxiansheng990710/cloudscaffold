package com.example.spring.boot.redisson.properties;

import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 分布式锁默认值配置
 * <p>
 *
 * @author : 21
 * @since : 2023/10/25 10:59
 */

@Data
@NoArgsConstructor
@Configuration
public class KlockConfig {

    /**
     * 尝试加锁，最多等待时间
     */
    private long waitTime = 0;
    /**
     * 上锁以后xxx秒自动解锁
     */
    private long leaseTime = 5;

}
