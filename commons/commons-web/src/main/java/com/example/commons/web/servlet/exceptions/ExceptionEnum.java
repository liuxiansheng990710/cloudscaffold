package com.example.commons.web.servlet.exceptions;

import static com.example.commons.core.enums.GlobalErr.x2000;
import static com.example.commons.core.enums.GlobalErr.x2001;
import static com.example.commons.core.enums.GlobalErr.x2011;
import static com.example.commons.core.enums.GlobalErr.x2012;
import static com.example.commons.core.enums.GlobalErr.x2013;
import static com.example.commons.core.enums.GlobalErr.x2014;
import static com.example.commons.core.enums.GlobalErr.x2015;
import static com.example.commons.core.enums.GlobalErr.x2016;
import static com.example.commons.core.enums.GlobalErr.x2017;
import static com.example.commons.core.enums.GlobalErr.x2018;
import static com.example.commons.core.enums.GlobalErr.x2019;
import static com.example.commons.core.enums.GlobalErr.x404;
import static com.example.commons.core.enums.GlobalErr.x405;
import static com.example.commons.core.enums.GlobalErr.x406;
import static com.example.commons.core.enums.GlobalErr.x415;
import static com.example.commons.core.enums.GlobalErr.x510;

import java.io.IOException;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

import com.alibaba.fastjson.JSONException;
import com.example.cloud.openfeign.exceptions.ApiFeignClientException;
import com.example.commons.core.exceptions.ApiException;
import com.example.commons.core.exceptions.CommonUtilsException;
import com.example.commons.core.exceptions.ServerException;
import com.example.commons.web.utils.ResponseUtils;
import com.example.spring.boot.redisson.exceptions.KlockInvocationException;
import com.example.spring.boot.redisson.exceptions.KlockTimeoutException;

/**
 * <p>
 * 异常枚举类
 * <p>
 *
 * @author : 21
 * @since : 2023/12/19 14:57
 */

public enum ExceptionEnum implements GlobalExceptionHandler {

    UNKNOWN_EXCEPTION(null, "未知异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            ResponseUtils.sendFail(request, response, x2000, ex);
        }
    },

    API_EXCEPTION(ApiException.class, "通常为断言异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            ApiException apiException = (ApiException) ex;
            ResponseUtils.sendFail(request, response, apiException.getErrors(), apiException.getException());
        }
    },

    NO_HANDLER_FOUND_EXCEPTION(NoHandlerFoundException.class, "请求的路径没有匹配的处理器时，会抛出该异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            NoHandlerFoundException noHandlerFoundException = (NoHandlerFoundException) ex;
            ResponseUtils.sendFail(request, response, x404, noHandlerFoundException);

        }
    },

    HTTP_REQUEST_METHOD_NOT_SUPPORTED_EXCEPTION(HttpRequestMethodNotSupportedException.class, "当客户端发送了不被支持的 HTTP 请求方法时抛出异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException = (HttpRequestMethodNotSupportedException) ex;
            ResponseUtils.sendFail(request, response, x405, httpRequestMethodNotSupportedException);
        }
    },

    HTTP_MEDIA_TYPE_NOT_ACCEPTABLE_EXCEPTION(HttpMediaTypeNotAcceptableException.class, "无法生成与请求中 Accept 头指定的客户端所需响应类型时抛出异常，表示服务器无法生成客户端请求的媒体类型") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            HttpMediaTypeNotAcceptableException httpMediaTypeNotAcceptableException = (HttpMediaTypeNotAcceptableException) ex;
            ResponseUtils.sendFail(request, response, x406, httpMediaTypeNotAcceptableException);
        }
    },

    HTTP_MEDIA_TYPE_NOT_SUPPORTED_EXCEPTION(HttpMediaTypeNotSupportedException.class, "无法处理请求中 Content-Type 头指定的媒体类型时抛出异常，表示服务器不支持请求的媒体类型") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            HttpMediaTypeNotSupportedException httpMediaTypeNotSupportedException = (HttpMediaTypeNotSupportedException) ex;
            ResponseUtils.sendFail(request, response, x415, httpMediaTypeNotSupportedException);
        }
    },

    ASYNC_REQUEST_TIMEOUT_EXCEPTION(AsyncRequestTimeoutException.class, "异步请求处理超时时，会抛出该异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            AsyncRequestTimeoutException asyncRequestTimeoutException = (AsyncRequestTimeoutException) ex;
            ResponseUtils.sendFail(request, response, x510, asyncRequestTimeoutException);
        }
    },

    HTTP_MESSAGE_NOT_WRITABLE_EXCEPTION(HttpMessageNotWritableException.class, "响应无法正确写入时，例如尝试返回不支持的响应类型，会抛出该异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            HttpMessageNotWritableException httpMessageNotWritableException = (HttpMessageNotWritableException) ex;
            ResponseUtils.sendFail(request, response, x2000, httpMessageNotWritableException);
        }
    },

    CONVERSION_NOT_SUPPORTED_EXCEPTION(ConversionNotSupportedException.class, "类型转换异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            ConversionNotSupportedException conversionNotSupportedException = (ConversionNotSupportedException) ex;
            ResponseUtils.sendFail(request, response, x2000, conversionNotSupportedException);
        }
    },

    COMMON_UTILS_EXCEPTION(CommonUtilsException.class, "自定义工具类错误") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            CommonUtilsException commonUtilsException = (CommonUtilsException) ex;
            ResponseUtils.sendFail(request, response, x2000, commonUtilsException, commonUtilsException.getMessage());
        }
    },

    SERVER_EXCEPTION(ServerException.class, "自定义服务错误") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            ServerException serverException = (ServerException) ex;
            ResponseUtils.sendFail(request, response, x2000, serverException, serverException.getMessage());
        }
    },

    API_FEIGN_CLIENT_EXCEPTION(ApiFeignClientException.class, "自定义跨服务调用错误") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            ApiFeignClientException apiFeignClientException = (ApiFeignClientException) ex;
            response.setStatus(apiFeignClientException.getStatus());
            ResponseUtils.sendFail(request, response, x2000, apiFeignClientException, apiFeignClientException.getMessage());
        }
    },

    KLOCK_INVOCATION_EXCEPTION(KlockInvocationException.class, "分布式锁调用异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            KlockInvocationException klockInvocationException = (KlockInvocationException) ex;
            ResponseUtils.sendFail(request, response, x2000, klockInvocationException, klockInvocationException.getMessage());
        }
    },

    KLOCK_TIMEOUT_EXCEPTION(KlockTimeoutException.class, "分布式锁超时异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            KlockTimeoutException klockTimeoutException = (KlockTimeoutException) ex;
            ResponseUtils.sendFail(request, response, x2000, klockTimeoutException, klockTimeoutException.getMessage());
        }
    },

    HTTP_CLIENT_ERROR_EXCEPTION_UNAUTHORIZED(HttpClientErrorException.Unauthorized.class, "未经授权的HTTP请求") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            HttpClientErrorException.Unauthorized unauthorized = (HttpClientErrorException.Unauthorized) ex;
            ResponseUtils.sendFail(request, response, x2001, unauthorized);
        }
    },

    SERVLET_REQUEST_BINDING_EXCEPTION(ServletRequestBindingException.class, "请求无法正确绑定到Controller方法的参数时，会抛出该异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            ServletRequestBindingException servletRequestBindingException = (ServletRequestBindingException) ex;
            ResponseUtils.sendFail(request, response, x2011, servletRequestBindingException);

        }
    },

    BIND_EXCEPTION(BindException.class, "请求参数绑定到方法参数或表单数据绑定Bean对象时，字段验证失败，类型不匹配等") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            BindException bindException = (BindException) ex;
            ResponseUtils.sendFail(request, response, x2011, bindException);
        }
    },

    CONSTRAINT_VIOLATION_EXCEPTION(ConstraintViolationException.class, "在字段上使用注解进行验证，验证失败时抛出的异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException) ex;
            ResponseUtils.sendFail(request, response, x2011, constraintViolationException);
        }
    },

    HTTP_MESSAGE_NOT_READABLE_EXCEPTION(HttpMessageNotReadableException.class, "请求的消息体无法正确读取时，例如请求的 JSON 数据格式不正确，会抛出该异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            HttpMessageNotReadableException httpMessageNotReadableException = (HttpMessageNotReadableException) ex;
            Throwable cause = httpMessageNotReadableException.getCause();
            //JackSon多态参数绑定异常
            if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidTypeIdException) {
                ResponseUtils.sendFail(request, response, x2012, (com.fasterxml.jackson.databind.exc.InvalidTypeIdException) cause);
            } else {
                ResponseUtils.sendFail(request, response, x2012, httpMessageNotReadableException);
            }
        }
    },

    METHOD_ARGUMENT_NOT_VALID_EXCEPTION(MethodArgumentNotValidException.class, "使用 @Valid 注解对方法参数进行校验时，如果参数校验失败，则会抛出该异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) ex;
            ResponseUtils.sendFail(request, response, x2013, methodArgumentNotValidException);
        }
    },

    MISSING_PATH_VARIABLE_EXCEPTION(MissingPathVariableException.class, "请求时路径中缺少需要的变量，会抛出该异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            MissingPathVariableException missingPathVariableException = (MissingPathVariableException) ex;
            ResponseUtils.sendFail(request, response, x2014, missingPathVariableException);
        }
    },

    MISSING_SERVLET_REQUEST_PARAMETER_EXCEPTION(MissingServletRequestParameterException.class, "请求时缺少必须参数时，会抛出该异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            MissingServletRequestParameterException missingServletRequestParameterException = (MissingServletRequestParameterException) ex;
            ResponseUtils.sendFail(request, response, x2015, missingServletRequestParameterException);
        }
    },

    TYPE_MISMATCH_EXCEPTION(TypeMismatchException.class, "请求参数的类型与目标方法的参数类型不匹配时，会抛出该异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            TypeMismatchException typeMismatchException = (TypeMismatchException) ex;
            ResponseUtils.sendFail(request, response, x2016, typeMismatchException);

        }
    },

    MISSING_SERVLET_REQUEST_PART_EXCEPTION(MissingServletRequestPartException.class, "请求缺少必需的部分，例如文件上传时缺少文件，会抛出该异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            MissingServletRequestPartException missingServletRequestPartException = (MissingServletRequestPartException) ex;
            ResponseUtils.sendFail(request, response, x2017, missingServletRequestPartException);

        }
    },

    JSONEXCEPTION(JSONException.class, "处理/解析JSON数据时发生的异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            JSONException jsonException = (JSONException) ex;
            ResponseUtils.sendFail(request, response, x2018, jsonException);
        }
    },

    IOEXCEPTION(IOException.class, "处理输入输出操作时可能抛出的异常") {
        @Override
        public void handle(HttpServletRequest request, HttpServletResponse response, Exception ex) {
            if (Objects.equals(ex.getMessage(), "Broken pipe")) {
                IOException ioException = (IOException) ex;
                ResponseUtils.sendFail(request, response, x2019, ioException);
            }
        }
    },

    ;

    private final Class<? extends Exception> exception;
    //异常描述，无实质作用
    private final String describe;

    ExceptionEnum(Class<? extends Exception> exception, String describe) {
        this.exception = exception;
        this.describe = describe;
    }

    public Class<? extends Exception> getExceptionClass() {
        return exception;
    }

    public Class<? extends Exception> getException() {
        return exception;
    }

    /**
     * 根据异常获取已知异常类型
     *
     * @param ex
     * @return
     */
    public static ExceptionEnum getExceptionEnum(Exception ex) {
        ExceptionEnum[] exceptionEnums = ExceptionEnum.values();
        for (ExceptionEnum exceptionEnum : exceptionEnums) {
            if (Objects.equals(exceptionEnum.getExceptionClass(), ex.getClass())) {
                return exceptionEnum;
            }
        }
        return UNKNOWN_EXCEPTION;
    }

    /**
     * 处理已知异常
     *
     * @param request
     * @param response
     * @param ex
     */
    public static void dealWithKnownException(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        ExceptionEnum exceptionEnum = getExceptionEnum(ex);
        exceptionEnum.handle(request, response, ex);
    }

}
