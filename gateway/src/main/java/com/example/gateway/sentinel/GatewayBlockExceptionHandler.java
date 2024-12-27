package com.example.gateway.sentinel;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSON;
import com.example.cloud.sentinel.model.ApiResponses;

/**
 * <p>
 * 网关sentinel异常统一处理
 * <p>
 *
 * @author : 21
 * @since : 2023/9/15 15:49
 */

@Component
public class GatewayBlockExceptionHandler {

    public GatewayBlockExceptionHandler() {
        BlockRequestHandler blockRequestHandler = (exchange, t) -> {
            ApiResponses<Object> apiResponses = new ApiResponses<>();
            String errorMsg = "网络错误 请稍后再试~";
            int status = HttpStatus.INTERNAL_SERVER_ERROR.value();
            if (t instanceof FlowException || t instanceof ParamFlowException) {
                status = HttpStatus.TOO_MANY_REQUESTS.value();
                errorMsg = "您点击太快了 请稍后再试~";
            } else if (t instanceof DegradeException) {
                status = HttpStatus.BAD_REQUEST.value();
            } else if (t instanceof AuthorityException) {
                status = HttpStatus.UNAUTHORIZED.value();
                errorMsg = "无权查看!";
            } else if (t instanceof SystemBlockException) {
                status = HttpStatus.BAD_REQUEST.value();
            }
            apiResponses.setStatus(status);
            apiResponses.setMsg(errorMsg);
            return ServerResponse.status(status)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(JSON.toJSON(apiResponses)));
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }

}
