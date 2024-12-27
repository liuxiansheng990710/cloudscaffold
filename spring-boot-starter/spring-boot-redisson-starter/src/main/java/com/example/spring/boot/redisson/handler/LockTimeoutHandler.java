package com.example.spring.boot.redisson.handler;

import org.aspectj.lang.JoinPoint;

import com.example.spring.boot.redisson.model.LockInfo;

/**
 * <p>
 * 加锁超时处理
 * <p>
 *
 * @author : 21
 * @since : 2023/10/25 17:10
 */
public interface LockTimeoutHandler {

    void handle(LockInfo lockInfo, JoinPoint joinPoint);

}
