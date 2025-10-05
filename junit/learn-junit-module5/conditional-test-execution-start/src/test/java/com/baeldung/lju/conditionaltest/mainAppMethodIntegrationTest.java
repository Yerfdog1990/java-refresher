package com.baeldung.lju.conditionaltest;

import com.baeldung.lju.LjuApp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

public class mainAppMethodIntegrationTest {

    /*
    2. Conditional Test Execution
    The Conditional Test Execution feature allows us to enable or disable tests based on specific conditions at runtime.
    JUnit provides a set of annotations for common conditions. This is useful for running tests only in certain environments, on specific OS types or certain Java versions, or under custom conditions.

    One of the simplest forms of conditional test execution is disabling tests. The @Disabled condition is always evaluated as true and deactivates the test class or method.
    For example, we might temporarily disable a new integration test until the full test case is ready.

    Conditions can be combined to create more complex test rules, allowing tests to run only when all specified conditions are met.
    Conditions also support a disabled reason, which helps document why a test is skipped and makes it easier to understand.

    3. Built-in Conditions
    Unlike @Disabled, which always disables a test, other JUnit conditions are evaluated dynamically.
    Let’s explore some of the commonly used conditions exposed by the JUnit API.

    3.1. OS-Specific Conditions
    We can enable or disable tests based on the operating system using annotations like @EnabledOnOs and @DisabledOnOs.

    Let’s assume we want to update our integration test from the class ApplicationIntegrationTest.
    For example, let’s say we want to pass different arguments to our main method based on the OS.

    We can avoid adding logic inside the test by duplicating the test method and using JUnit’s OS-specific annotations to invoke it dynamically.
    This keeps the implementation clean and avoids conditional statements within the test itself:

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void mainAppMethodIntegrationTestOnWindows() {
        LjuApp.main(new String[] { "OS_WINDOWS_TEST" });
    }

    @Test
    @EnabledOnOs(OS.LINUX)
    void mainAppMethodIntegrationTestOnLinux() {
        LjuApp.main(new String[] { "OS_LINUX_TEST" });
    }

    Apart from passing the operating system using the org.junit.jupiter.condition.OS enum the annotation also allows us to define specific architecture via a different property:

    @Test
    @EnabledOnOs(value = OS.LINUX, architectures = "x86_64")
    void mainAppMethodIntegrationTestOnLinux() {
       // ...
    }
     */
    @Test
    @EnabledOnOs(value = OS.MAC, architectures = "x86_64")
    void mainAppMethodIntegrationTestOnMacOs() {
        LjuApp.main(new String[] { "OS_MACOS_TEST" });
    }

    @Test
    @EnabledOnOs(OS.WINDOWS)
    void mainAppMethodIntegrationTestOnWindows() {
        LjuApp.main(new String[] { "OS_WINDOWS_TEST" });
    }

    @Test
    @EnabledOnOs(value=OS.LINUX, architectures = "x86_64")
    void mainAppMethodIntegrationTestOnLinux() {
        LjuApp.main(new String[] { "OS_LINUX_TEST" });
    }

}
