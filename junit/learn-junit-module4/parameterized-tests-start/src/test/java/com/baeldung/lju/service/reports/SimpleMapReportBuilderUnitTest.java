package com.baeldung.lju.service.reports;

import com.baeldung.lju.domain.model.TaskStatus;
import com.baeldung.lju.domain.model.Worker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.junit.platform.commons.util.StringUtils;

import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class SimpleMapReportBuilderUnitTest {

    private SimpleMapReportBuilder reportBuilder;


    @BeforeEach
    void beforeEach() {
        reportBuilder = new SimpleMapReportBuilder();
    }

    @Test
    void givenWorkerWithFullName_whenBuildingReport_thenWorkerNameIsCorrect() {
        reportBuilder.addSpecificWorkerData(aWorkerWithName("John", "DOE"));

        Map<String, Object> report = reportBuilder.obtainReport();

        assertTrue(report.containsKey("Worker Name"));
    }

    static Worker aWorkerWithName(String firstName, String lastName) {
        return new Worker("john.doe@yahoo.com", firstName, lastName);
    }
    @Test
    void givenWorkerWithFullName_whenBuildingReport_thenWorkerNameIsPresent() {
        reportBuilder.addSpecificWorkerData(aWorkerWithName("John", "DOE"));

        Map<String, Object> report = reportBuilder.obtainReport();

        assertTrue(report.containsKey("Worker Name"));
    }

    @Test
    void givenWorkerWithHonorificName_whenBuildingReport_thenWorkerNameIsPresent() {
        reportBuilder.addSpecificWorkerData(aWorkerWithName("Dr. John", "DOE"));

        Map<String, Object> report = reportBuilder.obtainReport();

        assertTrue(report.containsKey("Worker Name"));
    }

    @Test
    void givenWorkerWithBlankFirstName_whenBuildingReport_thenWorkerNameIsPresent() {
        reportBuilder.addSpecificWorkerData(aWorkerWithName("John-William", "DOE"));

        Map<String, Object> report = reportBuilder.obtainReport();

        assertTrue(report.containsKey("Worker Name"));
    }
    /*
    Note: To maintain focus on the core topic of this lesson, we are using simplified validations.
    We’ll include this aspect in a more advanced example in a future section.
    Although the @BeforeEach block and the helper method for creating Worker instances reduce boilerplate code, there’s still a lot of duplication between the tests.
    In such cases, using a parameterized test approach helps minimize code duplication and highlights the different input parameters.
     */

    /*
    Parameterized Tests
    To write parameterized tests, we’ll need to add the junit-jupiter-params dependency to our pom.xml and specify a specific version for this artifact.
    However – since we configured the junit-bom for dependency management in the previous lessons – we can omit the <version> field and let the dependency management mechanism handle it:

    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-params</artifactId>
        <scope>test</scope>
    </dependency>

    Now, we can refactor our code and take advantage of JUnit 5’s @ParameterizedTest to simplify our test class.
    Apart from the @ParameterizedTest annotation, we’ll use @ValueSource annotation to define the various test arguments:
     */

    @ParameterizedTest
    @ValueSource(strings = { "John", "Dr. John", "John-William" })
    void whenBuildingReport_thenWorkerNameIsPresent(String firstName) {
        reportBuilder.addSpecificWorkerData(aWorkerWithName(firstName, "DOE"));

        Map<String, Object> report = reportBuilder.obtainReport();

        assertTrue(report.containsKey("Worker Name"));
    }
    /*
    When we run the tests, we’ll see three independent tests being executed, one for each value provided in the source:

    Additionally, we can use @ValueSource to provide arguments of primitive types and Java classes.
    Needless to say, if one of them fails, it won’t affect the outcome of the other test cases.
     */

    /*
    Sources of Arguments
    While the @ValueSource annotation is a convenient way to define parameters for our tests, it’s not very flexible.
    To define more complex parameters, we can choose from a variety of other sources that are better suited to different use cases.

    @NullSource and @EmptySource
    For specific use cases, we can use the @NullSource, @EmptySource, or a combination of both, @NullAndEmptySource.

    We expect our reportBuilder to handle both null and empty Strings correctly, so let’s leverage this annotation:
     */

    @ParameterizedTest
    @NullAndEmptySource
    void givenWorkerWithNullOrEmptyFirstName_whenBuildingReport_thenWorkerNameIsPresent(String firstName) {
        reportBuilder.addSpecificWorkerData(aWorkerWithName(firstName, "DOE"));

        Map<String, Object> report = reportBuilder.obtainReport();

        assertTrue(report.containsKey("Worker Name"));
    }
    /*
    @NullSource runs the test with a null reference, while @EmptySource provides an empty String, empty Collection,
    or empty Map – depending on the argument type required by the test.
     */

    /*
    @EnumSource
    The @EnumSource annotation allows us to run tests for all values of an enum.
    For example, we can use @EnumSource(TaskStatus.class) to assert that all the values of the TaskStatus enum have a non-blank label:
     */
    @ParameterizedTest
    @EnumSource(TaskStatus.class)
    void whenCheckingTaskStatuses_thenTheyAllHaveALabel(TaskStatus statuses) {
        assertFalse(StringUtils.isBlank(statuses.getLabel()));
    }
    /*
    @CsvSource and @CsvFileSource
    The @CsvSource annotation is especially helpful when a test method requires multiple parameters.
    For example, we can use it to supply an input parameter as well as the expected outcome of the test.

    Let’s leverage @CsvSource to provide pairs of values, defining the Worker's firstName and the expected full name to be added to the report:
     */
    @ParameterizedTest
    @CsvSource({
            "John,John DOE",
            "Dr. John,Dr. John DOE",
            "John-William,John-William DOE",
    })
    void givenWorkerFirstName_whenBuildingReport_thenWorkerNameIsCorrect(String firstName, String expectedFullName) {
        reportBuilder.addSpecificWorkerData(aWorkerWithName(firstName, "DOE"));

        Map<String, Object> report = reportBuilder.obtainReport();

        assertEquals(expectedFullName, report.get("Worker Name"));
    }
    /*
    Note: Here, we’re using parameterization not only for the input values but also for the expected outcomes in the assertions.

    Furthermore, we can also externalize the test input to a dedicated .csv file.
    Consequently, we’ll add a worker-names.csv file to the src/test/resources directory and use the @CsvFileSource annotation to reference this file containing the test parameters:
     */
    @ParameterizedTest
    @CsvFileSource(resources = "/worker-names.csv")
    void givenWorkerFirstNameFromCsv_whenBuildingReport_thenWorkerNameIsCorrect(String firstName, String expectedFullName) {
        reportBuilder.addSpecificWorkerData(aWorkerWithName(firstName, "DOE"));

        Map<String, Object> report = reportBuilder.obtainReport();

        assertEquals(expectedFullName, report.get("Worker Name"));
    }
    /*
    @MethodSource
    The value sources we’ve used so far let us define primitive test inputs declaratively through annotation metadata.
    In contrast, the @MethodSource annotation allows us to generate test parameters programmatically by referencing a separate static method.

    Moreover, with @MethodSource, we can supply more complex data types like Java objects.
    For example, we could have a test that accepts the entire Worker object and the full name we expect to find in the report:
     */
    @ParameterizedTest
    @MethodSource("customWorkersAndExpectedFullNames")
    void givenCustomWorker_whenBuildingReport_thenWorkerNameIsCorrect(Worker worker, String expectedFullName) {
        reportBuilder.addSpecificWorkerData(worker);

        Map<String, Object> report = reportBuilder.obtainReport();

        assertEquals(expectedFullName, report.get("Worker Name"));
    }
    /*
    Now, we need to create a static function named customWorkersAndExpectedFullNames() that returns a Stream of Arguments.
    JUnit’s Arguments object allows us to wrap multiple values that define the input for a test.
    In this case, it will be an instance of a Worker object and a String:
     */
    static Stream<Arguments> customWorkersAndExpectedFullNames() {
        return Stream.of(
                Arguments.of(aWorkerWithName("J.", "DOE"), "J. DOE"),
                Arguments.of(aWorkerWithName("John", "DOE"), "John DOE"),
                Arguments.of(aWorkerWithName("Dr. John", "DOE"), "Dr. John DOE"),
                Arguments.of(aWorkerWithName("John-William", "DOE"), "John-William DOE")
        );
    }
    /*
    Other Sources
    Other argument sources include @FieldSource, which works like @MethodSource but references a field instead of a method.

    Additionally, JUnit 5 provides a few extension points when it comes to parameterized tests.
    For instance, @ArgumentsSource enables us to provide a custom implementation of an ArgumentsProvider and generate test parameters this way.

    Lastly, we can create custom value sources by defining our own annotation that, in turn, is annotated with @ArgumentsSource.
    Follow our detailed article to dive deeper into this topic and study all the various value sources.
    Link: https://www.baeldung.com/parameterized-tests-junit-5
     */
}
