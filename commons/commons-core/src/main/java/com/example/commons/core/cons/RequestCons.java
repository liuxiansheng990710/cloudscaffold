package com.example.commons.core.cons;

/**
 * <p>
 * 请求常量
 * <p>
 *
 * @author : 21
 * @since : 2023/12/14 17:05
 */

public interface RequestCons {

    /**
     * 认证头
     */
    String AUTHORIZATION = "Authorization";

    //-----------------------------------------------------------------------暂存Servlet的request参数--------------------------------------------------------------------------//
    String REQ_BEGIN_TIME = "REQ_BEGIN_TIME";
    String REQ_URL = "REQ_REQURL";
    String REQ_MAPPING = "REQ_MAPPING";
    String REQ_METHOD = "REQ_METHOD";
    String REQ_BODY = "REQ_BODY";
    String REQ_X_REAL_IP = "X-Real-IP";

}
