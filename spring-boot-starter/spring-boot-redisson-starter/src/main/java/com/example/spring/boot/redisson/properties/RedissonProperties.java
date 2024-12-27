package com.example.spring.boot.redisson.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = RedissonProperties.REDISSON_YML_CONFIG_PREFIX)
public class RedissonProperties {

    public static final String REDISSON_YML_CONFIG_PREFIX = "redisson";

    private boolean enable;

    private String config;

    @NestedConfigurationProperty
    private KlockConfig klock = new KlockConfig();
}
