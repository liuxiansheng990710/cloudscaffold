package com.example.commons.core.enums;

import com.example.commons.core.exceptions.ServerException;

public enum HTTPMethod {
    GET, POST, PUT, DELETE, PATCH, TRACE, HEAD, OPTIONS;

    /**
     * <p>
     * 获取HTTPMethodEnum
     * </p>
     *
     * @param method 数据库类型字符串
     * @return
     */
    public static HTTPMethod getHttpMethod(String method) {
        HTTPMethod[] httpMethods = HTTPMethod.values();
        for (HTTPMethod httpMethod : httpMethods) {
            if (httpMethod.name().equalsIgnoreCase(method)) {
                return httpMethod;
            }
        }
        throw new ServerException("Error: Unknown HTTPMethod, or do not support changing HTTPMethod!\n");
    }

}