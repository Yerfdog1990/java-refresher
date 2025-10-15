package com.baeldung.lhj.extension;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.baeldung.lhj.persistence.util.JpaUtil;

public class CloseResourcesExtension implements AfterAllCallback {

    @Override
    public void afterAll(ExtensionContext context) {
        // Get a root store to manage shared state across all tests
        ExtensionContext.Namespace globalNamespace = ExtensionContext.Namespace.GLOBAL;
        ExtensionContext.Store rootStore = context.getRoot().getStore(globalNamespace);
        rootStore.getOrComputeIfAbsent(CloseResources.class);
    }

    private static class CloseResources implements ExtensionContext.Store.CloseableResource {
        @Override
        public void close() {
            System.out.println("Closing resources...");
            JpaUtil.closeEntityManagerFactory();
        }
    }
}