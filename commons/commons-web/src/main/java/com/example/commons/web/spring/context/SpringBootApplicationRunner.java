package com.example.commons.web.spring.context;

import java.util.Objects;
import java.util.Properties;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.example.commons.core.utils.ApplicationContextRegister;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class SpringBootApplicationRunner implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) {
        AbstractApplicationContext applicationContext = (AbstractApplicationContext) ApplicationContextRegister.getApplicationContext();
        Environment environment = applicationContext.getEnvironment();
        String applicationName = Objects.requireNonNull(environment.getProperty("spring.application.name")).replace("provider-", "");
        String port = environment.getProperty("server.port");

        Properties props = System.getProperties();
        log.info("Operating System Name: {}", props.getProperty("os.name"));
        log.info("Operating System Version: {}", props.getProperty("os.version"));
        log.info("Java Run The Environment Version: {}", props.getProperty("java.version"));
        log.info("{} Start Complete With Port {}", applicationName, port);
        log.info("{} Start With Environment {}", props.get("server.name"), props.get("server.environment"));
        log.info("Let's get started!");

    }
}
