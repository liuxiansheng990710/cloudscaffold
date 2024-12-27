package com.example.mybatisplus.enhance.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.baomidou.mybatisplus.core.MybatisXMLLanguageDriver;
import com.example.mybatisplus.enhance.injector.ColumnsCheckInjector;
import com.example.mybatisplus.enhance.metahandler.CustomMetaObjectHandler;

/**
 * <p>
 * mybaits-plus支持注册类
 * <p>
 *
 * @author : 21
 * @since : 2023/10/8 10:37
 */

@Configuration
@ConditionalOnClass(MybatisXMLLanguageDriver.class)
public class MPSupportAutoConfiguration {

    /**
     * 自动填充
     */
    @Bean
    public CustomMetaObjectHandler customMetaObjectHandler() {
        return new CustomMetaObjectHandler();
    }

    @Bean
    @Primary
    /**
     * 数据库字段缺失检测
     */
    public ColumnsCheckInjector columnsCheckInjector() {
        return new ColumnsCheckInjector();
    }

}
