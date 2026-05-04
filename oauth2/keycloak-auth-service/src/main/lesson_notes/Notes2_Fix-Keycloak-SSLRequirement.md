# Fixing Keycloak SSL Requirement Issue

## Problem
Keycloak shows "HTTPS required" error when trying to access the admin console or when Spring Boot applications try to authenticate with Keycloak, even in development environments.

## Environment
- Keycloak version: 26.6.1
- Docker setup with PostgreSQL
- Development environment (non-production)

## Root Cause
Keycloak defaults to requiring HTTPS for security, but in development environments, we often need to run it on HTTP for easier testing and configuration.

## Failed Solutions
The following common solutions did NOT work for Keycloak 26.6.1:

### 1. Environment Variables (Failed)
```yaml
environment:
  KC_HTTP_ENABLED: "true"
  KC_SSL_REQUIRED: "none"
  KC_HTTPS_REQUIRED: "none"
```

These environment variables had no effect on the SSL requirement behavior.

### 2. Admin CLI Tool (Failed)
```bash
docker exec keycloak-container bash -c "cd /opt/keycloak/bin && ./kcadm.sh config credentials --server http://localhost:8080 --realm master --user admin --password admin"
```

This failed because the admin-cli client wasn't properly configured with the necessary permissions.

## Working Solution: Database Update

The most reliable solution is to update the database directly:

### Step 1: Update the REALM table
```bash
# Access PostgreSQL container and update the realm setting
docker exec <postgres-container-name> psql -U keycloak -d keycloak -c "UPDATE REALM SET ssl_required='NONE' where name = 'master';"
```

For our specific setup:
```bash
docker exec resources-postgres-1 psql -U keycloak -d keycloak -c "UPDATE REALM SET ssl_required='NONE' where name = 'master';"
```

### Step 2: Restart Keycloak
```bash
docker-compose restart keycloak
```

### Step 3: Verify Access
- Admin Console: `http://localhost:9082/admin/`
- Login: `admin` / `admin`

## Why This Works
- Keycloak stores SSL requirements in the `REALM` table in the database
- Direct database update bypasses configuration issues with newer versions
- Restarting Keycloak ensures the setting is loaded from the database
- This method works across all Keycloak versions

## Additional Configuration for Spring Boot Integration

### KeycloakConfig.java
```java
@Configuration
public class KeycloakConfig {
    
    @Bean
    public Keycloak keycloak(){
        return KeycloakBuilder.builder()
                .clientSecret(clientSecret)
                .clientId(clientId)
                .grantType("client_credentials")
                .realm("master")  // Use master realm for admin operations
                .serverUrl(serverUrl)
                .build();
    }
}
```

### Application Configuration
```yaml
app:
  keycloak:
    admin:
      clientId: admin-cli
      clientSecret: lK4vLqhhmXjq0V5PKfJOqlw0840o6hcH
    realm: master
    serverUrl: http://localhost:9082
```

## Docker Compose Configuration
```yaml
services:
  keycloak:
    image: quay.io/keycloak/keycloak:26.6.1
    command: start-dev
    ports:
      - "9082:8080"
    environment:
      KC_BOOTSTRAP_ADMIN_USERNAME: admin
      KC_BOOTSTRAP_ADMIN_PASSWORD: admin
      KC_DB: postgres
      KC_DB_USERNAME: keycloak
      KC_DB_PASSWORD: password
      KC_DB_URL: jdbc:postgresql://postgres:5432/keycloak
      KC_HOSTNAME: localhost
      KC_FEATURES: token-exchange,admin-fine-grained-authz
      # Note: SSL-related env vars don't work for 26.6.1
```

## Stack Overflow Answer Template

For those using Keycloak 26.6.1 with Docker and PostgreSQL:

```bash
# Direct database update (most reliable method)
docker exec <postgres-container> psql -U keycloak -d keycloak -c "UPDATE REALM SET ssl_required='NONE' where name = 'master';"

# Restart to apply changes
docker-compose restart keycloak
```

Access Keycloak at `http://localhost:9082/admin/` after restart.

## Production Considerations
- This solution is intended for development environments only
- For production, always use HTTPS with proper SSL certificates
- Consider using port 8443 with HTTPS in production deployments

## Troubleshooting
If issues persist:
1. Verify PostgreSQL container is running
2. Check database connection credentials
3. Ensure Keycloak fully restarts after database update
4. Verify the update was applied: `docker exec postgres psql -U keycloak -d keycloak -c "SELECT ssl_required FROM REALM WHERE name='master';"`