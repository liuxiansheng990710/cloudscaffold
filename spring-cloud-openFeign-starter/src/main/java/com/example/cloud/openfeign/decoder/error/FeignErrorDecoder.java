package com.example.cloud.openfeign.decoder.error;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.example.cloud.openfeign.exceptions.ApiFeignClientException;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * Feign异常解码器  原生
 * <p>
 *
 * @author : 21
 * @since : 2023/7/19 10:47
 */

@Slf4j
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        try {
            Response.Body body = response.body();
            if (Objects.nonNull(body)) {
                String bodyString = Util.toString(body.asReader(StandardCharsets.UTF_8));
                return new ApiFeignClientException(response.status(), bodyString);
            } else {
                return new ApiFeignClientException(response.status(), String.format("FeignError: methodKey is [%s], Response.Status is [%s].", methodKey, response.status()));
            }
        } catch (IOException e) {
            return new ApiFeignClientException("网络错误，请重试~");
        }
    }
}
