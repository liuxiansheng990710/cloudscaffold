package com.example.provider.quartz.common;

import java.lang.reflect.Method;

import org.springframework.util.ReflectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.example.commons.core.exceptions.ServerException;
import com.example.commons.core.utils.ApplicationUtils;

/**
 * <p>
 * 定时任务执行方法
 * <p>
 *
 * @author : 21
 * @since : 2023/9/25 16:43
 */

public class QuartzRunnable implements Runnable {

    //任务执行目标
    private final Object target;
    private final Method method;
    private final Long jobId;
    private final JSONObject params;

    QuartzRunnable(String className, Long jobId, JSONObject params) throws NoSuchMethodException {
        this.target = ApplicationUtils.getBean(ApplicationUtils.forName(className));
        //获取excute方法
        this.method = target.getClass().getDeclaredMethod("execute", Long.class, JSONObject.class);
        this.jobId = jobId;
        this.params = params;
    }

    @Override
    public void run() {
        try {
            //将该方法访问修饰符设置为pubilc（也就是可访问）
            ReflectionUtils.makeAccessible(method);
            method.invoke(target, jobId, params);
        } catch (Exception e) {
            throw new ServerException("Failed to execution scheduled task", e);
        }
    }
}
