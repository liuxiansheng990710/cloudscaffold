package com.example.provider.auth.controller.rpc;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.auth.entity.mysql.User;
import com.example.provider.api.auth.rpc.ApiRPCAuthTestClient;
import com.example.provider.auth.controller.services.AuthMpTestServcice;

@RestController
@RequestMapping("/rpc/auth")
public class RPCAuthTestRestController implements ApiRPCAuthTestClient {

    @Autowired
    private AuthMpTestServcice authMpTestServcice;

    @GetMapping("/user/{uid}")
    public List<String> getAuthFeignClient(@PathVariable("uid") Long uid) {
        User user = authMpTestServcice.getUser(uid);
        authMpTestServcice.addUser();
        return Objects.isNull(user) ? Collections.singletonList("666") : Collections.singletonList(user.toString());
    }

    @Override
    public void create() {
        User user = new User();
        user.setName("定时任务创建");
        authMpTestServcice.save(user);
    }

}
