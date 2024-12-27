package com.example.provider.api.base.rpc;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "provider-base", path = "/rpc/base", contextId = "apiRPCBaseTestClient"/*, fallback = BaseTestClientFallBack.class*/)
public interface ApiRPCBaseTestClient {

    @GetMapping("/test")
    List<String> getBaseFeignTest();

}
