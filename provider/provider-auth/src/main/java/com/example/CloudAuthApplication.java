package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <p>
 * Auth服务启动类
 * <p>
 *
 * @author : 21
 * @since : 2023/9/11 14:54
 */

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableCaching
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class CloudAuthApplication {


    public static void main(String[] args) {
        SpringApplication.run(CloudAuthApplication.class);
    }

}
