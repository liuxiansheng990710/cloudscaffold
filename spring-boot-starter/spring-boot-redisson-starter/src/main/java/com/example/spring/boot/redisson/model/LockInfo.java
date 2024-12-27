package com.example.spring.boot.redisson.model;

import com.example.spring.boot.redisson.enums.LockType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LockInfo {

    /**
     * 锁类型
     */
    private LockType lockType;
    /**
     * 锁的名称
     */
    private String name;
    /**
     * 尝试加锁，最多等待时间
     */
    private long waitTime;
    /**
     * 上锁以后xxx秒自动解锁
     */
    private long leaseTime;
    /**
     * 重试获取锁最大间隔（毫秒）
     */
    private long retryMaxTime;
    /**
     * 自定义错误信息
     */
    private String error;
    /**
     * 是否加锁成功
     */
    private boolean lock;

}
