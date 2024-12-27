package com.example.commons.core.log.model;

import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import com.example.commons.core.cons.RequestCons;
import com.example.commons.core.model.ApiResponses;
import com.example.commons.core.utils.IpUtils;
import com.example.commons.core.utils.TypeUtils;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 请求日志
 * <p>
 *
 * @author : 21
 * @since : 2023/12/13 14:25
 */

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Slf4j
public class RequestLogger extends SuperLogger {

    public static final String LOG_PREFIX = "<log> - ";

    /**
     * 请求开始时间
     */
    private Date startTime;
    /**
     * 请求参数
     */
    private Map parameterMap;
    /**
     * 请求体
     */
    private Object requestBody;
    /**
     * 请求路径
     */
    private String url;
    /**
     * 请求mapping
     */
    private String mapping;
    /**
     * 请求方法
     */
    private String method;
    /**
     * 返回结果（成功时正常返回，失败时带异常信息）
     */
    private Object data;
    /**
     * IP地址
     */
    private String ip;
    /**
     * 运行时间 单位:ms
     */
    private String runTime;
    /**
     * 转发Hosts
     */
    private String forwardHosts;

    /**
     * 获取请求日志参数体
     *
     * @param request
     * @param result
     * @return
     */
    public static RequestLogger getRequestLogger(HttpServletRequest request, Object result) {
        Long beiginTime = (Long) request.getAttribute(RequestCons.REQ_BEGIN_TIME);
        Long endTime = System.currentTimeMillis();

        RequestLogger logger = new RequestLogger();
        logger.setParameterMap(request.getParameterMap())
                .setStartTime(TypeUtils.castToDate(request.getAttribute(RequestCons.REQ_BEGIN_TIME)))
                .setRequestBody(request.getAttribute(RequestCons.REQ_BODY))
                .setUrl((String) request.getAttribute(RequestCons.REQ_URL))
                .setMapping((String) request.getAttribute(RequestCons.REQ_MAPPING))
                .setMethod((String) request.getAttribute(RequestCons.REQ_METHOD))
                .setData(result)
                .setIp(Optional.ofNullable(request.getHeader(RequestCons.REQ_X_REAL_IP)).orElse(IpUtils.getIpAddr(request)))
                .setRunTime((Objects.isNull(beiginTime) ? 0 : endTime - beiginTime) + "ms");
        return logger;
    }

    /**
     * webFlux日志参数体
     *
     * @param exchange
     * @param failedResponse
     * @param forwardHosts
     * @return
     */
    public static RequestLogger getRequestLogger(ServerWebExchange exchange, ApiResponses failedResponse, String forwardHosts) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders httpHeaders = request.getHeaders();
        Long beiginTime = TypeUtils.castToLong(httpHeaders.getFirst(RequestCons.REQ_BEGIN_TIME));
        Long endTime = System.currentTimeMillis();
        String ipAddress = httpHeaders.getFirst(RequestCons.REQ_X_REAL_IP);

        RequestLogger logger = new RequestLogger();
        logger.setParameterMap(request.getQueryParams())
                .setUrl(request.getPath().value())
                .setMapping(httpHeaders.getFirst(RequestCons.REQ_MAPPING))
                .setMethod(RequestCons.REQ_METHOD)
                .setRunTime((beiginTime != null ? endTime - beiginTime : 0) + "ms")
                .setData(failedResponse)
                .setRequestBody(exchange.getAttribute(RequestCons.REQ_BODY))
                .setIp(StringUtils.isNotBlank(ipAddress) ? ipAddress : IpUtils.getIpAddr(httpHeaders, request))
                .setForwardHosts(forwardHosts);
        return logger;
    }

    /**
     * 根据http状态打印不同级别日志
     *
     * @param status
     * @param logger
     */
    public static void print(int status, String logger) {
        if (status >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            log.error(logger);
            return;
        }
        log.info(logger);
    }

    /**
     * 根据http状态打印不同级别~~错误日志~~
     *
     * @param status
     * @param logger
     */
    public static void errorPrint(int status, String logger) {
        if (status >= HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
            log.error(logger);
            return;
        }
        log.warn(logger);
    }

}
