package com.example.provider.base.controller.rpc;

import java.util.Collections;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.provider.api.base.rpc.ApiRPCBaseTestClient;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rpc/base")
@Slf4j
public class RPCBaseTestRestController implements ApiRPCBaseTestClient {

    @Override
    @SentinelResource(value = "/rpc/base", blockHandler = "getBaseFeginTest")
    public List<String> getBaseFeignTest() {
        return Collections.singletonList("我是Feign");
    }

    public List<String> getBaseFeginTest(BlockException e) {
        return Collections.singletonList("我限流了");
    }
}
