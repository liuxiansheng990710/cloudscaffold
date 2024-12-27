package com.example.commons.core.log.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * mq日志消息体
 * <p>
 *
 * @author : 21
 * @since : 2023/12/7 9:46
 */

@Setter
@Getter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MQErrorLogger extends SuperLogger {

    public static final String LOG_PREFIX = "<mqErrorLog> - ";

    /**
     * 应用名称
     */
    private String queueName;
    /**
     * 错误消息
     */
    private String exception;
    /**
     * 当前时间戳
     */
    private String time;
    /**
     * 消息体
     */
    private String mqBody;
}
