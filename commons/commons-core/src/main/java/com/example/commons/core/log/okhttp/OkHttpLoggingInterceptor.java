package com.example.commons.core.log.okhttp;

/**
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * {@linkplain okhttp3.OkHttpClient#interceptors() application interceptor} or as a {@linkplain
 * okhttp3.OkHttpClient#networkInterceptors() network interceptor}. <p> The format of the logs created by
 * this class should not be considered stable and may change slightly between releases. If you need
 * a stable logging format, use your own interceptor.
 */

import static okhttp3.internal.platform.Platform.INFO;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;

import com.example.commons.core.log.model.Okttp3Logger;
import com.example.commons.core.utils.JacksonUtils;
import com.google.common.base.Throwables;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okhttp3.internal.platform.Platform;
import okio.Buffer;
import okio.BufferedSource;
import okio.GzipSource;

/**
 * 改写HttpLoggingInterceptor,把日志打成一行便于日志收集
 *
 * <p>因为HttpLoggingInterceptor是final,所以无法继承,升级时请注意升级该类(升级可能造成不兼容)<p/>
 *
 * @see okhttp3.logging.HttpLoggingInterceptor
 */

@SuppressWarnings("all")
public final class OkHttpLoggingInterceptor implements Interceptor {

    private static final Charset UTF8 = StandardCharsets.UTF_8;

    public enum Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         * <p>Example:
         * <pre>{@code
         * --> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
         * }</pre>
         */
        BODY
    }

    public interface Logger {

        void log(String message);

        /**
         * A {@link Logger} defaults output appropriate for the current platform.
         */
        Logger DEFAULT = message -> Platform.get().log(INFO, message, null);
    }

    public OkHttpLoggingInterceptor() {
        this(Logger.DEFAULT, null);
    }

    public OkHttpLoggingInterceptor(Logger logger, String source) {
        this.logger = logger;
        this.source = source;
    }

    private final Logger logger;
    private final String source;

    private volatile Set<String> headersToRedact = Collections.emptySet();

    public void redactHeader(String name) {
        Set<String> newHeadersToRedact = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        newHeadersToRedact.addAll(headersToRedact);
        newHeadersToRedact.add(name);
        headersToRedact = newHeadersToRedact;
    }

    private volatile Level level = Level.NONE;

    /**
     * Change the level at which this interceptor logs.
     */
    public OkHttpLoggingInterceptor setLevel(Level level) {
        if (level == null) {
            throw new NullPointerException("level == null. Use Level.NONE instead.");
        }
        this.level = level;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Okttp3Logger okttp3Logger = new Okttp3Logger();
        okttp3Logger.setOkSource(source);
        Level level = this.level;
        Request request = chain.request();
        if (level == Level.NONE) {
            return chain.proceed(request);
        }
        boolean logBody = level == Level.BODY;
        RequestBody requestBody = request.body();
        boolean hasRequestBody = requestBody != null;
        okttp3Logger.setOkMethod(request.method());
        okttp3Logger.setOkUrl(request.url().toString());
        if (logBody) {
            Headers headers = request.headers();
            Map<String, String> headersMap = new HashMap<>();
            for (int i = 0, count = headers.size(); i < count; i++) {
                String name = headers.name(i);
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name)) {
                    String value = headersToRedact.contains(headers.name(i)) ? "██" : headers.value(i);
                    headersMap.put(headers.name(i), value);
                }
            }
            okttp3Logger.setOkHeader(JacksonUtils.toJson(headersMap));
            if (hasRequestBody && !bodyHasUnknownEncoding(request.headers())) {
                Buffer buffer = new Buffer();
                requestBody.writeTo(buffer);
                if (isPlaintext(buffer)) {
                    okttp3Logger.setOkBody(buffer.readString(UTF8));
                }
            }
        }
        long startNs = System.nanoTime();
        Response response = null;
        try {
            response = chain.proceed(request);
        } catch (Exception e) {
            okttp3Logger.setOkException(StringUtils.substring(Throwables.getStackTraceAsString(e), 0, 4096));
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        okttp3Logger.setRunTime(tookMs + "ms");
        if (logBody && Objects.nonNull(response)) {
            ResponseBody responseBody = response.body();
            okttp3Logger.setOkStatus(response.code());
            long contentLength = responseBody.contentLength();
            Headers headers = response.headers();
            if (HttpHeaders.hasBody(response)) {
                if (!bodyHasUnknownEncoding(response.headers())) {
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();
                    if ("gzip".equalsIgnoreCase(headers.get("Content-Encoding"))) {
                        try (GzipSource gzippedResponseBody = new GzipSource(buffer.clone())) {
                            buffer = new Buffer();
                            buffer.writeAll(gzippedResponseBody);
                        }
                    }
                    if (isPlaintext(buffer)) {
                        if (contentLength != 0) {
                            String data = buffer.clone().readString(UTF8);
                            okttp3Logger.setOkResult(data);
                        }
                    }

                }
            }
        }
        logger.log(Okttp3Logger.LOG_PREFIX + okttp3Logger);
        return response;
    }

    private void logHeader(Headers headers, int i) {
        String value = headersToRedact.contains(headers.name(i)) ? "██" : headers.value(i);
        logger.log(headers.name(i) + ": " + value);
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer) {
        try {
            Buffer prefix = new Buffer();
            long byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++) {
                if (prefix.exhausted()) {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false;
                }
            }
            return true;
        } catch (EOFException e) {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private static boolean bodyHasUnknownEncoding(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null
                && !"identity".equalsIgnoreCase(contentEncoding)
                && !"gzip".equalsIgnoreCase(contentEncoding);
    }

}
