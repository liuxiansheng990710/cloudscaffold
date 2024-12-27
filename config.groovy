environments {
    alpha {
        profile = 'alpha'
        nacos {
            addr = '47.120.38.241:8848'
            group = 'ALPHA_GROUP'
        }
        sentinel {
            dashboard = '127.0.0.1:8858'
            //不要动这个格式
            filterUrl = '\n          - /v1/** \n          - /v2/** \n          - /sys/**'
        }
        seata {
            addr = '47.94.0.13:8091'
        }
        redisson {
            config = 'classpath:redisson-alpha-config.json'
        }
        knife4j {
            username = '21'
            password = '21'
        }
    }

    beta {
        profile = 'beta'
        nacos {
            addr = '47.120.38.241:8848'
            group = 'BETA_GROUP'
        }
        sentinel {
            dashboard = '47.94.0.13:8858'
            clientIp = '47.94.0.13'
            gatewayPort = '8720'
            authPort = '8721'
            basePort = '8722'
            quartzPort = '8723'
            //不要动这个格式
            filterUrl = '\n          - /v1/** \n          - /v2/** \n          - /sys/**'
        }
        seata {
            addr = '47.94.0.13:8091'
        }
        redisson {
            config = 'classpath:redisson-beta-config.json'
        }
        knife4j {
            username = '21'
            password = '21'
        }
    }

    release {
        profile = 'release'
        nacos {
            addr = '47.120.38.241:8848'
            group = 'RELEASE_GROUP'
        }
        sentinel {
            dashboard = '47.94.0.13:8858'
            clientIp = '47.94.0.13'
            gatewayPort = '8720'
            authPort = '8721'
            basePort = '8722'
            quartzPort = '8723'
            //不要动这个格式
            filterUrl = '\n          - /v1/** \n          - /v2/** \n          - /sys/**'
        }
        seata {
            addr = '47.94.0.13:8091'
        }
        redisson {
            config = 'classpath:redisson-beta-config.json'
        }
        knife4j {
            username = '21'
            password = '21'
        }
    }
}