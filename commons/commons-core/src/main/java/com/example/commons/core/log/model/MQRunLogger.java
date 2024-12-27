package com.example.commons.core.log.model;

import org.springframework.messaging.MessageHeaders;

import com.alibaba.fastjson.JSONObject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * mq运行时日志（包括生产/消费者信息）
 * <p>
 *
 * @author : 21
 * @since : 2023/12/9 16:48
 */

@Setter
@Getter
@Accessors(chain = true)
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MQRunLogger extends SuperLogger {

    public static final String PRODUCER_LOG_PREFIX = "<mq_procuder> - ";
    public static final String CONSUMER_LOG_PREFIX = "<mq_consumer> - ";

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 发送耗时
     */
    private String sendRunTime;

    /**
     * 是否发送成功
     */
    private Boolean sent;

    /**
     * 消息Id
     */
    private String msgId;

    /**
     * 消息体
     */
    private JSONObject payload;

    /**
     * 消息头
     */
    private MessageHeaders headers;

    /**
     * 异常信息
     */
    private String exception;

    /**
     * 异常信息
     */
    private String requestId;

}
