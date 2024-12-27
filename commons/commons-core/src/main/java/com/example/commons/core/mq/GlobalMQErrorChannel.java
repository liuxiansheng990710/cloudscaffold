package com.example.commons.core.mq;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.amqp.utils.SerializationUtils;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import com.example.commons.core.log.model.MQErrorLogger;
import com.example.commons.core.utils.TypeUtils;
import com.google.common.base.Throwables;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * mq全局异常处理 这里其实可以不用，用拦截器替代
 * <p>
 *
 * @author : 21
 * @since : 2023/12/7 9:47
 */

@Slf4j
@Component
@Deprecated
public class GlobalMQErrorChannel {

    private static final Set<String> whiteListPatterns =
            new LinkedHashSet<>(Arrays.asList("java.util.*", "java.lang.*"));

    /**
     * 默认字符集
     */
    private static final String DEFAULT_CHARSET = Charset.defaultCharset().name();

//    @StreamListener("errorChannel")
    //spring boot 3 彻底废弃此用法 所以提前替换
    public void globalMqErrorHandler(ErrorMessage errorMessage) {
        Message<?> originalMessage = errorMessage.getOriginalMessage();
        if (Objects.nonNull(originalMessage)) {
            String stackTrace = Throwables.getStackTraceAsString(errorMessage.getPayload().getCause());
            MessageHeaders headers = originalMessage.getHeaders();
            String queueName = headers.get(AmqpHeaders.CONSUMER_QUEUE, String.class);
            Date timestamp = headers.get(AmqpHeaders.TIMESTAMP, Date.class);
            org.springframework.amqp.core.Message message = headers.get("sourceData", org.springframework.amqp.core.Message.class);
            String body = getBodyContentAsString(message);
            MQErrorLogger mqErrorLogger = new MQErrorLogger();
            //减少日志长度
            mqErrorLogger.setException(StringUtils.substring(stackTrace, 0, 4096));
            mqErrorLogger.setQueueName(queueName);
            mqErrorLogger.setTime(Objects.nonNull(timestamp) ? timestamp.toInstant().atOffset(ZoneOffset.of("+8")).toLocalDateTime().toString() : LocalDateTime.now(ZoneOffset.of("+8")).toString());
            mqErrorLogger.setMqBody(body);
            log.error(MQErrorLogger.LOG_PREFIX + mqErrorLogger);
        }

    }

    /**
     * 获取消息体内容
     *
     * @param message
     * @return
     */
    private String getBodyContentAsString(org.springframework.amqp.core.Message message) {
        if (Objects.nonNull(message)) {
            byte[] body = message.getBody();
            if (body == null) {
                return null;
            }
            MessageProperties properties = message.getMessageProperties();
            String contentType = properties.getContentType();
            String contentEncoding = properties.getContentEncoding();
            contentEncoding = StringUtils.isNotBlank(contentEncoding) ? contentEncoding : DEFAULT_CHARSET;
            try {
                if (MessageProperties.CONTENT_TYPE_SERIALIZED_OBJECT.equals(contentType)) {
                    return SerializationUtils.deserialize(new ByteArrayInputStream(body), whiteListPatterns,
                            ClassUtils.getDefaultClassLoader()).toString();
                }
                if (MessageProperties.CONTENT_TYPE_TEXT_PLAIN.equals(contentType)
                        || MessageProperties.CONTENT_TYPE_JSON.equals(contentType)
                        || MessageProperties.CONTENT_TYPE_JSON_ALT.equals(contentType)
                        || MessageProperties.CONTENT_TYPE_XML.equals(contentType)) {
                    return new String(body, contentEncoding);
                }
            } catch (Exception e) {
                // ignore
            }
            return TypeUtils.castToString(body) + "(byte[" + body.length + "])";
        }
        return null;
    }

}
