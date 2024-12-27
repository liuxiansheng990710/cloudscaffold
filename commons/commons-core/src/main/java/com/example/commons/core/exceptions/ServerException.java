package com.example.commons.core.exceptions;

/**
 * <p>
 * 服务运行异常
 * <p>
 *
 * @author : 21
 * @since : 2023/9/22 9:51
 */

public class ServerException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ServerException(String message) {
        super(message);
    }

    public ServerException(Throwable throwable) {
        super(throwable);
    }

    public ServerException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
