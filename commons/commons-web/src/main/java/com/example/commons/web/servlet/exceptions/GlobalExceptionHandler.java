package com.example.commons.web.servlet.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 全局异常处理器
 * <p>
 *
 * @author : 21
 * @since : 2023/12/18 17:31
 */

public interface GlobalExceptionHandler {

    void handle(HttpServletRequest request, HttpServletResponse response, Exception ex);

}
