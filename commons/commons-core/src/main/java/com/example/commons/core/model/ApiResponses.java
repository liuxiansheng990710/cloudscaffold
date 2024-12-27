package com.example.commons.core.model;

import java.io.Serializable;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Getter
@Accessors(chain = true)
public class ApiResponses<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "http 状态码")
    private Integer status;

    @Schema(description = "自定义错误状态码")
    private String error;

    @Schema(description = "异常信息")
    private String exception;

    @Schema(description = "错误信息")
    private String msg;

    @Schema(description = "错误等级")
    private String ranking;

    @Schema(description = "当前时间戳")
    private String time;

    @Schema(description = "客户端是否展示")
    private Boolean show;

    @Schema(description = "结果集返回")
    private T result;

    @Schema(description = "请求ID")
    private String requestId;

}
