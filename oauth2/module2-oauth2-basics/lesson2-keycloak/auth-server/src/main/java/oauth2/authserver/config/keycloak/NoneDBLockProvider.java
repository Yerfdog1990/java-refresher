package oauth2.authserver.config.keycloak;

import org.keycloak.models.dblock.DBLockProvider;

public class NoneDBLockProvider implements DBLockProvider {

    @Override
    public void waitForLock(Namespace lock) {}

    @Override
    public void releaseLock() {}

    @Override
    public Namespace getCurrentLock() {
        return null;
    }

    @Override
    public boolean supportsForcedUnlock() {
        return false;
    }

    @Override
    public void destroyLockInfo() {}

    @Override
    public void close() {}
}
