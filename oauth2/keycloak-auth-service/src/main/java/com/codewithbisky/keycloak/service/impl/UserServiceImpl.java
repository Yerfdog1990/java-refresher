package com.codewithbisky.keycloak.service.impl;

import com.codewithbisky.keycloak.model.NewUserRecord;
import com.codewithbisky.keycloak.service.UserService;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.core.Response;
import org.jspecify.annotations.NonNull;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Value("${app.keycloak.realm}")
    private String realm;
    private final Keycloak keycloak;


    public UserServiceImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }
    @Override
    public void createUser(NewUserRecord newUserRecord) {

        UserRepresentation userRepresentation = getUserRepresentation(newUserRecord);

        UsersResource usersResource = getUsersResource();

        try (Response response = usersResource.create(userRepresentation)) {

            log.info("Status Code " + response.getStatus());

            if (Objects.equals(201, response.getStatus())) {
                log.info("User created successfully");
                
                // Only proceed with email verification if user creation succeeded
                List<UserRepresentation> userRepresentations = usersResource.searchByUsername(newUserRecord.username(), true);
                if (!userRepresentations.isEmpty()) {
                    UserRepresentation userRepresentation1 = userRepresentations.getFirst();
                    
                    // Skip email verification for development or when email is not configured
                    try {
                        sendVerificationEmail(userRepresentation1.getId());
                    } catch (Exception e) {
                        log.warn("Email verification skipped - email not configured in Keycloak: {}", e.getMessage());
                    }
                }
            } else if (Objects.equals(409, response.getStatus())) {
                log.warn("User already exists: {}", newUserRecord.username());
                throw new RuntimeException("User already exists: " + newUserRecord.username());
            } else {
                log.error("Failed to create user. Status code: {}", response.getStatus());
                throw new RuntimeException("Failed to create user. Status code: " + response.getStatus());
            }
        }
    }

    private static @NonNull UserRepresentation getUserRepresentation(NewUserRecord newUserRecord) {
        UserRepresentation  userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(newUserRecord.firstName());
        userRepresentation.setLastName(newUserRecord.lastName());
        userRepresentation.setUsername(newUserRecord.username());
        userRepresentation.setEmail(newUserRecord.username());
        userRepresentation.setEmailVerified(false);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(newUserRecord.password());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        userRepresentation.setCredentials(List.of(credentialRepresentation));
        return userRepresentation;
    }

    @Override
    public void sendVerificationEmail(String userId) {

        try {
            UsersResource usersResource = getUsersResource();
            usersResource.get(userId).sendVerifyEmail();
            log.info("Verification email sent successfully for user: {}", userId);
        } catch (ForbiddenException e) {
            log.warn("Email verification failed - admin client permissions insufficient or email not configured: {}", e.getMessage());
            throw new RuntimeException("Email verification not available - email service not configured");
        } catch (InternalServerErrorException e) {
            log.warn("Email verification failed - email service not configured in Keycloak: {}", e.getMessage());
            throw new RuntimeException("Email verification not available - email service not configured");
        } catch (Exception e) {
            log.error("Unexpected error sending verification email: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }

    }

    @Override
    public void deleteUser(String userId) {
        UsersResource usersResource = getUsersResource();
        try (Response response = usersResource.delete(userId)) {
            log.info("Delete user status: " + response.getStatus());
            if (!Objects.equals(204, response.getStatus())) {
                throw new RuntimeException("Failed to delete user, status code: " + response.getStatus());
            }
        }
    }

    @Override
    public void forgotPassword(String username) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(username, true);
        UserRepresentation userRepresentation1 = userRepresentations.getFirst();
        UserResource userResource = usersResource.get(userRepresentation1.getId());
        userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"));
    }

    @Override
    public UserResource getUser(String userId) {
        UsersResource usersResource = getUsersResource();
        return usersResource.get(userId);
    }

    @Override
    public List<RoleRepresentation> getUserRoles(String userId) {
        return getUser(userId).roles().realmLevel().listAll();
    }


    @Override
    public List<GroupRepresentation> getUserGroups(String userId) {
        return getUser(userId).groups();
    }

    private UsersResource getUsersResource(){
        return keycloak.realm(realm).users();
    }

}
