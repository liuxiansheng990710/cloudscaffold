package com.example.provider.base.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.model.base.entity.mysql.App;
import com.example.model.base.mapper.AppMapper;
import com.example.provider.api.auth.rpc.ApiRPCAuthTestClient;
import com.example.provider.base.service.MpTestService;

import io.seata.spring.annotation.GlobalTransactional;

@Service
public class MpTestServiceImpl extends ServiceImpl<AppMapper, App> implements MpTestService {

    @Autowired
    private ApiRPCAuthTestClient authTestClient;

    @Transactional(rollbackFor = Exception.class)
    @GlobalTransactional(rollbackFor = Exception.class)
    public void update(Long id) {
        List<String> authFeignClient = authTestClient.getAuthFeignClient(id);
        App app = new App();
        app.setName(authFeignClient.get(0));
//        int a = 1 / 0;
        saveOrUpdate(app);
    }

}
