package com.example.cloud.sentinel.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;

@RestControllerAdvice
public class FeignExceptionHandler {

    private BlockExceptionHandler blockExceptionHandler;

    @Autowired
    public void constructorExceptionHandler(BlockExceptionHandler blockExceptionHandler){
        this.blockExceptionHandler = blockExceptionHandler;
    }

    @ExceptionHandler(BlockException.class)
    public void openFeignException(HttpServletRequest request, HttpServletResponse response, BlockException e) throws Exception {
        blockExceptionHandler.handle(request, response, e);
    }

}
