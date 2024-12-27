package com.example.commons.core.exceptions;

import com.example.commons.core.model.Errors;

/**
 * <p>
 * 断言异常
 * <p>
 *
 * @author : 21
 * @since : 2023/12/18 16:37
 */

public class ApiException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误码
     */
    private final Errors errors;
    /**
     * 错误
     */
    private Exception exception;

    public ApiException(Errors errors) {
        super(errors.getMsg());
        this.errors = errors;
    }

    public ApiException(Errors errors, Exception exception) {
        this(errors);
        this.exception = exception;
    }

    public Errors getErrors() {
        return errors;
    }

    public Exception getException() {
        return exception;
    }

}
