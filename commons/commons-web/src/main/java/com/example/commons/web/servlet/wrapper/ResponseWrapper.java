package com.example.commons.web.servlet.wrapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.util.MimeTypeUtils;

import com.example.commons.core.utils.JacksonUtils;
import com.example.commons.core.model.Errors;
import com.google.common.base.Throwables;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * servlet响应构造器
 * <p>
 *
 * @author : 21
 * @since : 2023/12/14 10:03
 */

@Slf4j
public class ResponseWrapper extends HttpServletResponseWrapper {

    private Errors errors;

    /**
     * Constructs a response adaptor wrapping the given response.
     *
     * @param response the {@link HttpServletResponse} to be wrapped.
     * @throws IllegalArgumentException if the response is null
     */
    public ResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    public ResponseWrapper(HttpServletResponse response, Errors errors) {
        super(response);
        setErrors(errors);
    }

    /**
     * 设置ErrorCode
     *
     * @param errors
     */
    public void setErrors(Errors errors) {
        if (Objects.nonNull(errors)) {
            this.errors = errors;
            super.setStatus(this.errors.getStatus());
        }
    }

    public Errors getErrors() {
        return errors;
    }

    /**
     * 向外输出json格式对象
     * flush的主要作用是强制将缓冲区中的内容写入底层的输出流，在缓冲写入的过程中，数据可能会保留在内存中，而不会立即写入到实际的输出设备中。调用 flush() 会触发数据的立即写入
     * - 确保数据到达： 在网络通信中，有时可能希望确保发送的数据尽快到达目标，而不是等到缓冲区满后再发送，刷新缓冲区可以促使数据立即发送。
     * - 避免数据丢失： 在一些场景下，程序可能在写入数据后不久就发生崩溃或关闭，为了避免丢失未写入的数据，可以在写入完成后立即刷新。
     *
     * @param obj
     */
    public void writeValueAsJson(Object obj) {
        //如果已经响应，则不再次返回
        if (super.isCommitted()) {
            log.warn("Response isCommitted, Skip the implementation of the method.");
            return;
        }
        super.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        super.setCharacterEncoding(StandardCharsets.UTF_8.name());
        //TODO 这里暂时不需要客户端缓存，之后集成Etag后，会设置
        super.setHeader("Cache-Control", "no-store");
        super.setHeader("Pragma", "no-cache");
        try {
            PrintWriter writer = super.getWriter();
            writer.print(JacksonUtils.toJson(obj));
            writer.flush();
        } catch (IOException e) {
            log.warn("Error: Response writeValueAsJson faild, stackTrace: {}", Throwables.getStackTraceAsString(e));
        }
    }
}
