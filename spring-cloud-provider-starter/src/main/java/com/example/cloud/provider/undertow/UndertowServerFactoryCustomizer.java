package com.example.cloud.provider.undertow;

import java.io.IOException;

import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.xnio.OptionMap;
import org.xnio.Options;
import org.xnio.Xnio;
import org.xnio.XnioWorker;

import io.undertow.connector.ByteBufferPool;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * 配置underTow 缓存池
 * underTow在高并发请求时，使用非阻塞I/O进行交互，性能会快一些
 * 解决 underTow警告：Buffer pool was not set on WebSocketDeploymentInfo, the default pool will be used
 * <p>
 *
 * @author : 21
 * @since : 2023/9/22 16:26
 */
@Slf4j
public class UndertowServerFactoryCustomizer implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {

    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        UndertowDeploymentInfoCustomizer undertowDeploymentInfoCustomizer = deploymentInfo -> {
            //获取web应用程序上下文  通过WebSocketDeploymentInfo.ATTRIBUTE_NAME获取webSocket信息
            WebSocketDeploymentInfo info = (WebSocketDeploymentInfo) deploymentInfo.getServletContextAttributes().get(WebSocketDeploymentInfo.ATTRIBUTE_NAME);
            //配置线程池（包含I/O线程池  工作线程池）
            XnioWorker worker = getXnioWorker();
            //配置WebSocket缓冲区池信息
            ByteBufferPool buffers = new DefaultByteBufferPool(Boolean.getBoolean("io.undertow.websockets.direct-buffers"), 1024, 100, 12);
            info.setWorker(worker);
            info.setBuffers(buffers);
        };
        factory.addDeploymentInfoCustomizers(undertowDeploymentInfoCustomizer);
    }

    /**
     * XnioWorker：
     * 1. 创建和管理 I/O 线程池：使用一个线程池来处理所有的 I/O 操作。线程池中的线程会监听并处理传入的网络连接和请求。通过配置线程池的大小和参数，可以灵活地调整 Undertow 服务器的性能和并发处理能力
     * 2. 事件循环和调度：XnioWorker 使用事件循环机制来监听和调度各种 I/O 事件，包括接收连接、处理请求以及发送响应等。它会以非阻塞的方式处理并发事件，提高服务器的整体响应能力和性能。
     * 3. 提供底层的可扩展性：XnioWorker 的设计使得它可以通过插件来添加新的 I/O 操作方式，以适应不同的需求。它支持多种传输协议和 I/O 处理模型，例如 TCP、UDP、UNIX 域套接字、SSL/TLS 等
     */
    private XnioWorker getXnioWorker() {
        XnioWorker worker = null;
        try {
            worker = Xnio.getInstance().createWorker(OptionMap.create(Options.THREAD_DAEMON, true));
        } catch (IOException ioException) {
            log.warn("XnioWorker cinfigure failed : {}", ioException.getMessage());
        }
        return worker;
    }

}