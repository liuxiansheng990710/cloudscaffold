package com.example.spring.boot.redisson.excutor.lock;

import com.example.spring.boot.redisson.enums.LockType;
import com.example.spring.boot.redisson.model.LockInfo;

/**
 * <p>
 * 锁执行器
 * <p>
 *
 * @author : 21
 * @since : 2023/10/24 16:48
 */

public interface LockExcutor {

    /**
     * 获取锁
     *
     * @return
     */
    boolean acquireLock(LockInfo lockInfo);

    /**
     * 释放锁
     *
     * @return
     */
    boolean releaseLock(LockInfo lockInfo);

    /**
     * 锁类型
     *
     * @return
     */
    LockType lockType();

}
