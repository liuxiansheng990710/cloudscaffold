package com.example.spring.boot.redisson.exceptions;

/**
 * <p>
 * 锁调用异常
 * <p>
 *
 * @author : 21
 * @since : 2023/10/24 18:28
 */
public class KlockInvocationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public KlockInvocationException() {
    }

    public KlockInvocationException(String message) {
        super(message);
    }

    public KlockInvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
