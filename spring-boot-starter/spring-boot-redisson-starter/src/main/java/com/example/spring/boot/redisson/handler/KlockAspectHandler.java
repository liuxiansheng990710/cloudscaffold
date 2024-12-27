package com.example.spring.boot.redisson.handler;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.example.spring.boot.redisson.annotations.Klock;
import com.example.spring.boot.redisson.enums.LockType;
import com.example.spring.boot.redisson.exceptions.KlockInvocationException;
import com.example.spring.boot.redisson.excutor.lock.LockExcutorFactory;
import com.example.spring.boot.redisson.excutor.provider.LockInfoProvider;
import com.example.spring.boot.redisson.excutor.strategy.LockTimeoutStrategy;
import com.example.spring.boot.redisson.excutor.strategy.ReleaseTimeoutStrategy;
import com.example.spring.boot.redisson.model.LockInfo;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Klock注解切面处理器
 * <p>
 *
 * @author : 21
 * @since : 2023/10/25 17:04
 */

@Aspect
@Component
@Order(0)
@Slf4j
public class KlockAspectHandler {

    private final Map<String, LockInfo> currentThreadLock = new ConcurrentHashMap<>();

    private ThreadLocal<String> currentLockId;

    @Around(value = "@annotation(klock)")
    public Object around(ProceedingJoinPoint joinPoint, Klock klock) throws Throwable {
        LockInfo lockInfo = LockInfoProvider.getLockInfo(joinPoint, klock);
        boolean lock = LockExcutorFactory.acquireLock(lockInfo);
        //获取锁失败 进入失败处理逻辑
        if (!lock) {
            if (log.isWarnEnabled()) {
                log.warn("加锁超时 如果是可重入锁 暂时忽略这条日志：锁key({})", lockInfo.getName());
            }
            if (!klock.customLockTimeoutStrategy().isEmpty()) {
                return LockTimeoutStrategy.handleCustomLockTimeout(klock.customLockTimeoutStrategy(), joinPoint);
            } else {
                //注意：如果没有指定预定义的策略，默认的策略为不做处理 继续执行下面逻辑
                klock.lockTimeoutStrategy().handle(lockInfo, joinPoint);
            }
        }
        lockInfo.setLock(true);
        this.currentLockId = ThreadLocal.withInitial(() -> LockExcutorFactory.getCurrentLockId(lockInfo.getName()));
        currentThreadLock.put(getCurrentLockId(), lockInfo);
        return joinPoint.proceed();
    }

    @AfterReturning(value = "@annotation(klock)")
    public void afterReturning(JoinPoint joinPoint, Klock klock) {
        releaseLock(klock, joinPoint);
        cleanUpThreadLocal();
        currentLockId.remove();
    }

    @AfterThrowing(value = "@annotation(klock)")
    public void afterThrowing(JoinPoint joinPoint, Klock klock) {
        releaseLock(klock, joinPoint);
        cleanUpThreadLocal();
        currentLockId.remove();
    }

    /**
     * 释放锁
     *
     * @param klock
     * @param joinPoint
     */
    private void releaseLock(Klock klock, JoinPoint joinPoint) {
        LockInfo lockInfo = currentThreadLock.get(getCurrentLockId());
        if (Objects.isNull(lockInfo)) {
            throw new KlockInvocationException("请检查是否修改了输入参数 导致锁键不一致：锁键({})" + currentLockId);
        }
        if (lockInfo.isLock()) {
            boolean releaseLock = LockExcutorFactory.releaseLock(lockInfo);
            if (!releaseLock) {
                handleReleaseTimeout(klock, lockInfo, joinPoint);
            }
        }
    }

    /**
     * 处理释放锁时超时
     *
     * @param klock
     * @param lockInfo
     * @param joinPoint
     */
    private void handleReleaseTimeout(Klock klock, LockInfo lockInfo, JoinPoint joinPoint) {
        if (log.isWarnEnabled()) {
            log.warn("Timeout while release Lock({})", lockInfo.getName());
        }
        if (!klock.customReleaseTimeoutStrategy().isEmpty()) {
            ReleaseTimeoutStrategy.handleCustomReleaseTimeout(klock.customReleaseTimeoutStrategy(), joinPoint);
        } else {
            //注意：如果没有指定预定义的策略，默认的策略为不做处理 继续执行下面逻辑
            klock.releaseTimeoutStrategy().handle(lockInfo);
        }
    }

    private void cleanUpThreadLocal() {
        LockType lockType = currentThreadLock.get(getCurrentLockId()).getLockType();
        if (Objects.equals(lockType, LockType.REENTRANT)) {
            if (LockExcutorFactory.getCounter(getCurrentLockId()).get() == 0) {
                currentThreadLock.remove(getCurrentLockId());
                LockExcutorFactory.removeCounter(getCurrentLockId());
            }
        } else {
            currentThreadLock.remove(getCurrentLockId());
        }
    }

    private String getCurrentLockId() {
        return currentLockId.get();
    }

}
