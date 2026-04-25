package com.baeldung.lsso.config;

import org.keycloak.platform.PlatformProvider;
import org.keycloak.services.ServicesLogger;

public class SimplePlatformProvider implements PlatformProvider {

    Runnable shutdownHook;

    @Override
    public void onStartup(Runnable startupHook) {
        startupHook.run();
    }

    @Override
    public void onShutdown(Runnable shutdownHook) {
        this.shutdownHook = shutdownHook;
    }

    @Override
    public void exit(Throwable cause) {
        ServicesLogger.LOGGER.fatal(cause);
        exit();
    }

    @Override
    public ClassLoader getScriptEngineClassLoader(org.keycloak.Config.Scope scope) {
        return getClass().getClassLoader();
    }

    @Override
    public java.io.File getTmpDirectory() {
        return new java.io.File(System.getProperty("java.io.tmpdir"));
    }

    @Override
    public String name() {
        return "spring-boot";
    }

    private void exit() {
        new Thread() {
            @Override
            public void run() {
                System.exit(1);
            }
        }.start();
    }

}
