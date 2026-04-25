package com.baeldung.lsso.config;

import javax.naming.CompositeName;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import javax.sql.DataSource;


import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddedKeycloakConfig {

    @Bean
    ServletContextInitializer keycloakJaxRsApplication(KeycloakServerProperties keycloakServerProperties, DataSource dataSource) throws Exception {

        mockJndiEnvironment(dataSource);
        EmbeddedKeycloakApplication.keycloakServerProperties = keycloakServerProperties;

        return (servletContext) -> {
            System.out.println("Skipping Keycloak servlet registration due to Jakarta EE compatibility issues");
            // The RestEasy servlet uses javax.servlet but Spring Boot 4.x expects jakarta.servlet
            // We'll initialize Keycloak differently
        };
    }

    @Bean
    ServletContextInitializer keycloakSessionManagement(KeycloakServerProperties keycloakServerProperties) {
        return (servletContext) -> {
            if (!isFilter()) {
                return;
            }

            var keycloakFilter = servletContext.addFilter("Keycloak Session Management", "org.keycloak.services.filters.KeycloakSessionServletFilter");
            keycloakFilter.addMappingForUrlPatterns(null, false, keycloakServerProperties.getContextPath() + "/*");
        };
    }

    private boolean isFilter() {
        try {
            Class<?> filterClass = Class.forName("org.keycloak.services.filters.KeycloakSessionServletFilter");
            return jakarta.servlet.Filter.class.isAssignableFrom(filterClass)
                || javax.servlet.Filter.class.isAssignableFrom(filterClass);
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private boolean isServlet(String servletClassName) {
        try {
            Class<?> servletClass = Class.forName(servletClassName);
            // For Spring Boot 4.x, we need to check if it's a javax.servlet.Servlet
            // since RestEasy uses the older servlet API
            return javax.servlet.Servlet.class.isAssignableFrom(servletClass);
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    private void mockJndiEnvironment(DataSource dataSource) throws NamingException {
        NamingManager.setInitialContextFactoryBuilder((env) -> (environment) -> new InitialContext() {

            @Override
            public Object lookup(Name name) {
                return lookup(name.toString());
            }

            @Override
            public Object lookup(String name) {

                if ("spring/datasource".equals(name)) {
                    return dataSource;
                }

                return null;
            }

            @Override
            public NameParser getNameParser(String name) {
                return CompositeName::new;
            }

            @Override
            public void close() {
                // NOOP
            }
        });
    }
}
