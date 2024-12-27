package com.example.provider.api.base.fallback;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.provider.api.base.rpc.ApiRPCBaseTestClient;

@Component
public class BaseTestClientFallBack implements ApiRPCBaseTestClient {

    @Override
    public List<String> getBaseFeignTest() {
        return Collections.singletonList("我是异常处理");
    }
}
