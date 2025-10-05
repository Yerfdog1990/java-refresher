package com.baeldung.lju.persistence.repository.impl;


import com.baeldung.lju.domain.model.Campaign;
import com.baeldung.lju.domain.model.Task;
import com.baeldung.lju.domain.model.TaskStatus;
import com.baeldung.lju.service.reports.SimpleMapReportBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static java.util.Collections.singletonList;

/*
    1. The @Tag Annotation
    JUnit’s @Tag annotation helps us categorize and filter tests.
    Instead of manually sorting test cases, we can assign tags and run specific groups when needed.
    We can label tests based on various criteria, such as their type, the technical layer they belong to, or the features they cover.

    For example, if we look at our InMemoryCampaignRepositoryUnitTest class, we’ll notice it’s testing the persistence layer for Campaign entities.
    The @Tag annotation allows us to highlight this easily:

    @Tag("persistence")
    @Tag("campaigns")
    class InMemoryCampaignRepositoryUnitTest {
        // ...
    }

    Now, let’s create another test class and verify the correct functionality of SimpleMapReportBuilder.
    This time, we’ll use the tags to highlight that we’re checking the “reports” use case and testing the “service” layer:
 */
public class SimpleMapReportBuilderUnitTest {
    @Test
    @Tag("service")
    @Tag("reports")
    void givenSimpleReportBuilder_whenAddingCampaignData_thenTotalCampaignsCountIsCorrect() {
        SimpleMapReportBuilder reportBuilder = new SimpleMapReportBuilder();
        Campaign campaign = new Campaign("CODE-1", "Campaign Name", "Test campaign description");
        Task task = new Task("Task Name", "description", LocalDate.now(), campaign, TaskStatus.TO_DO, null);

        reportBuilder.addCampaignsData(singletonList(task));

        Map<String, Object> report = reportBuilder.obtainReport();
        Assertions.assertEquals(1L, report.get("Total Campaigns Count"));
    }
    /*
    As we can see, we can use @Tag at the class level and it applies to all test methods of the class.
    However, we can also use it at a more granular level for individual test methods.
     */

    /*
    3. Tags and the Maven Surefire Plugin
    So far, we’ve seen how tags can help us organize test cases better.
    However, they also allow us to target a specific technical layer or business area for more efficient and focused test execution.

    When running tests with the Maven Surefire Plugin, we can use the “groups” property to filter tests by tag.
    Let’s run only the tests for the “campaigns” business area:

    mvn test -Dgroups=campaigns

    If we check the console, we’ll notice that the four tests from the @Tag(“campaigns“) annotated class are executed:

    [INFO] Results:
    [INFO]
    [INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
    Copy
    Additionally, we can use logical operators like and (&), or (|), and not (!) to define an expression for filtering tests.
    Let’s use the or (|) operator to include report-related tests in our previous test execution:

    mvn test -Dgroups=campiagns|reports

    As a result, we expect five tests to be executed this time:

    [INFO] Results:
    [INFO]
    [INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0

    Similarly, we can filter out a group of tests if we use the excludeGroups property instead:

    mvn test -DexcludedGroups=persistence

    Naturally, we can define these in the pom.xml plugin configurations as well:

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>${maven-surefire.version}</version>
                <configuration>
                    <groups>campaigns|reports</groups>
                    <excludedGroups>integration|persistence</excludedGroups>
                </configuration>
            </plugin>
        </plugins>
    </build>

    As we can see, JUnit’s tagging feature provides a convenient way to define which tests to execute,
    while the logical operators offer more flexibility when executing maven commands.
     */
}
