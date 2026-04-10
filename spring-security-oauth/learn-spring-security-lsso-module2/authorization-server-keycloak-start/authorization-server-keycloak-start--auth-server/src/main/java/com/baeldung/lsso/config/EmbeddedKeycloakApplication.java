package com.baeldung.lsso.config;

import java.util.NoSuchElementException;

import org.keycloak.Config;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.models.RealmModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.services.util.JsonConfigProviderFactory;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.baeldung.lsso.config.KeycloakServerProperties.AdminUser;

public class EmbeddedKeycloakApplication extends KeycloakApplication {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedKeycloakApplication.class);

    static KeycloakServerProperties keycloakServerProperties;

    protected void loadConfig() {
        JsonConfigProviderFactory factory = new RegularJsonConfigProviderFactory();
        Config.init(factory.create()
            .orElseThrow(() -> new NoSuchElementException("No value present")));
    }

    public EmbeddedKeycloakApplication() {
        super();
        KeycloakSessionFactory sessionFactory = getSessionFactory();
        if (sessionFactory != null) {
            KeycloakModelUtils.runJobInTransaction(sessionFactory, session -> {
                createMasterRealmAdminUser(session);
                createBaeldungRealm(session);
            });
        } else {
            LOG.warn("KeycloakSessionFactory is null in EmbeddedKeycloakApplication constructor. Master realm admin user and Baeldung realm might not be created.");
        }
    }

    private void createMasterRealmAdminUser(KeycloakSession session) {

        AdminUser admin = keycloakServerProperties.getAdminUser();

        try {
            RealmManager realmManager = new RealmManager(session);
            RealmModel masterRealm = session.realms().getRealmByName(Config.getAdminRealm());
            if (masterRealm == null) {
                masterRealm = realmManager.createRealm(Config.getAdminRealm());
                masterRealm.setName(Config.getAdminRealm());
                masterRealm.setDisplayName(Config.getAdminRealm());
                masterRealm.setDisplayNameHtml(Config.getAdminRealm());
                masterRealm.setEnabled(true);
            }
            session.getContext().setRealm(masterRealm);

            ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);
            LOG.info("Creating master realm user: {}", admin.getUsername());
            applianceBootstrap.createMasterRealmUser(admin.getUsername(), admin.getPassword());
        } catch (Exception ex) {
            LOG.warn("Couldn't create keycloak master admin user: {}", ex.getMessage());
        }
    }

    private void createBaeldungRealm(KeycloakSession session) {
        try {
            RealmManager manager = new RealmManager(session);
            Resource lessonRealmImportFile = new ClassPathResource(keycloakServerProperties.getRealmImportFile());
            RealmRepresentation realmRep = JsonSerialization.readValue(lessonRealmImportFile.getInputStream(), RealmRepresentation.class);

            if (manager.getRealmByName(realmRep.getRealm()) == null) {
                manager.importRealm(realmRep);
            }
        } catch (Exception ex) {
            LOG.warn("Failed to import Realm json file: {}", ex.getMessage());
        }
    }
}
