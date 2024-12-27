package com.example.provider.base.controller.v1;

import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.base.entity.mysql.App;
import com.example.provider.api.auth.rpc.ApiRPCAuthTestClient;
import com.example.provider.base.service.MpTestService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/v1")
public class BaseMpTestRestController {

    @Autowired
    private MpTestService mpTestService;
    @Autowired
    private ApiRPCAuthTestClient authTestClient;

    @GetMapping("/mp-test/{id}")
    @Parameter(name = "name", description = "定时任务的ID", required = true, in = ParameterIn.QUERY, schema = @Schema(type = "String"))
    public App getApp(@PathVariable("id") Long id, @RequestParam("name") String name, @ParameterObject App app) {
        mpTestService.update(id);
        return mpTestService.getById(id);
    }

}
