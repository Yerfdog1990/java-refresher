package com.codewithbisky.keycloak.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class JwtAuthConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();


    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        System.out.println("=== JWT DEBUG ===");
        System.out.println("JWT Subject: " + jwt.getSubject());
        System.out.println("Realm Access: " + jwt.getClaim("realm_access"));
        System.out.println("Resource Access: " + jwt.getClaim("resource_access"));
        
        Collection<GrantedAuthority> authorities = Stream.concat(
                jwtGrantedAuthoritiesConverter.convert(jwt).stream(),
                extractResourceRoles(jwt).stream()).collect(Collectors.toSet());
        
        System.out.println("Extracted Authorities: " + authorities);
        System.out.println("=== END JWT DEBUG ===");
        
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalClaimName(jwt));
    }

    private String getPrincipalClaimName(Jwt jwt) {
        String claimName = JwtClaimNames.SUB;
        return jwt.getClaim(claimName);
    }

    private Collection<? extends GrantedAuthority> extractResourceRoles(Jwt jwt) {

        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");

        Collection<String> allRoles = new ArrayList<>();
        Collection<String> resourceRoles;

        if(resourceAccess != null && resourceAccess.get("account") != null){
            Object accountObj = resourceAccess.get("account");
            if(accountObj instanceof Map){
                @SuppressWarnings("unchecked")
                Map<String,Object> account = (Map<String,Object>) accountObj;
                if(account.containsKey("roles") ){
                    Object rolesObj = account.get("roles");
                    if(rolesObj instanceof Collection){
                        @SuppressWarnings("unchecked")
                        Collection<String> roles = (Collection<String>) rolesObj;
                        allRoles.addAll(roles);
                    }
                }
            }
        }

        if(realmAccess != null && realmAccess.containsKey("roles")){
            Object rolesObj = realmAccess.get("roles");
            if(rolesObj instanceof Collection){
                @SuppressWarnings("unchecked")
                Collection<String> realmRoles = (Collection<String>) rolesObj;
                allRoles.addAll(realmRoles);
            }
        }


        return allRoles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .collect(Collectors.toSet());
    }
}
