package com.example.gateway.exceptions.resolver;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.util.Date;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.example.cloud.openfeign.exceptions.ApiFeignClientException;
import com.example.commons.core.enums.GlobalErr;
import com.example.commons.core.exceptions.ApiException;
import com.example.commons.core.log.model.RequestLogger;
import com.example.commons.core.model.ApiResponses;
import com.example.commons.core.model.ErrorCode;
import com.example.commons.core.model.Errors;
import com.example.commons.core.utils.JacksonUtils;
import com.google.common.base.Throwables;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * webflux错误信息解析
 * <p>
 *
 * @author : 21
 * @since : 2024/1/19 10:28
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionResolver {

    /**
     * 返回错误信息并打印错误日志
     *
     * @param exchange
     * @param throwable
     * @return
     */
    public static ApiResponses<?> failedResponse(ServerWebExchange exchange, Throwable throwable) {
        return failedResponse(exchange, getErrors(throwable), throwable);
    }

    /**
     * 返回错误信息并打印错误日志
     *
     * @param exchange
     * @param errors
     * @param throwable
     * @return
     */
    public static ApiResponses<?> failedResponse(ServerWebExchange exchange, Errors errors, Throwable throwable) {
        exchange.getResponse().setRawStatusCode(errors.getStatus());
        ApiResponses<?> failedResponse = new ApiResponses<>();
        failedResponse.setError(errors.getError())
                .setMsg(errors.getMsg())
                .setShow(errors.isShow())
                .setRanking(errors.getRanking())
                .setStatus(errors.getStatus())
                .setRequestId(MDC.get("traceId"))
                .setTime(DateUtil.format(new Date(), DatePattern.ISO8601_FORMAT));
        if (!(throwable instanceof ApiException)) {
            failedResponse.setException(StringUtils.substring(Throwables.getStackTraceAsString(throwable), 0, 4096));
        }
        failedResponse.setException(null);
        //打印错误日志
        RequestLogger logger = RequestLogger.getRequestLogger(exchange, failedResponse, getForwardHosts(exchange));
        RequestLogger.errorPrint(errors.getStatus(), RequestLogger.LOG_PREFIX + logger);
        return failedResponse;
    }

    /**
     * 获取转发hosts
     *
     * @param exchange
     * @return
     */
    private static String getForwardHosts(ServerWebExchange exchange) {
        URI uri = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR);
        return Objects.nonNull(uri) ? uri.getAuthority() : null;
    }

    /**
     * 根据异常类型 获取错误对象
     *
     * @param ex
     * @return
     */
    private static Errors getErrors(Throwable ex) {
        if (ex instanceof ApiException) {
            ApiException exception = (ApiException) ex;
            return exception.getErrors();
        } else if (ex instanceof UndeclaredThrowableException &&
                Objects.nonNull(ex.getCause().getCause()) &&
                ex.getCause().getCause() instanceof ApiFeignClientException) {
            ApiFeignClientException exception = (ApiFeignClientException) ex.getCause().getCause();
            Errors errors = JacksonUtils.readValue(exception.getErrerBodyMsg(), ErrorCode.class);
            return Objects.nonNull(errors) ? errors : GlobalErr.x2002;
        } else {
            final int httpStatus;
            if (ex instanceof ResponseStatusException) {
                ResponseStatusException exception = (ResponseStatusException) ex;
                httpStatus = exception.getStatus().value();
            } else {
                httpStatus = HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
            switch (httpStatus) {
                // 404 没有找到对应的页面、接口
                case org.apache.http.HttpStatus.SC_NOT_FOUND:
                    return GlobalErr.x404.errMsg("请求的路径不存在~");
                // 503 就是服务还没注册上
                case org.apache.http.HttpStatus.SC_SERVICE_UNAVAILABLE:
                    return GlobalErr.x2000;
                // 504 服务响应超时
                case org.apache.http.HttpStatus.SC_GATEWAY_TIMEOUT:
                    return GlobalErr.x504;
                // 默认 500 系统错误
                default:
                    return GlobalErr.x2002;
            }
        }
    }

}
