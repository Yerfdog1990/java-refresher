package com.baeldung.ls.configuration;

import com.baeldung.ls.persistence.repository.impl.ProjectRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PersistenceConfig {

    @Bean
    public ProjectRepositoryImpl getProjectRepository() {
        return new ProjectRepositoryImpl();
    }
}
