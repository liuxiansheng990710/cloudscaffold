package com.example.commons.core.mq;

import java.util.HashMap;

import org.springframework.cloud.stream.messaging.DirectWithAttributesChannel;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.GlobalChannelInterceptor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ChannelInterceptor;

/**
 * <p>
 * mq消费者切面（暂只打印info日志）
 * <p>
 *
 * @author : 21
 * @since : 2023/12/7 17:00
 */

@Configuration
@GlobalChannelInterceptor(patterns = "*")
@SuppressWarnings("all")
public class MQChannelInterceptor implements ChannelInterceptor {

    private long startTime;

    private static HashMap<String, MQLogHandler> LOG_HANDLER_MAP = new HashMap();

    /**
     * 只打印符合标准的队列
     */
    static {
        LOG_HANDLER_MAP.put(MQLogHandler.PRODUCER_DECISION_RULES + true, ProducerLogHandler.SENT_SUCCESFUL);
        LOG_HANDLER_MAP.put(MQLogHandler.PRODUCER_DECISION_RULES + false, ProducerLogHandler.SENT_FAILED);
        LOG_HANDLER_MAP.put(MQLogHandler.CONSUMER_DECISION_RULES + true, ConsumerLogHandler.SENT_SUCCESFUL);
        LOG_HANDLER_MAP.put(MQLogHandler.CONSUMER_DECISION_RULES + false, ConsumerLogHandler.SENT_FAILED);
    }

    /**
     * 消息发送到通道之前被调用 允许修改消息
     * 如果返回null,则不会发生调用
     *
     * @param message 将要发送的消息
     * @param channel 发送消息的通道
     * @return
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        this.startTime = System.currentTimeMillis();
        return ChannelInterceptor.super.preSend(message, channel);
    }

    /**
     * 消息发送到通道后被调用（不论成功失败都会被调用）
     * 生产者：发送之后才会调用该方法
     * 消费者：消费完成之后调用该方法
     *
     * @param message 已经发送的消息
     * @param channel 发送消息的通道
     * @param sent    消息是否成功发送
     */
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
        ChannelInterceptor.super.postSend(message, channel, sent);
    }

    /**
     * 消息发送后的处理（不论成功失败都会被调用）
     * 生产者：发送之后才会调用该方法
     * 消费者：消费完成之后调用该方法
     *
     * @param message 已经发送的消息
     * @param channel 发送消息的通道
     * @param sent    消息是否成功发送
     * @param ex      如果有异常，将会是异常的引用
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        if (channel instanceof DirectWithAttributesChannel) {
            printMQRunLog(message, channel, sent, ex, startTime);
        }
        ChannelInterceptor.super.afterSendCompletion(message, channel, sent, ex);
    }

    /**
     * 打印mq运行日志
     *
     * @param message
     * @param channel
     * @param sent
     * @param ex
     */
    private void printMQRunLog(Message<?> message, MessageChannel channel, boolean sent, Exception ex, long startTime) {
        long endTime = System.currentTimeMillis();
        String queueName = ((DirectWithAttributesChannel) channel).getBeanName();
        String queueRules = MQLogHandler.extractQueueRules(queueName);
        LOG_HANDLER_MAP.get(queueRules + sent).handler(message, queueName, ex, startTime, endTime);
    }

    //----------------------------------------------- 以下方法为 主动从通道接受消息时才会被调用 -----------------------------------------------------------------------//

    /**
     * 在消息从通道接收之前被调用
     *
     * @param channel 接收消息的通道
     * @return true 表示允许接收消息，false 表示拒绝接收消息
     */
    @Override
    public boolean preReceive(MessageChannel channel) {
        return ChannelInterceptor.super.preReceive(channel);
    }

    /**
     * 消息成功从通道接收后被调用（5.1之后失败不会被调用）
     *
     * @param message 已经接收到的消息
     * @param channel 接收消息的通道
     * @return
     */
    @Override
    public Message<?> postReceive(Message<?> message, MessageChannel channel) {
        return ChannelInterceptor.super.postReceive(message, channel);
    }

    /**
     * 消息接收后的处理（不论成功或者失败）
     *
     * @param message 已经接收到的消息
     * @param channel 接收消息的通道
     * @param ex      如果有异常，将会是异常的引用
     */
    @Override
    public void afterReceiveCompletion(Message<?> message, MessageChannel channel, Exception ex) {
        ChannelInterceptor.super.afterReceiveCompletion(message, channel, ex);
    }

}
