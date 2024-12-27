package com.example.properties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class CheckDataSourceProperties {

    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;
    private Integer maxPoolSize;

}
