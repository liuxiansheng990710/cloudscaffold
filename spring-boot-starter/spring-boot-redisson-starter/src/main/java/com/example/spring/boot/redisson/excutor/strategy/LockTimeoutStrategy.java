package com.example.spring.boot.redisson.excutor.strategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;

import com.example.spring.boot.redisson.exceptions.KlockInvocationException;
import com.example.spring.boot.redisson.exceptions.KlockTimeoutException;
import com.example.spring.boot.redisson.excutor.lock.LockExcutorFactory;
import com.example.spring.boot.redisson.handler.LockTimeoutHandler;
import com.example.spring.boot.redisson.model.LockInfo;

/**
 * <p>
 * 加锁超时处理策略
 * <p>
 *
 * @author : 21
 * @since : 2023/10/25 17:14
 */
public enum LockTimeoutStrategy implements LockTimeoutHandler {

    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo, JoinPoint joinPoint) {
            // do nothing
        }
    },

    /**
     * 快速失败
     */
    FAIL_FAST() {
        @Override
        public void handle(LockInfo lockInfo, JoinPoint joinPoint) {
            String errorMsg = String.format("Failed to acquire Lock(%s) with timeout(%ds)", lockInfo.getName(), lockInfo.getWaitTime());
            throw new KlockTimeoutException(errorMsg, lockInfo.getError());
        }
    },

    /**
     * 一直阻塞，直到获得锁，最多尝试六次后，仍会报错
     */
    KEEP_ACQUIRE() {
        //初始获取锁间隔(100毫秒)
        private static final long DEFAULT_INTERVAL = 100L;
        //最大获取锁间隔(由于网关是10s超时 所以这里设置最大尝试间隔为6.2秒)
        //指数退避共计执行六次 前六次累计6.3s 第七次等待6.4s > 6.2s（如果第七次再等待，网关超时了，也没有意义），也就是说，在网关超时之前，会一直尝试获取锁
        private static final long DEFAULT_MAX_INTERVAL = 6200L;

        @Override
        public void handle(LockInfo lockInfo, JoinPoint joinPoint) {
            long interval = DEFAULT_INTERVAL;
            long maxInterval = lockInfo.getRetryMaxTime() <= 0 ? DEFAULT_MAX_INTERVAL : lockInfo.getRetryMaxTime();
            //第一次 等待100ms 再去获取锁 -> 200ms -> 800ms -> ...
            while (!LockExcutorFactory.acquireLock(lockInfo)) {
                if (interval > maxInterval) {
                    String errorMsg = String.format("多次获取锁（%s）失败，这可能是因为发生了死锁。",
                            lockInfo.getName());
                    throw new KlockTimeoutException(errorMsg, lockInfo.getError());
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(interval);
                    //相当于 interval = interval * 2
                    interval <<= 1;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new KlockTimeoutException("Failed to acquire Lock", e);
                }
            }
        }
    };

    /**
     * 处理自定义加锁超时
     *
     * @param lockTimeoutHandler
     * @param joinPoint
     * @return
     */
    public static Object handleCustomLockTimeout(String lockTimeoutHandler, JoinPoint joinPoint) {
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Method handleMethod;
        try {
            //获取切面所在类，然后执行自定义超时逻辑方法
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(lockTimeoutHandler, currentMethod.getParameterTypes());
            ReflectionUtils.makeAccessible(handleMethod);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customLockTimeoutStrategy", e);
        }
        Object res;
        try {
            res = handleMethod.invoke(joinPoint.getTarget(), joinPoint.getArgs());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new KlockInvocationException("Fail to invoke custom lock timeout handler: " + lockTimeoutHandler, e);
        }
        return res;
    }

}
