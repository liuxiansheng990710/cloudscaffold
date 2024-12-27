package com.example.cloud.openfeign.decoder;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ApiResponses<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * http 状态码
     */
    private Integer status;
    /**
     * 错误状态码
     */
    private String error;
    /**
     * 异常信息
     */
    private String exception;
    /**
     * 错误信息
     */
    private String msg;
    /**
     * 错误等级
     */
    private String ranking;
    /**
     * 当前时间戳
     */
    private String time;
    /**
     * 客户端是否展示
     */
    private Boolean show;
    /**
     * 结果集返回
     */
    private T result;
    /**
     * 请求ID
     */
    private String requestId;

}
