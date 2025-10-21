package com.baeldung.ls;

import com.baeldung.ls.persistence.repository.IProjectRepository;
import com.baeldung.ls.persistence.repository.impl.ProjectRepositoryImpl;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class LsAppConfig {
    @Bean
    //@Scope("singleton") -> Spring creates a single instance of a bean, and all requests for such a bean will return the same object, which will be cached.
    //@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)

    //@Scope("prototype") -> Spring will create a new instance every time a bean is requested
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public IProjectRepository singletonBean() {
        return new ProjectRepositoryImpl();
    }
}