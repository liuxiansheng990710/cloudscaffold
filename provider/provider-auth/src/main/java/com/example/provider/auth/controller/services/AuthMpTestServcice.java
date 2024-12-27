package com.example.provider.auth.controller.services;

import java.util.Date;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.example.commons.core.model.MQTestModel;
import com.example.commons.core.utils.JacksonUtils;
import com.example.model.auth.entity.mysql.User;
import com.example.model.auth.mapper.UserMapper;
import com.example.mybatisplus.enhance.service.BaseService;
import com.example.mybatisplus.enhance.service.impl.BaseServiceImpl;

import cn.hutool.core.date.DateUtil;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AuthMpTestServcice extends BaseServiceImpl<UserMapper, User> implements BaseService<User> {

    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private StreamBridge streamBridge;

    @Cacheable(value = "users", key = "#uid")
    public User getUser(Long uid) {
//        Cache cache = cacheManager.getCache("users");
        return getById(uid);
    }

    @Transactional(rollbackFor = Exception.class)
    @GlobalTransactional
    public void addUser() {
        User user = new User();
        user.setName("22");
        save(user);
    }

    public void mqOutPut(Long uid, Integer type) {
        User user = getById(uid);
        JSONObject userJsonObj = JacksonUtils.parseObject(user);
        MQTestModel mqTestModel = new MQTestModel();
        mqTestModel.setJsonObject(userJsonObj);
        String bindingName = type == 1 ? "21-alpha-test-out-0" : "21-alpha-delay-test-out-0";
        MessageBuilder<MQTestModel> messageBuilder = MessageBuilder.withPayload(mqTestModel).setHeader("uid", uid);
        if (type == 2) {
            messageBuilder.setHeader("x-delay", 10000);
        }
        streamBridge.send(bindingName, messageBuilder.build());
        log.info("消息发出去了：" + userJsonObj);
    }

    public int id = 1;

    //    @Bean("21-alpha-test")
    public Supplier<Message<MQTestModel>> mqSupplierOutPut() {
        User user = getById(1710826857378402305L);
        JSONObject userJsonObj = JacksonUtils.parseObject(user);
        MQTestModel mqTestModel = new MQTestModel();
        mqTestModel.setJsonObject(userJsonObj);
        mqTestModel.setId(id);
        return () -> {
            log.info("第{}次发送消息：时间{}", id, DateUtil.dateNew(new Date()));
            id++;
            return MessageBuilder.withPayload(mqTestModel).build();
        };
    }

}
