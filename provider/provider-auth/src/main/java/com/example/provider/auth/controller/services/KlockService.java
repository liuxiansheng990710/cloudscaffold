package com.example.provider.auth.controller.services;

import org.springframework.stereotype.Service;

import com.example.spring.boot.redisson.annotations.Klock;
import com.example.spring.boot.redisson.annotations.KlockKey;
import com.example.spring.boot.redisson.enums.LockType;
import com.example.spring.boot.redisson.excutor.strategy.LockTimeoutStrategy;

@Service
public class KlockService {

    @Klock(name = "21", lockType = LockType.WRITE, waitTime = 5, leaseTime = 30, keys = {"#param"}, lockTimeoutStrategy = LockTimeoutStrategy.KEEP_ACQUIRE)
    public String getFairLockDoNothing(String param, @KlockKey(value = "6") Long id) throws InterruptedException {
        Thread.sleep(2 * 1000);
        return "success";
    }

    @Klock(name = "21", lockType = LockType.REENTRANT, waitTime = 5, leaseTime = 30, keys = {"#param"}, lockTimeoutStrategy = LockTimeoutStrategy.FAIL_FAST)
    public String getFairLockSleep(String param) throws InterruptedException {
        Thread.sleep(5 * 1000);
//        ((KlockService) AopContext.currentProxy()).initMethod(param);
        return "success";
    }

    @Klock(name = "21", lockType = LockType.REENTRANT, waitTime = 5, leaseTime = 30, keys = {"#param"}, lockTimeoutStrategy = LockTimeoutStrategy.FAIL_FAST)
    public String initMethod(String param) throws InterruptedException {
        Thread.sleep(5 * 1000);
        return "success";
    }

}
