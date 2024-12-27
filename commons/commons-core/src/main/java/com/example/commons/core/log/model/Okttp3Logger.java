package com.example.commons.core.log.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * okhttp请求日志详情
 * <p>
 *
 * @author : 21
 * @since : 2023/12/5 17:51
 */

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Okttp3Logger extends SuperLogger {

    public static final String LOG_PREFIX = "<ok3Log> - ";

    /**
     * requestBody
     */
    private String okSource;
    /**
     * requestBody
     */
    private String okBody;
    /**
     * 请求路径
     */
    private String okUrl;
    /**
     * 请求方法
     */
    private String okMethod;
    /**
     * 日志需要打印的json字符串
     */
    private String okResult;
    /**
     * 头信息
     */
    private String okHeader;
    /**
     * http 状态码
     */
    private Integer okStatus;
    /**
     * 异常信息
     */
    private String okException;
    /**
     * 运行时间 单位:ms
     */
    private String runTime;
}