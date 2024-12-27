package com.example.spring.boot.redisson.excutor.lock;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.spring.boot.redisson.model.LockInfo;
import com.example.spring.boot.redisson.enums.LockType;

/**
 * <p>
 * 读锁执行器
 * <p>
 *
 * @author : 21
 * @since : 2023/10/24 17:22
 */

@Component
public class ReadLockExcutor implements LockExcutor {

    @Autowired
    private RedissonClient redissonClient;

    private RLock readLock;

    @Override
    public boolean acquireLock(LockInfo lockInfo) {
        readLock = redissonClient.getReadWriteLock(lockInfo.getName()).readLock();
        try {
            return readLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean releaseLock(LockInfo lockInfo) {
        if (readLock.isHeldByCurrentThread()) {
            try {
                return readLock.forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    @Override
    public LockType lockType() {
        return LockType.READ;
    }
}
