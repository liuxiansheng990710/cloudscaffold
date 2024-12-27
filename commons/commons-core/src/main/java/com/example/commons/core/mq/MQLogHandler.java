package com.example.commons.core.mq;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.messaging.Message;

import com.alibaba.fastjson.JSONObject;
import com.example.commons.core.utils.JacksonUtils;
import com.example.commons.core.utils.OkHttpUtils;

/**
 * <p>
 * mq日志执行器
 * <p>
 *
 * @author : 21
 * @since : 2023/12/9 18:32
 */

public interface MQLogHandler {

    /**
     * 生产者/消费者 判断规则
     */
    String PRODUCER_DECISION_RULES = "-out-";
    String CONSUMER_DECISION_RULES = "-in-";

    void handler(Message<?> message, String queueName, Exception ex, long startTime, long endTime);

    /**
     * 根据队列名称 获取判断规则
     *
     * @param queueName
     * @return
     */
    static String extractQueueRules(String queueName) {
        if (queueName.contains(PRODUCER_DECISION_RULES)) {
            return PRODUCER_DECISION_RULES;
        } else if (queueName.contains(CONSUMER_DECISION_RULES)) {
            return CONSUMER_DECISION_RULES;
        }
        return StringUtils.EMPTY;
    }

    /**
     * 处理message消息体
     *
     * @param message
     * @return
     */
    default JSONObject processMessage(Message<?> message) {
        String payloadJson = new String((byte[]) message.getPayload());
        return JacksonUtils.parseObject(payloadJson);
    }

    /**
     * 处理异常信息 控制长度
     *
     * @param exception
     * @return
     */
    default String processException(Exception exception) {
        return Objects.isNull(exception) ? null : StringUtils.substring(exception.getMessage(), 0, 4096);
    }

    /**
     * 获取运行时长
     *
     * @param startTime
     * @param endTime
     * @return
     */
    default String getRunTime(long startTime, long endTime) {
        long runTime = endTime - startTime;
        return runTime + "ms";
    }

    default String getMessageId(Message<?> message) {
        UUID msgId = message.getHeaders().getId();
        if (Objects.nonNull(msgId)) {
            return msgId.toString();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 解析头中参数，手动传递MDC所需参数
     *
     * @param message
     */
    default void transferMDC(Message<?> message) {
        Optional.ofNullable(message.getHeaders().get("b3")).ifPresent(sleuthB3 -> {
            String[] contextSource = sleuthB3.toString().split("-");
            HashMap<String, String> coentextMap = new HashMap<>();
            coentextMap.put("traceId", contextSource[0]);
            coentextMap.put("spanId", contextSource[1]);
            MDC.setContextMap(coentextMap);
        });
    }

    /**
     * 发送钉钉群错误提示
     *
     * @param queueName
     */
    default void errorSendToDing(String queueName) {
        StringBuilder builder = new StringBuilder();
        String str = Objects.equals(extractQueueRules(queueName), CONSUMER_DECISION_RULES) ? "未正常消费" : "发送失败";
        builder.append("#### 消息队列告警\n")
                .append("> 以下为")
                .append(str)
                .append("的Queues\n")
                .append("\n");
        builder.append("- ");
        builder.append(queueName);
        builder.append("\n");
        builder.append("\n")
                .append("> 请对应开发及时查看");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype", "markdown");
        JSONObject markdown = new JSONObject();
        markdown.put("title", "消息队列告警");
        markdown.put("text", builder.toString());
        jsonObject.put("markdown", markdown);
        jsonObject.put("isAtAll", true);
        //此处为自创测试群
        OkHttpUtils.postBody("https://oapi.dingtalk.com/robot/send?access_token=e8e7572b9ecb0d7fbc1c3060a2436bc118af4226ae4a6462a3708e4e466b313e", jsonObject);
    }
}
