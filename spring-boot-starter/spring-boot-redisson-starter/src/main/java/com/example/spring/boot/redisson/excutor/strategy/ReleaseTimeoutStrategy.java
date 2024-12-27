package com.example.spring.boot.redisson.excutor.strategy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.util.ReflectionUtils;

import com.example.spring.boot.redisson.exceptions.KlockInvocationException;
import com.example.spring.boot.redisson.exceptions.KlockTimeoutException;
import com.example.spring.boot.redisson.handler.ReleaseTimeoutHandler;
import com.example.spring.boot.redisson.model.LockInfo;

/**
 * <p>
 * 解锁超时处理策略
 * <p>
 *
 * @author : 21
 * @since : 2023/10/26 9:22
 */

public enum ReleaseTimeoutStrategy implements ReleaseTimeoutHandler {

    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(LockInfo lockInfo) {
            // do nothing
        }
    },
    /**
     * 快速失败
     */
    FAIL_FAST() {
        @Override
        public void handle(LockInfo lockInfo) {
            String errorMsg = String.format("Found Lock(%s) already been released while lock lease time is %d s", lockInfo.getName(), lockInfo.getLeaseTime());
            throw new KlockTimeoutException(errorMsg, lockInfo.getError());
        }
    };

    /**
     * 处理自定义释放锁时超时
     *
     * @param releaseTimeoutHandler
     * @param joinPoint
     */
    public static void handleCustomReleaseTimeout(String releaseTimeoutHandler, JoinPoint joinPoint) {
        Method currentMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Method handleMethod;
        try {
            handleMethod = joinPoint.getTarget().getClass().getDeclaredMethod(releaseTimeoutHandler, currentMethod.getParameterTypes());
            ReflectionUtils.makeAccessible(handleMethod);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Illegal annotation param customReleaseTimeoutStrategy", e);
        }
        try {
            handleMethod.invoke(joinPoint.getTarget(), joinPoint.getArgs());
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new KlockInvocationException("Fail to invoke custom release timeout handler: " + releaseTimeoutHandler, e);
        }
    }

}
