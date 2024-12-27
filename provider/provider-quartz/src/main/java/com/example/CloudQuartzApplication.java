package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <p>
 * 定时任务服务启动类
 * <p>
 *
 * @author : 21
 * @since : 2023/9/22 10:08
 */

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class CloudQuartzApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudQuartzApplication.class);
    }

}
