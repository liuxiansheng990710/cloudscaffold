package com.example.provider.base.properties;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = CustomNacosProperties.CUSTIOM_PROPERTIES)
public class CustomNacosProperties {

    public static final String CUSTIOM_PROPERTIES = "custom";

    private String version;

    private int type;

    private List<String> v;

}
