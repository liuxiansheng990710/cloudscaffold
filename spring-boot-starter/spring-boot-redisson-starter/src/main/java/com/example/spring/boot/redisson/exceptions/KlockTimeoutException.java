package com.example.spring.boot.redisson.exceptions;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 加锁失败异常
 * <p>
 *
 * @author : 21
 * @since : 2023/10/25 17:20
 */
public class KlockTimeoutException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    private String errorMsg;

    public KlockTimeoutException() {
    }

    public KlockTimeoutException(String message, String errorMsg) {
        super(message);
        setErrorMsg(errorMsg);
    }

    public KlockTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

}
