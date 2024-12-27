package com.example.spring.boot.redisson.excutor.lock;

import java.util.Objects;
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
 * 可重入锁执行器
 * <p>
 *
 * @author : 21
 * @since : 2023/10/24 17:21
 */

@Component
public class ReentrantLockExcutor implements LockExcutor {

    @Autowired
    private RedissonClient redissonClient;

    private RLock reentrantLock;

    @Override
    public boolean acquireLock(LockInfo lockInfo) {
        try {
            String lockId = LockExcutorFactory.getCurrentLockId(lockInfo.getName());
            if (Objects.isNull(LockExcutorFactory.getCounter(lockId))) {
                LockExcutorFactory.initCounter(lockId);
            }
            reentrantLock = redissonClient.getLock(lockInfo.getName());
            boolean isLock = reentrantLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
            if (isLock) {
                LockExcutorFactory.getCounter(lockId).getAndIncrement();
            }
            return isLock;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean releaseLock(LockInfo lockInfo) {
        if (reentrantLock.isHeldByCurrentThread()) {
            try {
                String lockId = LockExcutorFactory.getCurrentLockId(lockInfo.getName());
                if (LockExcutorFactory.getCounter(lockId).decrementAndGet() == 0) {
                    return reentrantLock.forceUnlockAsync().get();
                }
                return true;
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    @Override
    public LockType lockType() {
        return LockType.REENTRANT;
    }
}
