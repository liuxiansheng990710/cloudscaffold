package com.example.spring.boot.redisson.excutor.provider;

import org.aspectj.lang.JoinPoint;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.spring.boot.redisson.annotations.Klock;
import com.example.spring.boot.redisson.model.LockInfo;
import com.example.spring.boot.redisson.properties.KlockConfig;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 锁信息提供者
 * <p>
 *
 * @author : 21
 * @since : 2023/10/25 11:21
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LockInfoProvider {

    @Autowired
    private static KlockConfig klockConfig;

    public static LockInfo getLockInfo(JoinPoint joinPoint, Klock klock) {
        String keyName = BusinessKeyProvider.getKeyName(joinPoint, klock);
        long waitTime = getWaitTime(klock);
        long leaseTime = getLeaseTime(klock);
        //如果占用锁的时间设计不合理，则打印相应的警告提示
        if (leaseTime == -1 && log.isWarnEnabled()) {
            log.warn("Trying to acquire Lock({}) with no expiration, " +
                    "Klock will keep prolong the lock expiration while the lock is still holding by current thread. " +
                    "This may cause dead lock in some circumstances.", keyName);
        }
        return new LockInfo(klock.lockType(), keyName, waitTime, leaseTime, klock.retryMaxTime(), klock.error(), false);
    }

    private static long getWaitTime(Klock lock) {
        return lock.waitTime() == Long.MIN_VALUE ?
                klockConfig.getWaitTime() : lock.waitTime();
    }

    private static long getLeaseTime(Klock lock) {
        return lock.leaseTime() == Long.MIN_VALUE ?
                klockConfig.getLeaseTime() : lock.leaseTime();
    }

}
