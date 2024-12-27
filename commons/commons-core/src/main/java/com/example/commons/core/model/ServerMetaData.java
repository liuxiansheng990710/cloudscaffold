package com.example.commons.core.model;

import java.io.Serializable;

import com.example.commons.core.enums.ServerEnvironment;
import com.example.commons.core.enums.Servers;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * 服务运行时元数据
 * <p>
 *
 * @author : 21
 * @since : 2023/12/13 14:43
 */

@Setter
@Getter
@NoArgsConstructor
public class ServerMetaData implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 服务应用名
     */
    private String applicationName = Servers.UNKNOWN.applicationName();
    /**
     * Servers枚举
     */
    private Servers servers = Servers.UNKNOWN;
    /**
     * ServerEnvironmentEnum 枚举
     */
    private ServerEnvironment serverEnvironment = ServerEnvironment.UNKNOWN;

    public static ServerMetaData getMetaData() {
        ServerMetaData metaData = new ServerMetaData();
        Servers applicationServer = Servers.getEnvServerName();
        metaData.setApplicationName(applicationServer.applicationName());
        metaData.setServers(applicationServer);
        metaData.setServerEnvironment(ServerEnvironment.getEnvironment());
        return metaData;
    }

}
