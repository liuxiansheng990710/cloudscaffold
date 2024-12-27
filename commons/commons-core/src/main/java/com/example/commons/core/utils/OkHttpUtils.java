package com.example.commons.core.utils;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.util.CollectionUtils;

import com.example.commons.core.enums.HTTPMethod;
import com.example.commons.core.exceptions.CommonUtilsException;
import com.example.commons.core.log.okhttp.OkHttpLoggingInterceptor;
import com.example.commons.core.model.OkResult;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * <p>
 * okttp3请求工具类
 * <p>
 *
 * @author : 21
 * @since : 2023/12/5 18:07
 */

@Slf4j
public class OkHttpUtils {

    private static final OkHttpClient httpClient;
    private static final String LOG_SOURCE = "OkHttp3请求结果";
    public static final OkHttpLoggingInterceptor LOGGING_INTERCEPTOR = new OkHttpLoggingInterceptor(message -> {
        if (StringUtils.isNotEmpty(message) && log.isDebugEnabled()) {
            log.debug(message);
        }
    }, LOG_SOURCE).setLevel(OkHttpLoggingInterceptor.Level.BODY);

    private OkHttpUtils() {
    }

    static {
        OkHttpClient.Builder okBuilder = new OkHttpClient.Builder()
                .connectTimeout(2L, TimeUnit.MINUTES)
                .readTimeout(3L, TimeUnit.MINUTES)
                .writeTimeout(2L, TimeUnit.SECONDS)
                .addInterceptor(LOGGING_INTERCEPTOR);
        httpClient = okBuilder.build();
    }

    /**
     * 获取httpClient
     *
     * @return
     */
    public static OkHttpClient getClient() {
        return httpClient;
    }

    /**
     * 发送HttpGet请求
     *
     * @param url
     * @return String
     */
    public static OkResult get(String url) {
        return get(url, Collections.emptyMap());
    }

    /**
     * 发送HttpGet请求
     *
     * @param url
     * @param headers
     * @return
     */
    public static OkResult get(String url, Map<String, String> headers) {
        return request(HTTPMethod.GET, url, headers, Collections.emptyMap());
    }

    /**
     * 发送HttpGet请求
     *
     * @param url
     * @param headers
     * @param params
     * @return String
     */
    public static OkResult get(String url, Map<String, String> headers, Map<String, String> params) {
        return request(HTTPMethod.GET, url, headers, params);
    }

    /**
     * 发送请求
     *
     * @param method
     * @param url
     * @param headers
     * @param params
     * @return
     */
    private static OkResult request(HTTPMethod method, String url, Map<String, String> headers, Map<String, String> params) {
        Request.Builder requestBuild = new Request.Builder();
        addHeaders(requestBuild, headers);
        switch (method) {
            case GET:
                if (!CollectionUtils.isEmpty(params)) {
                    requestBuild.url(url + "?" + getUrlParamsByMap(params));
                } else {
                    requestBuild.url(url);
                }
                requestBuild.get();
                break;
            case DELETE:
                if (!CollectionUtils.isEmpty(params)) {
                    requestBuild.url(url + "?" + getUrlParamsByMap(params));
                } else {
                    requestBuild.url(url);
                }
                requestBuild.delete();
                break;
            case POST:
            case PUT:
            case PATCH:
                requestBuild.url(url);
                FormBody.Builder formBodybuilder = new FormBody.Builder();
                Set<String> paramKeys = params.keySet();
                for (String key : paramKeys) {
                    formBodybuilder.add(key, TypeUtils.castToString(params.get(key), ""));
                }
                FormBody formBody = formBodybuilder.build();
                requestBuild.method(method.name(), formBody);
                break;
            default:
                throw new CommonUtilsException("内部请求方式不正确");
        }
        OkHttpClient okHttpClient = getClient();
        Call call = okHttpClient.newCall(requestBuild.build());
        return getOkResult(call);
    }

    /**
     * 添加请求头
     *
     * @param headers
     * @param requestBuild
     */
    private static void addHeaders(Request.Builder requestBuild, Map<String, String> headers) {
        Set<String> headerKeys = headers.keySet();
        for (String key : headerKeys) {
            requestBuild.addHeader(key, headers.get(key));
        }
    }

    /**
     * 发送HttpPost请求
     *
     * @param url
     * @return String
     */
    public static OkResult post(String url) {
        return post(url, Collections.emptyMap(), Collections.emptyMap());
    }

    /**
     * 发送HttpPost请求
     *
     * @param url
     * @param params
     * @return String
     */
    public static OkResult post(String url, Map<String, String> params) {
        return post(url, Collections.emptyMap(), params);
    }

    /**
     * 发送HttpPost请求
     *
     * @param url
     * @param headers
     * @param params
     * @return String
     */
    public static OkResult post(String url, Map<String, String> headers, Map<String, String> params) {
        return request(HTTPMethod.POST, url, headers, params);
    }

    /**
     * 发送HttpPut请求
     *
     * @param url
     * @return String
     */
    public static OkResult put(String url) {
        return put(url, Collections.emptyMap(), Collections.emptyMap());
    }

    /**
     * 发送HttpPut请求
     *
     * @param url
     * @param params
     * @return String
     */
    public static OkResult put(String url, Map<String, String> params) {
        return put(url, Collections.emptyMap(), params);
    }

    /**
     * 发送HttpPut请求
     *
     * @param url
     * @param params
     * @return String
     */
    public static OkResult put(String url, Map<String, String> headers, Map<String, String> params) {
        return request(HTTPMethod.PUT, url, headers, params);

    }

    /**
     * 发送HttpPatch请求
     *
     * @param url
     * @return String
     */
    public static OkResult patch(String url) {
        return patch(url, Collections.emptyMap(), Collections.emptyMap());
    }

    /**
     * 发送HttpPatch请求
     *
     * @param url
     * @param params
     * @return String
     */
    public static OkResult patch(String url, Map<String, String> params) {
        return patch(url, Collections.emptyMap(), params);
    }

    /**
     * 发送HttpPatch请求
     *
     * @param url
     * @param params
     * @return String
     */
    public static OkResult patch(String url, Map<String, String> headers, Map<String, String> params) {
        return request(HTTPMethod.PATCH, url, headers, params);

    }

    /**
     * 发送带请求体请求只适用于继承HttpEntityEnclosingRequestBase的请求(POST,PUT,PATCH)
     *
     * @param method
     * @param url
     * @param headers
     * @param body
     * @return
     */
    public static OkResult requestBody(HTTPMethod method, String url, Map<String, String> headers, Object body) {
        String content = JacksonUtils.toJson(body);
        Request.Builder requestBuild = new Request.Builder().url(url);
        addHeaders(requestBuild, headers);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), content);
        switch (method) {
            case POST:
                requestBuild.post(requestBody);
                break;
            case PUT:
                requestBuild.put(requestBody);
                break;
            case PATCH:
                requestBuild.patch(requestBody);
                break;
            default:
                throw new CommonUtilsException("内部请求方式不正确");

        }
        OkHttpClient okHttpClient = getClient();
        Call call = okHttpClient.newCall(requestBuild.build());
        return getOkResult(call);

    }

    /**
     * Post请求
     *
     * @param url
     * @param headers
     * @param body
     * @return
     */
    public static OkResult postBody(String url, Map<String, String> headers, Object body) {
        return requestBody(HTTPMethod.POST, url, headers, body);

    }

    /**
     * Put请求
     *
     * @param url
     * @param headers
     * @param body
     * @return
     */
    public static OkResult putBody(String url, Map<String, String> headers, Object body) {
        return requestBody(HTTPMethod.PUT, url, headers, body);

    }

    /**
     * Patch请求
     *
     * @param url
     * @param headers
     * @param body
     * @return
     */
    public static OkResult patchBody(String url, Map<String, String> headers, Object body) {
        return requestBody(HTTPMethod.PATCH, url, headers, body);

    }

    /**
     * Post请求
     *
     * @param url
     * @param body
     * @return
     */
    public static OkResult postBody(String url, Object body) {
        return postBody(url, Collections.emptyMap(), body);

    }

    /**
     * Put请求
     *
     * @param url
     * @param body
     * @return
     */
    public static OkResult putBody(String url, Object body) {
        return putBody(url, Collections.emptyMap(), body);

    }

    /**
     * Patch请求
     *
     * @param url
     * @param object
     * @return
     */
    public static OkResult patchBody(String url, Object object) {
        return patchBody(url, Collections.emptyMap(), object);

    }

    /**
     * 发送Delete请求
     *
     * @param url
     * @return
     */
    public static OkResult delete(String url, Map<String, String> headers) {
        return request(HTTPMethod.DELETE, url, headers, Collections.emptyMap());
    }

    /**
     * 发送Delete请求
     *
     * @param url
     * @return
     */
    public static OkResult delete(String url) {
        return delete(url, Collections.emptyMap());
    }

    /**
     * 发送HttpPost请求
     *
     * @param url
     * @param headers
     * @param params
     * @return String
     */
    public static OkResult delete(String url, Map<String, String> headers, Map<String, String> params) {
        return request(HTTPMethod.DELETE, url, headers, params);
    }

    /**
     * 获取请求返回
     *
     * @param call
     * @return
     */
    private static OkResult getOkResult(Call call) {
        OkResult okResult = OkResult.DEFAULT;
        try {
            Response execute = call.execute();
            okResult = new OkResult(execute.code(), execute.body().string());
        } catch (Exception ignored) {
        }
        return okResult;
    }

    /**
     * 将map转换成url
     *
     * @param map
     * @return
     */
    public static String getUrlParamsByMap(Map<String, String> map) {
        if (CollectionUtils.isEmpty(map)) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            buffer.append(entry.getKey())
                    .append("=")
                    .append(URLUTF8Utils.encode(entry.getValue()));
            buffer.append("&");
        }
        String urlStr = buffer.toString();
        if (urlStr.endsWith("&")) {
            urlStr = org.apache.commons.lang3.StringUtils.substringBeforeLast(urlStr, "&");
        }
        return urlStr;
    }

    /**
     * 将map转换成url不转码
     *
     * @param map
     * @return
     */
    public static String getUrlParamsByMapNoTranscoding(Map<String, String> map) {
        if (CollectionUtils.isEmpty(map)) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            buffer.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
            buffer.append("&");
        }
        String urlStr = buffer.toString();
        if (urlStr.endsWith("&")) {
            urlStr = org.apache.commons.lang3.StringUtils.substringBeforeLast(urlStr, "&");
        }
        return urlStr;
    }

}

