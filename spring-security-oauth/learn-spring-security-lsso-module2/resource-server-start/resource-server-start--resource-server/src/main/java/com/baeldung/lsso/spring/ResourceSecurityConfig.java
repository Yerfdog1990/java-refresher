package com.baeldung.lsso.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class ResourceSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http){
        http
            .authorizeHttpRequests(authorize -> authorize
                .anyRequest().authenticated()
            )
                .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> {
                        jwt.jwkSetUri("http://localhost:8080/auth/realms/baeldung/protocol/openid-connect/certs");
                    })
                );
        return http.build();
    }
}
