package com.example.commons.core.mq;

import org.springframework.messaging.Message;

import com.example.commons.core.log.model.MQRunLogger;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 生产者日志处理器
 * <p>
 *
 * @author : 21
 * @since : 2023/12/9 18:25
 */

@Slf4j
public enum ProducerLogHandler implements MQLogHandler {

    SENT_SUCCESFUL {
        @Override
        public void handler(Message<?> message, String queueName, Exception ex, long startTime, long endTime) {
            MQRunLogger runLogger = new MQRunLogger();
            runLogger.setQueueName(queueName)
                    .setSent(Boolean.TRUE)
                    .setSendRunTime(getRunTime(startTime, endTime))
                    .setMsgId(getMessageId(message))
                    .setPayload(processMessage(message))
                    .setHeaders(message.getHeaders());
            log.info(MQRunLogger.PRODUCER_LOG_PREFIX + runLogger);
        }
    },

    SENT_FAILED {
        @Override
        public void handler(Message<?> message, String queueName, Exception ex, long startTime, long endTime) {
            MQRunLogger runLogger = new MQRunLogger();
            runLogger.setQueueName(queueName)
                    .setSent(Boolean.FALSE)
                    .setSendRunTime(getRunTime(startTime, endTime))
                    .setMsgId(getMessageId(message))
                    .setPayload(processMessage(message))
                    .setException(processException(ex))
                    .setHeaders(message.getHeaders());
            log.error(MQRunLogger.PRODUCER_LOG_PREFIX + runLogger);
            errorSendToDing(queueName);
        }
    }

}
