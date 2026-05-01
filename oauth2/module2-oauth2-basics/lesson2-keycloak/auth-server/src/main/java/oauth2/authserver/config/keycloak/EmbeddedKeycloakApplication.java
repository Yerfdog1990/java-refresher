package oauth2.authserver.config.keycloak;

import org.keycloak.exportimport.ExportImportManager;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.services.managers.ApplianceBootstrap;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.services.resources.KeycloakApplication;
import org.keycloak.util.JsonSerialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.keycloak.Config;

@SuppressWarnings("rawtypes")
public class EmbeddedKeycloakApplication extends KeycloakApplication {
    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedKeycloakApplication.class);
    static KeycloakServerProperties keycloakServerProperties;

    protected void loadConfig() {
        // Handled in initAndStart
    }

    @Override
    protected ExportImportManager bootstrap(KeycloakSession session) {
        final ExportImportManager exportImportManager = super.bootstrap(session);
        // Use the same session so the master realm created by super.bootstrap()
        // is visible (it lives in this session's uncommitted transaction).
        createMasterRealmAdminUser(session);
        createBaeldungRealm(session);
        return exportImportManager;
    }

    private void createMasterRealmAdminUser(KeycloakSession session) {
        ApplianceBootstrap applianceBootstrap = new ApplianceBootstrap(session);
        KeycloakServerProperties.AdminUser admin = keycloakServerProperties.getAdminUser();
        try {
            applianceBootstrap.createMasterRealmUser(admin.getUsername(), admin.getPassword(), false);
        } catch (Exception ex) {
            LOG.warn("Couldn't create keycloak master admin user: {}", ex.getMessage());
        }
    }

    private void createBaeldungRealm(KeycloakSession session) {
        try {
            RealmManager manager = new RealmManager(session);
            Resource lessonRealmImportFile = new ClassPathResource(
                    keycloakServerProperties.getRealmImportFile());
            manager.importRealm(JsonSerialization.readValue(lessonRealmImportFile.getInputStream(),
                    RealmRepresentation.class));
        } catch (Exception ex) {
            LOG.warn("Failed to import Realm json file: {}", ex.getMessage());
        }
    }

    @Override
    public void exit(Throwable cause) {
        LOG.error("Fatal error occurred, exiting Keycloak application", cause);
    }

    @Override
    protected String getDataDir() {
        return new File(System.getProperty("java.io.tmpdir"), "keycloak-data").getAbsolutePath();
    }

    @Override
    protected void initKeycloakSessionFactory(KeycloakSessionFactory factory) {
        // Default implementation - can be customized if needed
    }

    @Override
    protected void createTemporaryAdmin(KeycloakSession session) {
        // Implementation for creating temporary admin user
        // This method is required by KeycloakApplication but may not be needed
        // for the embedded setup, so we provide an empty implementation
    }

    @Override
    protected void initAndStart() {
        try {
            java.util.Map<org.keycloak.common.Profile.Feature, Boolean> features = new java.util.HashMap<>();
            for (org.keycloak.common.Profile.Feature f : org.keycloak.common.Profile.Feature.values()) {
                features.put(f, true);
            }
            org.keycloak.common.Profile.init(org.keycloak.common.Profile.ProfileName.DEFAULT, features);
        } catch (Exception e) {
            LOG.warn("Failed to initialize Keycloak Profile", e);
        }

        // Set hostname system property for Keycloak 26.x
        System.setProperty("keycloak.hostname", "localhost");
        
        // Disable Liquibase lock checking to clear any existing locks
        System.setProperty("liquibase.databasechangeloglock.skip", "true");

        try {
            Resource configResource = new ClassPathResource("META-INF/keycloak-server.json");
            LOG.info("Loading Keycloak configuration from: {}", configResource.getURL());
            java.util.Map<String, Object> configMap = JsonSerialization.readValue(configResource.getInputStream(), java.util.Map.class);
            LOG.info("Loaded Keycloak configuration with {} SPI configurations", configMap.size());
            Config.init(new Config.ConfigProvider() {
                @Override
                public String getProvider(String spi) {
                    java.util.Map<String, Object> spiConfig = (java.util.Map<String, Object>) configMap.get(spi);
                    if (spiConfig != null) {
                        return resolveValue((String) spiConfig.get("provider"));
                    }
                    return null;
                }

                @Override
                public String getDefaultProvider(String spi) {
                    LOG.info("Getting default provider for SPI: {}", spi);
                    if ("hostname".equals(spi)) {
                        LOG.info("Returning null for hostname SPI to let Keycloak handle default");
                        return null;
                    }
                    String provider = getProvider(spi);
                    LOG.info("Returning provider '{}' for SPI: {}", provider, spi);
                    return provider;
                }

                @Override
                public Config.Scope scope(String... scope) {
                    java.util.Map<String, Object> current = configMap;
                    for (String s : scope) {
                        Object obj = current.get(s);
                        if (obj instanceof java.util.Map) {
                            current = (java.util.Map<String, Object>) obj;
                        } else {
                            return new Config.SystemPropertiesConfigProvider().scope(scope);
                        }
                    }
                    return createScope(current);
                }

                private Config.Scope createScope(java.util.Map<String, Object> map) {
                    return new Config.Scope() {
                        @Override
                        public String get(String key) {
                            return get(key, null);
                        }

                        @Override
                        public String get(String key, String defaultValue) {
                            Object value = map.get(key);
                            if (value == null) {
                                return defaultValue;
                            }
                            return resolveValue(value.toString());
                        }

                        @Override
                        public String[] getArray(String key) {
                            Object value = map.get(key);
                            if (value instanceof java.util.List) {
                                return ((java.util.List<?>) value).stream().map(Object::toString).toArray(String[]::new);
                            }
                            return null;
                        }

                        @Override
                        public Integer getInt(String key) {
                            return getInt(key, null);
                        }

                        @Override
                        public Integer getInt(String key, Integer defaultValue) {
                            String v = get(key);
                            return v != null ? Integer.parseInt(v) : defaultValue;
                        }

                        @Override
                        public Long getLong(String key) {
                            return getLong(key, null);
                        }

                        @Override
                        public Long getLong(String key, Long defaultValue) {
                            String v = get(key);
                            return v != null ? Long.parseLong(v) : defaultValue;
                        }

                        @Override
                        public Boolean getBoolean(String key) {
                            return getBoolean(key, null);
                        }

                        @Override
                        public Boolean getBoolean(String key, Boolean defaultValue) {
                            String v = get(key);
                            return v != null ? Boolean.parseBoolean(v) : defaultValue;
                        }

                        @Override
                        public java.util.Set<String> getPropertyNames() {
                            return map.keySet();
                        }

                        @Override
                        public Config.Scope scope(String... scope) {
                            java.util.Map<String, Object> current = map;
                            for (String s : scope) {
                                Object obj = current.get(s);
                                if (obj instanceof java.util.Map) {
                                    current = (java.util.Map<String, Object>) obj;
                                } else {
                                    return null;
                                }
                            }
                            return createScope(current);
                        }

                        @Override
                        public Config.Scope root() {
                            return createScope(configMap);
                        }
                    };
                }

                private String resolveValue(String value) {
                    if (value == null || !value.startsWith("${") || !value.endsWith("}")) {
                        return value;
                    }
                    String core = value.substring(2, value.length() - 1);
                    int colonIndex = core.indexOf(':');
                    String propertyName;
                    String defaultValue;
                    if (colonIndex != -1) {
                        propertyName = core.substring(0, colonIndex);
                        defaultValue = core.substring(colonIndex + 1);
                    } else {
                        propertyName = core;
                        defaultValue = null;
                    }
                    String resolved = System.getProperty(propertyName);
                    if (resolved == null) {
                        resolved = System.getenv(propertyName.toUpperCase().replace('.', '_'));
                    }
                    return resolved != null ? resolved : defaultValue;
                }
            });
        } catch (Exception e) {
            LOG.error("Failed to initialize Keycloak Config", e);
        }
        
        clearStaleLiquibaseLock();
        startup();
    }

    private void clearStaleLiquibaseLock() {
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("spring/datasource");
            try (Connection conn = ds.getConnection()) {
                conn.setAutoCommit(true);
                try (Statement stmt = conn.createStatement()) {
                    stmt.executeUpdate(
                        "UPDATE DATABASECHANGELOGLOCK SET LOCKED=FALSE, LOCKGRANTED=NULL, LOCKEDBY=NULL WHERE ID=1");
                    LOG.debug("Cleared stale Liquibase lock");
                } catch (SQLException ignored) {
                    // Table does not exist on first startup — safe to ignore
                }
            }
        } catch (Exception e) {
            LOG.debug("Could not clear Liquibase lock ({}), continuing startup", e.getMessage());
        }
    }

    @Override
    protected KeycloakSessionFactory createSessionFactory() {
        DefaultKeycloakSessionFactory factory = new DefaultKeycloakSessionFactory();
        factory.init();
        return factory;
    }

    private static class DefaultKeycloakSessionFactory extends org.keycloak.services.DefaultKeycloakSessionFactory {
        @Override
        public KeycloakSession create() {
            return new org.keycloak.services.DefaultKeycloakSession(this) {
                @Override
                protected org.keycloak.services.DefaultKeycloakContext createKeycloakContext(KeycloakSession session) {
                    return new org.keycloak.services.DefaultKeycloakContext(session) {
                        @Override
                        protected org.keycloak.http.HttpRequest createHttpRequest() {
                            return null;
                        }

                        @Override
                        protected org.keycloak.http.HttpResponse createHttpResponse() {
                            return null;
                        }
                    };
                }

                @Override
                public org.keycloak.models.RealmProvider realms() {
                    return getProvider(org.keycloak.models.RealmProvider.class);
                }

                @Override
                public org.keycloak.models.UserProvider users() {
                    return getProvider(org.keycloak.models.UserProvider.class);
                }
            };
        }
    }
}
