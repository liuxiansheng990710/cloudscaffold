package com.example.commons.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.commons.core.model.ApiResponses;
import com.example.commons.web.utils.ResponseUtils;

public class SuperController {

    @Autowired
    protected HttpServletRequest request;

    @Autowired
    protected HttpServletResponse response;

    public <T> ApiResponses<T> success(T object) {
        return ResponseUtils.success(response, object);
    }

}
