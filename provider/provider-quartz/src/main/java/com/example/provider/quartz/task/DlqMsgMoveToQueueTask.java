package com.example.provider.quartz.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.example.commons.core.mq.RabbitMQKit;
import com.example.provider.quartz.common.IExecuteQuartzJob;

/**
 * <p>
 * 死信队列消息重回队列定时任务（需要添加至定时任务中）
 * <p>
 *
 * @author : 21
 * @since : 2023/12/6 14:59
 */

@Component
public class DlqMsgMoveToQueueTask implements IExecuteQuartzJob {

    @Autowired
    private RabbitProperties rabbitProperties;

    @Override
    public void execute(Long jobId, JSONObject parm) {
        RabbitMQKit rabbitMQKit = new RabbitMQKit(rabbitProperties);
        List<String> dlqQueues = rabbitMQKit.findQueues(".dlq", 0);
        dlqQueues.forEach(rabbitMQKit::moveMessage);
    }
}
