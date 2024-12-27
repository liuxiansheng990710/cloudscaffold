package com.example;

import java.util.Objects;

import org.springframework.core.io.ClassPathResource;

import com.example.checke.support.CheckHandler;
import com.example.checke.support.MySQLCheckActuator;
import com.example.commons.core.enums.ServerEnvironment;
import com.example.commons.core.utils.StringUtils;
import com.example.exception.CheckDataBaseException;
import com.example.properties.CheckDataProperties;
import com.example.utils.AnalysisYamlUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Lists;

public class DataBaseCheckApplication {

    public static void main(String[] args) throws JsonProcessingException {
        if (Objects.equals(System.getProperty("check.enable"), "false")) {
            return;
        }
        String checkEnv = System.getProperty("check.env");
        String checkModule = System.getProperty("module.name");
        if (StringUtils.isBlank(checkEnv) || ServerEnvironment.getEnvironment(checkEnv).isUnknown() || StringUtils.isBlank(checkModule)) {
            throw new CheckDataBaseException(String.format("需检测的环境:[%s]与服务:[%s]错误", checkEnv, checkModule));
        }
        AnalysisYamlUtils.setResources(new ClassPathResource("application.yml"), new ClassPathResource("application-" + checkEnv + ".yml"));
        CheckDataProperties dataProperties = AnalysisYamlUtils.analysisCheckDataProperties();
        Lists.newArrayList(new MySQLCheckActuator(dataProperties.getCheckedEntities(), dataProperties.getDatasource()))
                .stream().peek(CheckHandler::init)
                .forEach(CheckHandler::check);
    }

}
