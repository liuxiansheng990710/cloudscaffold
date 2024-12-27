package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <p>
 * Base服务启动类
 * <p>
 *
 * @author : 21
 * @since : 2023/9/11 14:55
 */

@SpringBootApplication
@EnableFeignClients
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
public class CloudBaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudBaseApplication.class, args);
    }

}
