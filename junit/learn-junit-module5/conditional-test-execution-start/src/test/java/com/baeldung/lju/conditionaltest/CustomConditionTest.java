package com.baeldung.lju.conditionaltest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class CustomConditionTest {
    @Test
    @EnabledIf("isWeekend")
    void givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved_1() {
        // ...
    }
    static boolean isWeekend() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY;
    }
    @Test
    @EnabledIf("com.baeldung.lju.conditionaltest.IsWeekendUtility#isWeekend")
    void givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved_2() {
        // ...
    }
    @Test
    @EnabledOnWeekends
    void givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved_3() {
        // ...
    }
      /*
    4. Custom Conditions
    In addition to the generic conditions we already covered, JUnit allows us to use @EnableIf as an extension point of the API.

    Let’s assume that the givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved() test is slow, and we only want to run it during the weekends.
    In this case, we can leverage the @EnabledIf annotation and point it to a boolean method that verifies the current day of the week:

    @Test
    @EnabledIf("isWeekend")
    void givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved() {
        // ...
    }

    boolean isWeekend() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        return today == DayOfWeek.SATURDAY || today == DayOfWeek.SUNDAY;
    }

    We can also reference a static from a different class.
    For example, let’s refactor our code and move the isWeekend() method to a dedicated utility class from the com.baeldung.lju package:

    class IsWeekendUtility {
        static boolean isWeekend() {
            // ...
        }
    }

    In this case, we should update our condition and use the fully qualified class name:

    @Test
    @EnabledIf("com.baeldung.lju.IsWeekendUtility#isWeekend")
    void givenExistingCampaign_whenFindByNonExistingId_thenNoCampaignRetrieved() {
        // ...
    }

    Lastly, we can improve the readability by creating custom, composed annotations.

    Let’s create a @EnabledOnWeekends annotation that internally uses @EnabledIf.
    We can create the new annotation in the com.baeldung.lju package from the test/java folder:

    @Target({ ElementType.TYPE, ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    @EnabledIf("com.baeldung.lju.IsWeekendUtility#isWeekend")
    public @interface EnabledOnWeekends {
    }

    Our annotation delegates evaluation to the static method isWeekend() in com.baeldung.lju.IsWeekendUtility.
    This allows us to annotate test methods or classes with @EnabledOnWeekends, making our tests more expressive:

    @Test
    @EnabledOnWeekends
    void givenEmptyDataSource_whenSave_thenCampaignIsAssignedId() {
        // ...
    }

    5. The Assumptions API
    The Assumptions API in JUnit provides a way to skip tests at runtime if certain conditions are not met.
    Unlike conditional test execution, which prevents tests from starting based on predefined rules,
    assumptions are checked within the test method and cause the test to be skipped without marking it as failed.

    While we don’t cover the Assumptions API in detail in this lesson, it can be useful in scenarios where tests should only proceed under specific conditions.
    This topic is outside the scope of this lesson, but you can find more details in the JUnit user manual.
    Link: https://docs.junit.org/current/user-guide/#writing-tests-assumptions
     */
}
