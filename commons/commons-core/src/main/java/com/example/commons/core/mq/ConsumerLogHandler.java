package com.example.commons.core.mq;

import org.slf4j.MDC;
import org.springframework.messaging.Message;

import com.example.commons.core.log.model.MQRunLogger;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 消费者日志处理器
 * <p>
 *
 * @author : 21
 * @since : 2023/12/9 22:34
 */

@Slf4j
public enum ConsumerLogHandler implements MQLogHandler {

    SENT_SUCCESFUL {
        @Override
        public void handler(Message<?> message, String queueName, Exception ex, long startTime, long endTime) {
            transferMDC(message);
            MQRunLogger runLogger = new MQRunLogger();
            runLogger.setQueueName(queueName)
                    .setSent(Boolean.TRUE)
                    .setSendRunTime(getRunTime(startTime, endTime))
                    .setMsgId(getMessageId(message))
                    .setPayload(processMessage(message))
                    .setHeaders(message.getHeaders());
            log.info(MQRunLogger.CONSUMER_LOG_PREFIX + runLogger);
            MDC.clear();
        }
    },

    SENT_FAILED {
        @Override
        public void handler(Message<?> message, String queueName, Exception ex, long startTime, long endTime) {
            transferMDC(message);
            MQRunLogger runLogger = new MQRunLogger();
            runLogger.setQueueName(queueName)
                    .setSent(Boolean.FALSE)
                    .setSendRunTime(getRunTime(startTime, endTime))
                    .setMsgId(getMessageId(message))
                    .setPayload(processMessage(message))
                    .setException(processException(ex))
                    .setHeaders(message.getHeaders());
            log.error(MQRunLogger.CONSUMER_LOG_PREFIX + runLogger);
            errorSendToDing(queueName);
            MDC.clear();
        }
    }

}
