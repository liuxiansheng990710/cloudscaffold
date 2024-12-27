package com.example.provider.base.controller.v1;

import java.util.List;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.example.cloud.provider.openapi3.annotaions.PolyParam;
import com.example.cloud.provider.openapi3.annotaions.PolyParams;
import com.example.commons.core.model.ApiResponses;
import com.example.commons.web.controller.SuperController;
import com.example.provider.base.properties.CustomNacosProperties;
import com.example.provider.base.servies.BaseTestServices;
import com.google.common.collect.Lists;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/v1")
public class BaseTestRestController extends SuperController {

    @Autowired
    private BaseTestServices baseTestServices;

    @Autowired
    private CustomNacosProperties customNacosProperties;

    @GetMapping("/base-test/{id}")
    @SentinelResource("parmBlock")
    public CustomNacosProperties get(@PathVariable("id") Integer id) {
        List<String> v = customNacosProperties.getV();
        int type = customNacosProperties.getType();
        CustomNacosProperties properties = new CustomNacosProperties();
        properties.setType(id);
        properties.setVersion(baseTestServices.sentienl());
        properties.setV(v);
        return properties;
    }

    @Operation(summary = "批量删除定时任务", description = "批量删除定时任务")
    @Parameter(name = "ids", description = "定时任务的ID集合(英文逗号隔开)", required = true, in = ParameterIn.PATH, schema = @Schema(type = "Long"))
    @DeleteMapping("/jobs/{ids}")
    public void delete(@PathVariable("ids") List<Long> ids) {
    }

    @GetMapping("/base-test-1")
//    @SentinelResource(value = "base-test", blockHandler = "sentienl", fallback = "sentienlFallBack")
    public ApiResponses<CustomNacosProperties> get2() {
        int a = 1 / 0;
        String version = customNacosProperties.getVersion();
        List<String> v = customNacosProperties.getV();
        int type = customNacosProperties.getType();
        CustomNacosProperties properties = new CustomNacosProperties();
        properties.setType(type);
        properties.setVersion(version);
        properties.setV(v);
        return success(properties);
    }

    @GetMapping("/base-test-add")
    public ApiResponses<CustomNacosProperties> add(String authOrigin) {
//        TimeUnit.SECONDS.sleep(2);
        CustomNacosProperties properties = new CustomNacosProperties();
        properties.setVersion(baseTestServices.sentienl());
        properties.setV(Lists.newArrayList(authOrigin));
        return success(properties);
    }

    @PostMapping("/test-audo")
    @PolyParams(resultType = Audo.class, paramType = Audo.class, value = {
            @PolyParam(paramType = Audo1.class, resultType = Audo1.class, type = "1"),
            @PolyParam(paramType = Audo2.class, resultType = Audo2.class, type = "2"),
    })
    public Audo audo(@RequestBody Audo audo) {
        return audo;
    }

    @GetMapping("/test-audo")
    @PolyParams(resultType = Audo.class, paramType = Audo.class, value = {
            @PolyParam(paramType = Audo1.class, resultType = Audo1.class, type = "1"),
            @PolyParam(paramType = Audo2.class, resultType = Audo2.class, type = "2"),
    })
    public Audo audu2(@ParameterObject Audo audo) {
        return audo;
    }
//
//    @PostMapping("/test-audo2")
//    @PolyParams(resultType = Audo.class, paramType = Audo.class, value = {
//            @PolyParam(paramType = Audo1.class, resultType = Audo1.class, type = "1"),
//            @PolyParam(paramType = Audo2.class, resultType = Audo2.class, type = "2"),
//    })
//    public Audo lege( @RequestBody Audo audo) {
//        return audo;
//    }
//
//    @GetMapping("/test-audo2")
//    @PolyParams(resultType = Audo.class, paramType = Audo.class, value = {
//            @PolyParam(paramType = Audo1.class, resultType = Audo1.class, type = "1"),
//            @PolyParam(paramType = Audo2.class, resultType = Audo2.class, type = "2"),
//    })
//    public Audo lege2(Audo audo) {
//        return audo;
//    }

    public ApiResponses<CustomNacosProperties> sentienl(BlockException e) {
        e.printStackTrace();
        CustomNacosProperties properties = new CustomNacosProperties();
        properties.setVersion("我是手动限流");
        return success(properties);
    }

    public ApiResponses<CustomNacosProperties> sentienlFallBack(Throwable throwable) {
        throwable.printStackTrace();
        CustomNacosProperties properties = new CustomNacosProperties();
        properties.setVersion("遇到错误了");
        return success(properties);
    }

}
