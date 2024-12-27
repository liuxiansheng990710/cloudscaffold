package com.example.cloud.sentinel.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSON;
import com.example.cloud.sentinel.model.ApiResponses;

/**
 * <p>
 * Sentinel异常全局处理方法
 * 1. 该返回为自定义返回体（支持修改）
 * 2. 异常类型为BlockException五种子类
 * FlowException             限流异常
 * ParamFlowException        热点参数限流的异常
 * DegradeException          降级异常
 * AuthorityException        授权规则异常
 * SystemBlockException      系统规则异常
 * 3. 尽量使用response原生输出方法
 * <p>
 *
 * @author : 21
 * @since : 2023/8/14 17:39
 */

@Component
public class CustomBlockExceptionHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        ApiResponses<Object> apiResponses = new ApiResponses<>();
        String errorMsg = "网络错误 请稍后再试~";
        int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        if (e instanceof FlowException || e instanceof ParamFlowException) {
            status = HttpStatus.TOO_MANY_REQUESTS.value();
            errorMsg = "您点击太快了 请稍后再试~";
        } else if (e instanceof DegradeException) {
            status = HttpStatus.BAD_REQUEST.value();
        } else if (e instanceof AuthorityException) {
            status = HttpStatus.UNAUTHORIZED.value();
            errorMsg = "无权查看!";
        } else if (e instanceof SystemBlockException) {
            status = HttpStatus.BAD_REQUEST.value();
        }
        apiResponses.setStatus(status);
        apiResponses.setMsg(errorMsg);

        response.setCharacterEncoding("utf-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(JSON.toJSONString(apiResponses));
    }

}
