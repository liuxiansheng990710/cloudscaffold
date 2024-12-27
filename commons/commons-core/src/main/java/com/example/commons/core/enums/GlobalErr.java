package com.example.commons.core.enums;

import org.springframework.http.HttpStatus;

import com.example.commons.core.model.Errors;

public enum GlobalErr implements Errors {

    x404(HttpStatus.NOT_FOUND, ErrorLevel.UNEXPECTED, "未找到", true),
    x405(HttpStatus.METHOD_NOT_ALLOWED, ErrorLevel.UNEXPECTED, "请求方式不支持", true),
    x406(HttpStatus.NOT_ACCEPTABLE, ErrorLevel.UNEXPECTED, "无法生成该请求的媒体类型", true),
    x415(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ErrorLevel.UNEXPECTED, "不支持的请求媒体类型", true),
    x504(HttpStatus.GATEWAY_TIMEOUT, ErrorLevel.CRASH, "网络错误，请重试", true),
    x510(HttpStatus.REQUEST_TIMEOUT, ErrorLevel.CRASH, "处理异步请求超时", true),

    x2000(HttpStatus.INTERNAL_SERVER_ERROR, ErrorLevel.CRASH, "网络错误，请重试", true),
    x2001(HttpStatus.BAD_REQUEST, ErrorLevel.REMIND, "用户名或密码错误", true),
    x2002(HttpStatus.INTERNAL_SERVER_ERROR, ErrorLevel.CRASH, "网络好像出了点问题~，请稍后重试", true),

    /**
     * 接口请求参数错误
     */
    x2011(HttpStatus.BAD_REQUEST, ErrorLevel.UNEXPECTED, "参数错误", true),
    x2012(HttpStatus.BAD_REQUEST, ErrorLevel.UNEXPECTED, "参数请求格式错误", true),
    x2013(HttpStatus.BAD_REQUEST, ErrorLevel.UNEXPECTED, "参数校验失败，请检查参数", true),
    x2014(HttpStatus.BAD_REQUEST, ErrorLevel.UNEXPECTED, "路径参数错误", true),
    x2015(HttpStatus.BAD_REQUEST, ErrorLevel.UNEXPECTED, "请求参数缺失", true),
    x2016(HttpStatus.BAD_REQUEST, ErrorLevel.UNEXPECTED, "参数类型错误", true),
    x2017(HttpStatus.BAD_REQUEST, ErrorLevel.UNEXPECTED, "Multipart请求参数错误", true),
    x2018(HttpStatus.BAD_REQUEST, ErrorLevel.UNEXPECTED, "参数错误：Json解析出错", true),
    x2019(HttpStatus.NOT_ACCEPTABLE, ErrorLevel.REMIND, "客户端提前断开连接", true),



    ;

    private final HttpStatus status;
    private final ErrorLevel level;
    private final String msg;
    private final boolean show;

    GlobalErr(HttpStatus status, ErrorLevel level, String msg, boolean show) {
        this.status = status;
        this.level = level;
        this.msg = msg;
        this.show = show;
    }

    @Override
    public String getError() {
        return name();
    }

    @Override
    public int getStatus() {
        return status.value();
    }

    @Override
    public String getRanking() {
        return level.name();
    }

    @Override
    public String getMsg() {
        return msg;
    }

    @Override
    public boolean isShow() {
        return show;
    }
}
