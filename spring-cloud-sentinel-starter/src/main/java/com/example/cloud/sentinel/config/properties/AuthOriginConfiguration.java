package com.example.cloud.sentinel.config.properties;

import java.util.Collections;
import java.util.List;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = AuthOriginConfiguration.SENTINEL_AUTH_ORIGIN_CONFIG)
public class AuthOriginConfiguration {

    public static final String SENTINEL_AUTH_ORIGIN_CONFIG = "sentinel.authority";

    //鉴权字段
    private String origin = "a";

    //鉴权类型
    private Integer strategy = 111;

    //鉴权参数
    private List<String> limitApp = Collections.emptyList();

}
