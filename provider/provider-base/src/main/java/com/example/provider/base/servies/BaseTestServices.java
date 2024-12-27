package com.example.provider.base.servies;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSONObject;
import com.example.commons.core.model.MQTestModel;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BaseTestServices {

    @SentinelResource(value = "/public-methods", blockHandler = "sentienlException")
    public String sentienl() {
        return "我是公共方法！！！";
    }

    public String sentienlException(BlockException blockExceptione) {
        blockExceptione.printStackTrace();
        return "公共方法流控";
    }

    @Bean("21-alpha-test")
    public Consumer<Message<MQTestModel>> mqInputTest() {
        return message -> {
            MQTestModel payload = message.getPayload();
            Long header = (Long) message.getHeaders().get("uid");
            JSONObject jsonObject = payload.getJsonObject();
            log.info("Base消息到了：----------header:{}" + jsonObject, header);
            int a = 1/0;
        };
    }

    @Bean("21-alpha-delay-test")
    public Consumer<Message<MQTestModel>> mqDelayInputTest() {
        return message -> {
            MQTestModel payload = message.getPayload();
            Long header = (Long) message.getHeaders().get("uid");
            JSONObject jsonObject = payload.getJsonObject();
            log.info("消息到了：----------header:{}" + jsonObject, header);
        };
    }

}