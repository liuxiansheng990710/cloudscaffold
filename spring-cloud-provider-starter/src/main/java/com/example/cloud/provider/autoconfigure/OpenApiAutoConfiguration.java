package com.example.cloud.provider.autoconfigure;

import static com.example.commons.core.cons.RequestCons.AUTHORIZATION;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import com.example.cloud.sentinel.config.properties.AuthOriginConfiguration;
import com.example.commons.core.enums.Servers;

import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;

/**
 * <p>
 * openApi全局配置
 * <p>
 *
 * @author : 21
 * @since : 2024/1/9 14:31
 */

@Configuration
public class OpenApiAutoConfiguration {

    @Autowired
    private AuthOriginConfiguration authOriginConfiguration;

    private static final String SCAN_PACKAGE = "com.example.provider";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("21脚手架")
                        .description("21脚手架 Swagger API 文档")
                        .version("1.0")
                        .termsOfService(Servers.getEnvServerName().desc())
                        .contact(new Contact().name("21")));
    }

    @Bean
    public GroupedOpenApi sysApi() {
        return GroupedOpenApi.builder()
                .group("SYS-API")
                .pathsToMatch("/sys/**")
                .packagesToScan(SCAN_PACKAGE)
                .addOperationCustomizer((operation, handlerMethod) -> {
                    globalParameter().forEach(operation::addParametersItem);
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi api() {
        return GroupedOpenApi.builder()
                .group("API")
                .pathsToMatch("/v1/**", "/v2/**")
                .packagesToScan(SCAN_PACKAGE)
                .addOperationCustomizer((operation, handlerMethod) -> {
                    globalParameter().forEach(operation::addParametersItem);
                    return operation;
                })
                .build();
    }

    @Bean
    public GroupedOpenApi rpcApi() {
        return GroupedOpenApi.builder()
                .group("RPC-API")
                .pathsToMatch("/rpc/**")
                .packagesToScan(SCAN_PACKAGE)
                .addOperationCustomizer((operation, handlerMethod) -> {
                    globalParameter().forEach(operation::addParametersItem);
                    return operation;
                })
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    @Lazy(false)
    @Primary
    PolyParmamApiResource springWebProvider() {
        return new PolyParmamApiResource();
    }

    public List<Parameter> globalParameter() {
        List<Parameter> globalParameter = new ArrayList<>();
        globalParameter.add(new Parameter()
                .in(ParameterIn.HEADER.toString())
                .required(true)
                //设置多个默认值
                .schema(new StringSchema()._enum(Arrays.asList("a12", "app1"))._default("a12"))
                .description("鉴权参数")
                .name(authOriginConfiguration.getOrigin()));
        globalParameter.add(new Parameter()
                .in(ParameterIn.HEADER.toString())
                .required(true)
                .schema(new StringSchema()._default("a"))
                .description("认证头")
                .name(AUTHORIZATION));
        return globalParameter;
    }

}
