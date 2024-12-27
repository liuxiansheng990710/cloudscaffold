package com.example.exception;

/**
 * <p>
 *  数据库检测异常
 * <p>
 *
 * @author : 21
 * @since : 2024/3/12 17:14
 */

public class CheckDataBaseException extends RuntimeException {

    public CheckDataBaseException(String msg) {
        super(msg);
    }

}
