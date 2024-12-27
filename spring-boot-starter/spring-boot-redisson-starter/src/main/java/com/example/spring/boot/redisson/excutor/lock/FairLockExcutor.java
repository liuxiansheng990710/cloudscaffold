package com.example.spring.boot.redisson.excutor.lock;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.spring.boot.redisson.enums.LockType;
import com.example.spring.boot.redisson.model.LockInfo;

/**
 * <p>
 * 公平锁执行器
 * <p>
 *
 * @author : 21
 * @since : 2023/10/24 16:49
 */

@Component
public class FairLockExcutor implements LockExcutor {

    @Autowired
    private RedissonClient redissonClient;

    private RLock fairLock;

    @Override
    public boolean acquireLock(LockInfo lockInfo) {
        fairLock = redissonClient.getFairLock(lockInfo.getName());
        try {
            return fairLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            //获取锁阻塞时 如果被其他线程中断后（抛出InterruptedException异常）
            //为了防止循环调用时，无法得知该线程已被中断，重新设置中断状态，可以使得正确结束
            Thread.currentThread().interrupt();
            return false;
        }
    }

    @Override
    public boolean releaseLock(LockInfo lockInfo) {
        //是否是当前线程持有锁
        if (fairLock.isHeldByCurrentThread()) {
            try {
                //fairLock.unlockAsync();安全释放当前线程持有的锁 fairLock.forceUnlockAsync();强制释放锁（不论是否是当前线程所持有）
                //由于上面判定是当前线程，所以可以强制释放锁，避免因其他问题无法释放锁，导致死锁问题
                return fairLock.forceUnlockAsync().get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    @Override
    public LockType lockType() {
        return LockType.FAIR;
    }

}
