package com.example.provider.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.model.base.entity.mysql.App;

public interface MpTestService extends IService<App> {

    void update(Long id);

}
