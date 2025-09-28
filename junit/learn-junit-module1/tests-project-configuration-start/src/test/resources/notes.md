1. Overview
In this lesson, we’ll learn about the Maven Surefire Plugin and how to use it to automatically run our JUnit tests when building the application. We’ll discover that the Surefire plugin is included in the Maven project’s effective POM by default and is used during the test phase.
Finally, we’ll learn how to use Maven build commands to build the project without executing the tests, which will speed up the build process.
The relevant module we need to import when we’re starting with this lesson is: tests-project-configuration-start.
If we want to have a look at the fully implemented lesson as a reference, feel free to import: tests-project-configuration-end.

2. The Maven Surefire Plugin
Until now, we have been running our tests directly from the IDE. However, we can automate the process as part of our continuous integration pipelines.
For Maven projects, we can use the Maven Surefire Plugin to run unit tests. This plugin detects and executes JUnit tests before the project is packaged, ensuring that our code is tested and verified during the build process.
By default, the maven-surefire-plugin is automatically included in Maven’s effective POM as part of the test phase. If we want to explicitly include the plugin, we can add it to the <build> section of the pom.xml:

<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>${maven-surefire.version}</version>
    </plugin>
  </plugins>
</build>
Copy
We can now run our tests using the Maven Surefire Plugin by executing a single command:

mvn surefire:test
Copy
Moreover, Maven automatically links this goal to the test phase. So, simply running mvn test will also use the plugin to execute our tests:

mvn test

Another benefit of adding the plugin explicitly is that we can add additional configurations.

3. Test Execution
As we’ve seen, the mvn test command automatically finds and runs all project tests. Similarly, the tests are run during mvn package and mvn install because they’re part of the default build lifecycle, ensuring code quality before packaging or installing.
In other words, when we run mvn install, we should expect to see the tests being executed by the plugin:
mvn install
Copy
The Maven Surefire Plugin generates a summary of the executed tests and their results. Let’s review the test results, logged in the console output:

[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.baeldung.lju.ApplicationIntegrationTest
...
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.192 s -- in com.baeldung.lju.ApplicationIntegrationTest
[INFO] Running com.baeldung.lju.persistence.repository.impl.InMemoryCampaignRepositoryUnitTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.010 s -- in com.baeldung.lju.persistence.repository.impl.InMemoryCampaignRepositoryUnitTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
[INFO]
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
Copy
As we can see, the execution summary displays the total number of tests run for each test class. It also highlights any skipped or failed tests, along with their corresponding error messages.

On the other hand, if we want to build the project without executing the tests, we can use the skipTests command-line argument:

mvn install -DskipTests

4. Running Specific Tests
Sometimes, we may want a more precise way to choose which tests to run, such as tests with a specific name or those in a particular package or class.
We can use the test parameter to specify which test class we want to run:

mvn test -Dtest=InMemoryCampaignRepositoryUnitTest

What’s more, we can update the test property to narrow the test execution down to a single test method:mvn test -Dtest=InMemoryCampaignRepositoryUnitTest#givenExistingCampaign_whenFindById_thenCampaignRetrieved
In this case, only the specified method of the 4 test methods in the class will run.
Lastly, we can use the parameter to define more complex patterns based on class names. For example, the pattern *UnitTest can be used to run tests from classes whose names end with UnitTest:

mvn test -Dtest=*UnitTest

We can also define any of these custom test inclusions as the default configuration for the Surefire plugin.
Let’s update our pom.xml adding a <configuration> section for the Surefire plugin, and define the *UnitTest pattern:

<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>${maven-surefire.version}</version>
    <configuration>
        <includes>
            <include>*UnitTest.java</include>
        </includes>
    </configuration>
</plugin>

As a result, whenever we build the project with commands like mvn package or mvn install, the Surefire plugin will only execute tests from classes with the UnitTest suffix.