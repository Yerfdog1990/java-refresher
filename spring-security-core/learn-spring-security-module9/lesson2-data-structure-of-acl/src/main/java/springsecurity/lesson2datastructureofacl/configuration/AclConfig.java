package springsecurity.lesson2datastructureofacl.configuration;

import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.acls.domain.*;
import org.springframework.security.acls.jdbc.BasicLookupStrategy;
import org.springframework.security.acls.jdbc.JdbcMutableAclService;
import org.springframework.security.acls.jdbc.LookupStrategy;
import org.springframework.security.acls.model.AclCache;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import springsecurity.lesson2datastructureofacl.security.CustomAclPermissionEvaluator;

import javax.sql.DataSource;

@Configuration
@EnableMethodSecurity
public class AclConfig {

    @Bean
    MethodSecurityExpressionHandler expressionHandler(
            CustomAclPermissionEvaluator evaluator) {

        DefaultMethodSecurityExpressionHandler h = new DefaultMethodSecurityExpressionHandler();
        h.setPermissionEvaluator(evaluator);
        return h;
    }

    @Bean
    AclAuthorizationStrategy aclAuthorizationStrategy() {
        return new AclAuthorizationStrategyImpl(
                new SimpleGrantedAuthority("ADMIN"));
    }

    @Bean
    PermissionGrantingStrategy permissionGrantingStrategy() {
        return new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger());
    }

    @Bean
    AclCache aclCache(PermissionGrantingStrategy pgs, AclAuthorizationStrategy aas) {
        return new SpringCacheBasedAclCache(
                new ConcurrentMapCache("aclCache"), pgs, aas);
    }

    @Bean
    LookupStrategy lookupStrategy(DataSource ds, AclCache cache, AclAuthorizationStrategy aas, PermissionGrantingStrategy pgs) {
        return new BasicLookupStrategy(ds, cache, aas, pgs);
    }

    @Bean
    MutableAclService aclService(DataSource ds, LookupStrategy ls, AclCache cache) {
        return new JdbcMutableAclService(ds, ls, cache);
    }
}
