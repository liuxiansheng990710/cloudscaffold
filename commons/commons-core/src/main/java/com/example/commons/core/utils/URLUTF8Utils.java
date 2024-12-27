package com.example.commons.core.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * <p>
 * URLUTF8编码解码器
 * <p>
 *
 * @author : 21
 * @since : 2023/12/5 18:10
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class URLUTF8Utils {

    public static String encode(String src) {
        return URLEncoder.encode(src, StandardCharsets.UTF_8);
    }

    public static String decode(String src) {
        return URLDecoder.decode(src, StandardCharsets.UTF_8);
    }

}
