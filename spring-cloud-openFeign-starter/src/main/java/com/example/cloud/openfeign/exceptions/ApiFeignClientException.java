package com.example.cloud.openfeign.exceptions;

/**
 * <p>
 * Feign异常类
 * <p>
 *
 * @author : 21
 * @since : 2023/7/19 10:41
 */

public class ApiFeignClientException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int status;

    private final String errerBodyMsg;

    public ApiFeignClientException(String errerBodyMsg) {
        super(errerBodyMsg);
        this.errerBodyMsg = errerBodyMsg;
    }

    public ApiFeignClientException(int status, String errerBodyMsg) {
        this(errerBodyMsg);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public String getErrerBodyMsg() {
        return errerBodyMsg;
    }

}
