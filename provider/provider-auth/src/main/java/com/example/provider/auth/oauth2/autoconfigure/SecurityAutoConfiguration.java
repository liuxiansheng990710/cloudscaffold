package com.example.provider.auth.oauth2.autoconfigure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityAutoConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        return security.exceptionHandling()
                .and()
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureForwardUrl("/login?error")
                        .permitAll())
                .authorizeRequests(authz -> authz
                        .antMatchers("/oauth/**", "/logout")
                        .authenticated()
                        .anyRequest()
                        .permitAll())
                .headers(headers -> headers
                        .frameOptions()
                        .disable())
                .csrf().disable().build();
    }

}
