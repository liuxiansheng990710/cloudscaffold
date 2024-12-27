package com.example.provider.auth.controller.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.commons.core.enums.GlobalErr;
import com.example.commons.core.exceptions.ServerException;
import com.example.commons.core.mq.RabbitMQKit;
import com.example.commons.core.utils.ApiAssert;
import com.example.commons.core.utils.JacksonUtils;
import com.example.commons.core.utils.ThreadUtils;
import com.example.commons.core.utils.TypeUtils;
import com.example.model.auth.dto.UserDTO;
import com.example.model.auth.entity.mysql.User;
import com.example.provider.api.base.rpc.ApiRPCBaseTestClient;
import com.example.provider.auth.controller.services.AuthMpTestServcice;
import com.example.provider.auth.controller.services.KlockService;
import com.example.spring.boot.redisson.properties.RedissonProperties;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@Slf4j
@Tag(name = "测试相关接口")
public class AuthTestRestController {

    @Autowired
    private ApiRPCBaseTestClient apiRPCBaseTestClient;
    @Autowired
    private AuthMpTestServcice authService;
    @Autowired
    private RedissonProperties redissonProperties;
    @Autowired
    private KlockService klockService;
    @Autowired
    private RabbitProperties rabbitProperties;

    @GetMapping("/test")
    @Parameter(name = "name", description = "定时任务的ID", required = true, in = ParameterIn.QUERY, schema = @Schema(type = "String"))
    public List<String> test(@RequestParam("name") String name) {
        User one = authService.getOne(Wrappers.<User>lambdaQuery().eq(User::getUid, 1710826857378402305L));
        Long obj = authService.getObj(Wrappers.<User>lambdaQuery().select(User::getUid).eq(User::getUid, 1710826857378402305L), TypeUtils::castToLong);
        List<UserDTO> entitys = authService.listEntitys(Wrappers.<User>lambdaQuery().eq(User::getUid, 1710826857378402305L), e -> e.convert(UserDTO.class));
        List<UserDTO> entitys2 = authService.listEntitys(Wrappers.<User>lambdaQuery().eq(User::getUid, 1710826857378402305L), UserDTO.class);
        Page<User> page = authService.page(new Page<>(), Wrappers.emptyWrapper());
        Page<UserDTO> dtoPage = authService.page(new Page<>(), Wrappers.emptyWrapper(), UserDTO.class);
        List<String> feignTest = apiRPCBaseTestClient.getBaseFeignTest();
        ArrayList<String> objects = new ArrayList<>();
        objects.add("我成功了");
        objects.addAll(feignTest);
        objects.add(name);
        User user = authService.getUser(1710826857378402305L);
        objects.add(JacksonUtils.toJson(user));
        objects.add(JacksonUtils.toJson(one));
        ThreadUtils.execute(() -> {
            authService.getUser(1L);
            log.info("我是异步");
        });
        ThreadUtils.submit(() -> log.error("我错了"));
        return objects;
    }

    @PostMapping("/body-test/{id}")
    public List<String> testBody(@RequestBody() List<Long> ids, @RequestParam("name") String name, @PathVariable("id") Long id) {
        ArrayList<String> objects = new ArrayList<>();
        objects.add("我成功了");
        objects.add(name);
        objects.addAll(ids.stream().map(Objects::toString).collect(Collectors.toList()));
        User user = authService.getUser(id);
        objects.add(JacksonUtils.toJson(user));
        String abc = "";
        ApiAssert.notNull(GlobalErr.x2000, abc);
        if (Objects.equals(abc, "123")) {
            throw new ServerException("我是自定义错误");
        }
        return objects;
    }

    @GetMapping("/test-klock")
    public void test2() throws InterruptedException {
        klockService.getFairLockSleep("拿到锁");
    }

    @GetMapping("/test-klock2")
    public void test3() throws InterruptedException {
        klockService.getFairLockDoNothing("拿到锁", 2100000L);
    }

    @GetMapping("/test-mq/{uid}")
    public void rabbitMQTest(@PathVariable("uid") Long uid, @RequestParam("type") Integer type) {
        authService.mqOutPut(uid, type);
    }

    @PutMapping("/move-mq")
    public void moveDlqMQ() {
        RabbitMQKit rabbitMQKit = new RabbitMQKit(rabbitProperties);
        List<String> dlqQueues = rabbitMQKit.findQueues(".dlq", 0);
        dlqQueues.forEach(rabbitMQKit::moveMessage);
    }

}
