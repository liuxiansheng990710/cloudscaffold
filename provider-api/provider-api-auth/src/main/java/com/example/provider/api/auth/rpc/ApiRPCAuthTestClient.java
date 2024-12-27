package com.example.provider.api.auth.rpc;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "provider-auth", path = "/rpc/auth", contextId = "apiRPCAuthTestClient")
public interface ApiRPCAuthTestClient {

    @GetMapping("/user/{uid}")
    List<String> getAuthFeignClient(@PathVariable("uid") Long uid);

    @PostMapping("/user")
    void create();

}
