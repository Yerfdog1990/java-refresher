package oauth2.authserver.config.keycloak;

import javax.sql.DataSource;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.jboss.resteasy.plugins.server.servlet.ResteasyContextParameters;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.naming.*;
import javax.naming.spi.NamingManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = false)
@EnableConfigurationProperties(KeycloakServerProperties.class)
public class EmbeddedKeycloakConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    ServletRegistrationBean<?> keycloakJaxRsApplication(
            KeycloakServerProperties keycloakServerProperties, DataSource dataSource) throws Exception {

        mockJndiEnvironment(dataSource);
        EmbeddedKeycloakApplication.keycloakServerProperties = keycloakServerProperties;
        ServletRegistrationBean<HttpServletDispatcher> servlet = new ServletRegistrationBean<>(
                new HttpServletDispatcher());
        servlet.addInitParameter("jakarta.ws.rs.Application",
                EmbeddedKeycloakApplication.class.getName());
        servlet.addInitParameter("resteasy.role.based.security", "true");
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_SERVLET_MAPPING_PREFIX,
                keycloakServerProperties.getContextPath());
        servlet.addInitParameter(ResteasyContextParameters.RESTEASY_USE_CONTAINER_FORM_PARAMS,
                "true");
        servlet.addUrlMappings(keycloakServerProperties.getContextPath() + "/*");
        servlet.setLoadOnStartup(1);
        servlet.setAsyncSupported(true);
        return servlet;
    }

    @Bean
    FilterRegistrationBean<EmbeddedKeycloakRequestFilter> keycloakSessionManagement(
            KeycloakServerProperties keycloakServerProperties) {
        FilterRegistrationBean<EmbeddedKeycloakRequestFilter> filter = new FilterRegistrationBean<>();
        filter.setName("Keycloak Session Management");
        filter.setFilter(new EmbeddedKeycloakRequestFilter());
        filter.addUrlPatterns(keycloakServerProperties.getContextPath() + "/*");

        return filter;
    }

    private void mockJndiEnvironment(DataSource dataSource) throws NamingException {
        NamingManager.setInitialContextFactoryBuilder(
                (env) -> (environment) -> new InitialContext() {
                    @Override
                    public Object lookup(Name name) {
                        return lookup(name.toString());
                    }

                    @Override
                    public Object lookup(String name) {
                        if ("spring/datasource".equals(name)) {
                            return dataSource;
                        } else if (name.startsWith("java:jboss/ee/concurrency/executor/")) {
                            return fixedThreadPool();
                        }
                        return null;
                    }

                    @Override
                    public NameParser getNameParser(String name) {
                        return CompositeName::new;
                    }

                    @Override
                    public void close() {
                    }
                });
    }

    @Bean("fixedThreadPool")
    public ExecutorService fixedThreadPool() {
        return Executors.newFixedThreadPool(5);
    }
}
