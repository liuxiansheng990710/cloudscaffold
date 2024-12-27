package com.example.gateway.exceptions.handler;

import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;

import com.example.commons.core.utils.JacksonUtils;
import com.example.gateway.cons.GatewayOrderCons;
import com.example.gateway.exceptions.resolver.ExceptionResolver;

import reactor.core.publisher.Mono;

/**
 * <p>
 * webflux错误处理器
 * 优先级应该低于 {@link org.springframework.web.reactive.handler.WebFluxResponseStatusExceptionHandler} 执行
 * 默认是0 所以选择-1 在默认处理器之前就已经执行完毕并返回
 * <p>
 *
 * @author : 21
 * @since : 2024/1/19 10:29
 */

public class WebFluxExceptionHandler implements ErrorWebExceptionHandler, Ordered {

    @Override
    public int getOrder() {
        return GatewayOrderCons.GOLBAL_GATEWAY_EXCEPTION_HANDLER;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        //如果已经响应 则无法修改状态码以及其他信息（所以重新发送一次错误信息）
        if (response.isCommitted()) {
            return Mono.error(ex);
        }
        ServerHttpRequest request = exchange.getRequest();
        //如果是请求的静态资源（例如.jpg/.png） 则直接返回空Mono
        if (request.getPath().value().contains(".")) {
            return Mono.empty();
        }
        //响应错误并且打印错误信息
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            byte[] bytes = JacksonUtils.toBytes(ExceptionResolver.failedResponse(exchange, ex));
            return bufferFactory.wrap(bytes);
        }));
    }
}
