package com.baeldung.lju.conditionaltest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;

@EnabledOnJre(JRE.JAVA_21)
public class ApplicationIntegrationTest {
    /*
    3.2. JRE-Specific Conditions
    Similarly, we can use @EnabledOnJre or @EnabledForJreRange to run tests only on specific Java versions or within a defined range and prevent execution on unsupported versions.

    We can apply all conditions at both the method and class levels.
    In our case, we can use a class-level annotation to ensure that all our integration tests are only executed when running on Java 21:

    @EnabledOnJre(JRE.JAVA_21)
    class ApplicationIntegrationTest {
        // ...
    }

    Needless to say, the API supports similar functionality for disabling tests on specific JRE versions via annotations like @DisabledOnJre and @DisableForJreRange.

    3.3. System and Environment-Related Conditions
    We can enable or disable tests based on specific system or environment properties.

    Let’s turn our focus to the test from the InMemoryCampaignRepositoryUnitTest class and pretend we want to run a test only if the environment variable “MY_ENV_VARIABLE” is set to “test”.
    In this case, we can leverage the @EnabledIfEnvironmentVariable annotation to activate the test when this variable matches our expectation:

    @Test
    @EnabledIfEnvironmentVariable(named = "MY_ENV_VARIABLE", matches = "test")
    void givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved() {
        // ...
    }

    On the other hand, we can use a similar API for evaluating System properties via @EnableIfSystemProperty.
    Same as for the previous examples, the API offers equivalent annotations for disabling tests, namely @DisableIfEnvironmentVariable and @DisableIfSystemProperty.

    Let’s use @DisableIfSystemProperty to disable the test givenExistingCampaign_whenFindById_thenCampaignRetrieved() while the system property “my.system.property” is set to “test“:

    @Test
    @DisabledIfSystemProperty(named = "my.system.property", matches = "test")
    void givenExistingCampaign_whenFindById_thenCampaignRetrieved() {
        // ...
    }

    If we execute “mvn test” now, we’ll notice that the test is executed.
    However, if we set “my.system.property” to “test“, we can expect the test to be skipped:

    mvn test -Dmy.system.property=test
     */

    @Test
    @EnabledIfEnvironmentVariable(named = "MY_ENV_VARIABLE", matches = "test")
    void givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved() {
        // ...
    }
    @Test
    @DisabledIfSystemProperty(named = "my.system.property", matches = "test")
    void givenExistingCampaign_whenFindById_thenCampaignRetrieved() {
        // ...
    }
}
