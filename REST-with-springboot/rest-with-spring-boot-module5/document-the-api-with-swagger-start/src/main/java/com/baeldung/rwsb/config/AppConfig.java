package com.baeldung.rwsb.config;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
          .info(new Info()
          .title("Baeldung API")
          .version("1.0")
          .description("Documentation for Baeldung REST with Spring Boot"));
    }
}
