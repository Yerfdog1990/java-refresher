package oauth2.authserver.config.keycloak;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.dblock.DBLockProvider;
import org.keycloak.models.dblock.DBLockProviderFactory;

public class NoneDBLockProviderFactory implements DBLockProviderFactory {

    @Override
    public DBLockProvider create(KeycloakSession session) {
        return new NoneDBLockProvider();
    }

    @Override
    public void init(Config.Scope config) {}

    @Override
    public void postInit(KeycloakSessionFactory factory) {}

    @Override
    public void close() {}

    @Override
    public String getId() {
        return "none";
    }

    @Override
    public void setTimeouts(long lockRecheckTimeMillis, long lockWaitTimeoutMillis) {}
}
