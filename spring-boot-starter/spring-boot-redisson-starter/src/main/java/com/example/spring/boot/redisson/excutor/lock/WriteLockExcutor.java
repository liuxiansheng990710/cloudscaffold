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
 * 写锁执行器
 * <p>
 *
 * @author : 21
 * @since : 2023/10/24 17:30
 */

@Component
public class WriteLockExcutor implements LockExcutor {

    @Autowired
    private RedissonClient redissonClient;

    private RLock writeLock;

    @Override
    public boolean acquireLock(LockInfo lockInfo) {
        writeLock = redissonClient.getReadWriteLock(lockInfo.getName()).writeLock();
        try {
            return writeLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean releaseLock(LockInfo lockInfo) {
        if (writeLock.isHeldByCurrentThread()) {
            try {
                return writeLock.forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    @Override
    public LockType lockType() {
        return LockType.WRITE;
    }
}
