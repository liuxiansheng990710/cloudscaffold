package com.example.commons.core.mq;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.commons.core.cons.RequestCons;
import com.example.commons.core.utils.JacksonUtils;
import com.example.commons.core.utils.OkHttpUtils;
import com.example.commons.core.utils.StringUtils;
import com.example.commons.core.utils.URLUTF8Utils;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * rabbitmq配置工具类
 * <p>
 *
 * @author : 21
 * @since : 2023/12/6 10:49
 */

@Slf4j
@Component
public class RabbitMQKit {

    /**
     * 队列查询 API
     */
    private static final String API_QUEUES = "%s/api/queues/%s";

    /**
     * 消息移动 API
     */
    private static final String API_PARAMETERS_SHOVEL = "%s/api/parameters/shovel/%s/%s";

    final String rabbitUrl;

    final String vHost;

    final String encodeVHost;

    final Map<String, String> authorization;

    public RabbitMQKit(RabbitProperties properties) {
        this.rabbitUrl = getRabbitURL(properties);
        this.vHost = properties.getVirtualHost();
        this.encodeVHost = URLUTF8Utils.encode(vHost);
        this.authorization = getBasicAuthorizationMap(properties);
    }

    /**
     * 查询Queue消息大于num的Queue
     *
     * @param num
     * @return
     */
    public List<String> findAllQueues(int num) {
        return findQueues(null, num);
    }

    /**
     * 查询Queue消息大于num的Queue
     *
     * @param num
     * @return
     */
    public List<String> findQueues(String endsWith, int num) {
        String queuesString = OkHttpUtils.get(String.format(API_QUEUES, rabbitUrl, encodeVHost), authorization).getResult();
        JSONArray jsonArray = JacksonUtils.parseArray(queuesString);
        List<String> dlqQueues = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("name");
            if ((StringUtils.isBlank(endsWith) || (StringUtils.isNotBlank(endsWith) && name.endsWith(endsWith))) &&
                    jsonObject.getIntValue("messages_ready") > num) {
                dlqQueues.add(name);
            }
        }
        return dlqQueues;
    }

    /**
     * 移动消息
     *
     * @param dlqQueue
     */
    public void moveMessage(String dlqQueue) {
        String destQueue = dlqQueue.replace(".dlq", "");
        String moveName = "Move from " + dlqQueue;
        JSONObject requestBody = new JSONObject();
        requestBody.put("component", "shovel");
        requestBody.put("vhost", vHost);
        // dlq来源
        requestBody.put("name", moveName);
        JSONObject value = new JSONObject();
        value.put("src-uri", "amqp:///" + encodeVHost);
        // dlq来源
        value.put("src-queue", dlqQueue);
        value.put("src-protocol", "amqp091");
        value.put("src-prefetch-count", 1000);
        value.put("src-delete-after", "queue-length");
        value.put("dest-protocol", "amqp091");
        value.put("dest-uri", "amqp:///" + encodeVHost);
        value.put("dest-add-forward-headers", false);
        value.put("ack-mode", "on-confirm");
        // 目标队列
        value.put("dest-queue", destQueue);
        requestBody.put("value", value);
        OkHttpUtils.putBody(String.format(API_PARAMETERS_SHOVEL, rabbitUrl, encodeVHost, moveName), authorization, requestBody);
    }

    /**
     * 获取Basic Authorization
     *
     * @return
     */
    private Map<String, String> getBasicAuthorizationMap(RabbitProperties prop) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put(RequestCons.AUTHORIZATION, "Basic " +
                Base64.getEncoder().encodeToString(
                        (prop.getUsername() + ":" + prop.getPassword())
                                .getBytes()));
        return hashMap;
    }

    /**
     * 获取RabbitMQ Host
     *
     * @return
     */
    private String getRabbitURL(RabbitProperties prop) {
        String addresses = prop.getAddresses();
        return addresses.replace("amqp", "http").replace("5672", "15672");
    }

}

