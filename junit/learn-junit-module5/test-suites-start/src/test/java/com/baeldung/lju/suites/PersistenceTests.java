package com.baeldung.lju.suites;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@DisplayName("Persistence Tests")
@SelectPackages("com.baeldung.lju.persistence")
public class PersistenceTests {
    /*
    2. Test Suites
    JUnit Platform Suite is a feature in JUnit 5 that allows us to create and execute test suites using annotations like @Suite, @SelectClasses, and @SelectPackages.

    A test suite is a group of tests that we want to run together as one.
    Similar to groping tests via tags, we can execute all the tests in a suite instead of calling each test class or method individually, which can be useful in various situations like:
    Grouping tests by feature or domain area
    Grouping tests by their scope and execution time, such as “integration” or “fast” tests only
    Generating aggregated reports for multiple classes

    2.1. Required Dependencies
    In order to create a test suite, we first need to add the junit-platform-suite-api and junit-platform-suite-engine dependencies to our pom.xml. Alternatively, we can import the junit-platform-suite module, aggregating both of these dependencies. We can omit to specify the version, and rely on the JUnit BOM and Maven’s dependency management mechanism to resolve it for us:

    <dependency>
        <groupId>org.junit.platform</groupId>
        <artifactId>junit-platform-suite</artifactId>
        <scope>test</scope>
    </dependency>

    2.2. Creating a Test Suite
    We can now use JUnit’s Test Suites to group and run related tests together.
    All we need to do is create a Java class, annotate it with @Suite, and specify which tests to include.

    For example, if we want to create a test suite for all tests in the com.baeldung.lju.persistence package, we can use the @SelectPackage annotation to define the suite.
    Let’s create it as a new class in the com.baledung.lju.suites package from src/test/java:

    @Suite
    @SelectPackages("com.baeldung.lju.persistence")
    class PersistenceTests {
    }

    Like with test classes, we can annotate our suite class with JUnit’s @DisplayName.
    This enables us to use a more expressive name for better reporting:
    @Suite
    @DisplayName("Persistence Tests")
    @SelectPackages("com.baeldung.lju.persistence")
    class PersistenceTests {
    }

    2.3. Executing a Test Suite
    That’s it! We can now run our test suite from the IDE or Maven, just like any other test class:

    mvn -Dtest=PersistenceTests test
     */

    /*
    3. Selection Mechanisms
    The test suites API offers a handful of advanced annotations for selecting the exact tests we want to add to our suite. As a result, we can select the test based on the name of their package, class, method, or tag.

    Apart from @SelectPackage, we can use the @SelectClasses annotation to include specific classes in our suite. Let’s create a IntegrationTests suite and leverage this annotation to select the ApplicationIntegrationTest class:

    @Suite
    @SelectClasses(names = { "com.baeldung.lju.ApplicationIntegrationTest" })
    class IntegrationTests {
    }

    As we can see, our ApplicationIntegrationTest contains a single test method, named mainMethodIntegrationTests(). So, instead of selecting the whole class, we can leverage the @SelectMethod annotation and scan only this specific test method:

    @Suite
    @SelectMethod(typeName = "com.baeldung.lju.ApplicationIntegrationTest", name = "mainAppMethodIntegrationTest")
    class MainMethodTest {
    }

    Furthermore, we can use more advanced selectors such as:
        -@SelectClasspathResource for tests from a specified package or resource in the classpath
        -@SelectModule for tests from a specific Java module
        -@SelectFile and @SelectDirectory which enable our test engine to discover tests or containers based on files in the file system
        -custom selectors for our own special needs
    To dive deeper into test discovery selectors, refer to JUnit’s official user guide.
    Link: https://docs.junit.org/current/user-guide/#running-tests-discovery-selectors

    4. Exclusions and Inclusions
    So far, we’ve learned how to select all tests from a class, package, or module. However, sometimes we need more precision – in such cases, we can use inclusion and exclusion filters to refine our test selection.

    4.1. Exclusions
    Let’s create a new test suite and use @SelectPackages to select everything from our root package com.baeldung.lju:

    @Suite
    @SelectPackages("com.baeldung.lju")
    class AllTests {
    }
    Copy
    If we execute this, we’ll notice an interesting behavior. This won’t execute only our integration and persistence tests, but our other test suites too! Let’s tun “mvn -Dtest=AllTests test” and observe this:

    [INFO] Results:
    [INFO]
    [INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
    Copy
    Since we created all our test suites in a dedicated package – we can use @ExcludePackage to filter them out:

    @Suite
    @SelectPackages("com.baeldung.lju")
    @ExcludePackages("com.baeldung.lju.suites")
    class AllTests {
    }
    Copy
    Needless to say, if we re-run the test suite now, we can expect only our five tests to be executed.

    Similarly, we can exclude tests based on their class name and tags, via annotations like @ExcludeClassNamePatterns or @ExcludeTags.

    4.2. Inclusions
    Now, let’s assume we want to create a new test suite and only include some specific, fast, unit tests. As we have seen in the previous example, we can do this using the @SelectMethod annotation and provide an array with the exact methods we want to execute:

    @Suite
    @SelectMethods({
        @SelectMethod(
            typeName = "com.baeldung.lju.persistence.repository.impl.InMemoryCampaignRepositoryUnitTest",
            name = "givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved"),
        @SelectMethod(
            typeName = "com.baeldung.lju.persistence.repository.impl.InMemoryCampaignRepositoryUnitTest",
            name = "givenExistingCampaign_whenFindById_thenCampaignRetrieved")
    })
    class FastTests {
    }
    Copy
    However, this solution is hard to read and understand. Also, we’ll need to change the test suite whenever we add a new “fast” test, or if we rename the existing ones.

    Instead, a more elegant solution would be to filter tests by a custom tag, leveraging @IncludeTags. First, let’s annotate our tests with @Tag(“fast”):

    @Test
    @Tag("fast")
    void givenEmptyDataSource_whenFindAllCampaigns_thenEmptyListRetrieved() {
        // ...
    }

    @Test
    @Tag("fast")
    void givenExistingCampaign_whenFindById_thenCampaignRetrieved() {
        // ...
    }

    Then, we’ll update our suite to scan the root package, exclude the other suites, and then include the tests tagged as “fast“:
    @Suite
    @SelectPackages("com.baeldung.lju")
    @ExcludePackages("com.baeldung.lju.suites")
    @IncludeTags("fast")
    class FastTests {
    }

    In other words, we select all the tests from the com.baeldung.lju package, excluding the other suites.
    After that, we use @IncludeTags to filter the remaining tests and only keep the ones tagged as “fast“.
    This reads: “Find all the fast tests in com.baeldung.lju, excluding com.baeldung.lji.suites“.

    Needless to say, the API is consistent and offers the @IncludeClassamePatterns and @IncludePackages annotations for including specific classes and packages.

    As we can see, the selection mechanism enables us to scan a large number of test methods, classes, packages, or even modules.
    After that, the inclusions and exclusions help us refine the selection and keep only the tests relevant to that particular suite.
     */
}
