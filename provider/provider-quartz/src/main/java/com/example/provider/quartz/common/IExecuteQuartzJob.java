package com.example.provider.quartz.common;

import com.alibaba.fastjson.JSONObject;

/**
 * <p>
 * 定时任务执行接口
 * <p>
 *
 * @author : 21
 * @since : 2023/9/25 18:11
 */

public interface IExecuteQuartzJob {

    /**
     * 执行定时任务
     *
     * @param jobId
     * @param parm
     */
    void execute(Long jobId, JSONObject parm);

}
