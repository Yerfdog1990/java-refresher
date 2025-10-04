package com.baeldung.lju;

import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StaticCodeAnalysisTest {
    /*
    Dynamic vs. Parameterized Tests
    JUnit 5’s dynamic tests let us create tests at runtime, making them useful for flexible scenarios like parameterized or data-driven tests. Instead of using @Test, we define them via methods annotated @TestFactory, which return an Iterable or Stream of DynamicTest objects.

    Each dynamic test from the Stream is executed and reported individually. They can pass or fail independently, similar to parameterized tests. However, they follow a different lifecycle: for example, methods annotated with @BeforeEach and @AfterEach run only once per @TestFactory method.

    Another key difference is that methods annotated with @TestFactory can return Java Streams, and the framework automatically closes the stream after the tests are executed. This is particularly useful when working with AutoCloseable objects such as files or connections.

    Due to their unique lifecycle and different API, dynamic tests aren’t commonly used to test business logic for specific use cases. For static collections of input-output pairs, parameterized tests are preferred.

    1.Dynamic Tests
    In this section’s examples, we’ll use the dynamic test API to create a simple static code analysis test that checks the length of source code files. Since these files are constantly added, removed, and updated, they serve as a dynamic test source.

    First, let’s create a com.baeldung.lju.StaticCodeAnalysisTest class and write a method that iterates through all Java files from our com.baeldung.lju.service package:
     */

    private static final int MAX_FILE_LENGTH = 300;

    @TestFactory
    Stream<DynamicTest> givenTheServicePackage_whenVerifyingFileLengths_thenTheyAreNotTooLong() {
        File root = new File("src/main/java", "com/baeldung/lju/service");
        return Stream.of(root.listFiles())
                .filter(file -> file.getName().endsWith(".java"))

                // To create DynamicTest instances, we can use the static factory method that takes the test name as a String and an Executable function to run assertions.
                // Let’s use this method to map() our Java files to DynamicTest objects:
                .map(javaFile -> verifyFileLength(javaFile));
    }

    static DynamicTest verifyFileLength(File javaFile) {
        return DynamicTest.dynamicTest(
                String.format("%s < %s lines", javaFile.getName(), MAX_FILE_LENGTH),
                () -> {
                    long fileLength = Files.lines(javaFile.toPath()).count();
                    assertTrue(fileLength < MAX_FILE_LENGTH);
                }
        );
        /*
        That’s it! We can now execute our test and expect it to verify all the Java files of the “service” package.
        If we change the source code by adding new files, we’ll validate them during the following test execution:
        Naturally, we can temporarily set MAX_FILE_LENGTH to a lower value, such as 20, to verify that our assertions are correctly identifying overly long files:
         */
    }

    /*
    Dynamic Containers
    Let’s enhance our dynamic test to scan through all nested packages and locate all Java files.
    To achieve this, we’ll extract a method and call it recursively, passing the parent folder as a parameter:
     */
    @TestFactory
    Stream<DynamicTest> givenSourceFiles_whenVerifyingAllFileLengths_thenTheyAreNotTooLong() {
        return verifyAllFromFolder(
                new File("src/main/java", "com/baeldung/lju"));
    }

    static Stream<DynamicTest> verifyAllFromFolder(File root) {
        if (root.listFiles() == null) {
            return Stream.empty();
        }
        return Stream.of(root.listFiles())
                .flatMap(file -> {
                    if (file.getName().endsWith(".java")) {
                        return Stream.of(verifyFileLength(file));
                    } else {
                        return Stream.of(file.listFiles())
                                .flatMap(StaticCodeAnalysisTest::verifyAllFromFolder);
                    }
                });
    }
    /*
    In addition to the DynamicTests, JUnit 5 can also process DynamicContainer objects, both of which extend the DynamicNode abstract class.
    The key difference is that a DynamicContainer allows us to logically group multiple DynamicNodes.
    Consequently, we can create a more expressive, nested test structure.

    Let’s update our code to leverage the DynamicContainer API. We’ll modify the method to return a Stream<DynamicNode>.
    Additionally, in the else branch, instead of streaming the files and mapping them with flatMap(), we’ll create and return a dynamic container:
     */
    @TestFactory
    Stream<DynamicNode> givenSourceFilesWithContainers_whenVerifyingAllFileLengths_thenTheyAreNotTooLong() {
        return verifyAllFromFolderWithContainers(
                new File("src/main/java", "com/baeldung/lju"));
    }

    static Stream<DynamicNode> verifyAllFromFolderWithContainers(File root) {
        if (root.listFiles() == null) {
            return Stream.empty();
        }
        return Stream.of(root.listFiles())
                .map(file -> {
                    if (file.getName().endsWith(".java")) {
                        return verifyFileLength(file);
                    } else {
                        return DynamicContainer.dynamicContainer(
                                file.getName(),
                                verifyAllFromFolderWithContainers(file));
                    }
                });
        /*
        When creating a dynamicContainer, we use the package name as the container’s name and recursively call the method to generate the dynamic tests included within it.
        Consequently, the structure of our dynamic test will mirror the package structure of our source code:
        To sum up, with this approach, our tests adapt naturally to new or updated source files.
        And by leveraging dynamic containers, we also ensure that related checks stay neatly grouped, which keeps our code organized and easier to navigate.
        While we’ve covered the basics of the dynamic tests API, you can explore the intricacies in more detail by referring to the official JUnit 5 documentation.
        Link: https://docs.junit.org/current/user-guide/#writing-tests-dynamic-tests
         */
    }
}
