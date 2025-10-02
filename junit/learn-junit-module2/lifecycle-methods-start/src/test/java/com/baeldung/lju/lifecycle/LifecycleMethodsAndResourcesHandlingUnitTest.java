package com.baeldung.lju.lifecycle;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LifecycleMethodsAndResourcesHandlingUnitTest {
    private static BufferedReader fileReader;
    public static final Logger logger = LoggerFactory.getLogger(LifecycleMethodsAndResourcesHandlingUnitTest.class);

    // 1.Lifecycle Methods
    /*
    We often have to perform some setup or teardown steps that are common to all the tests in a class.
    In order to avoid repeating this logic on each test, we can use the Lifecycle Methods for a DRYer approach.
    A lifecycle method is any method that is annotated with one of the @BeforeEach, @AfterEach, @BeforeAll, or @AfterAll annotations.
    Lifecycle methods can be declared locally or inherited from superclasses or interfaces, and are subject to a few restrictions:
        -must not be abstract
        -must not return a value (return type must be void)
        -must not be private
    Just to be able to clearly appreciate how the lifecycle methods are executed, we won’t be actually testing anything at this stage,
    but instead, we’ll consider a naive scenario in which our system needs to open and read the contents of the file.txt file, like the one present in the src/test/resources directory:
        (1/4) This will help understand the test's lifecycle and how to handle resources.
        (2/4) It contains several lines.
        (3/4) And we'll read one line at a time.
        (4/4) This is the last line.

    Now, let’s create an empty LifecycleMethodsAndResourcesHandlingUnitTest test class and proceed to discuss each annotation.
     */


    // 1.1: @BeforeEach
    /*
    The @BeforeEach annotation indicates to JUnit that it should execute the annotated method before running each test in the current class.
    We typically use this lifecycle method when we have some common setup logic to run before each test.
    Let’s imagine that we need to access the contents of the file.txt file before each test:
     */
    //@BeforeEach
    void setupUsingResource() throws Exception {
        InputStream fileStream = LifecycleMethodsAndResourcesHandlingUnitTest.class.getClassLoader()
                .getResourceAsStream("file.txt");
        fileReader = new BufferedReader(new InputStreamReader(fileStream));

        logger.info("fileReader is ready: {}", fileReader.ready());
    }
    /*
    This mechanism greatly simplifies our test cases by avoiding the need to repeat this rather complex logic on each test.
    Let’s also add a simple test method that prints to the console two lines from the file.txt file:
     */

    //@Test
    void givenOpenResource_whenReadLines1_thenLineIsLogged() throws Exception {
        for (int i = 0; i < 2; i++) {
            logger.info(fileReader.readLine());
        }
        fileReader.close();
    }
    /*
    It’s important to manage resources correctly when writing tests, so we’re also closing the fileReader in the last step of the test.
    We’ll see the following output printed to the console when running the test:
    08:55:53.684 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- fileReader is ready: true
    08:55:53.687 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (1/4) This will help understand the test's lifecycle and how to handle resources.
    08:55:53.687 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (2/4) It contains several lines.

    Based on the console logs, we can notice that the setupUsingResource method, which is annotated with @BeforeEach, is executed before the test.
    Now, let’s take things further and add another test in the same class, which also prints two lines from the file:
     */

    //@Test
    void givenOpenResource_whenReadLines2_thenLineIsLogged() throws Exception {
        for (int i = 0; i < 2; i++) {
            logger.info(fileReader.readLine());
        }
        fileReader.close();
    }
    /*
    Running the LifecycleMethodsAndResourcesHandlingUnitTest test class produces the output below:
    09:01:18.647 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- fileReader is ready: true
    09:01:18.650 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (1/4) This will help understand the test's lifecycle and how to handle resources.
    09:01:18.650 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (2/4) It contains several lines.
    09:01:18.658 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- fileReader is ready: true
    09:01:18.658 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (1/4) This will help understand the test's lifecycle and how to handle resources.
    09:01:18.658 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (2/4) It contains several lines.

    We can observe that JUnit executed the @BeforeEach method twice, once before each test, and that in both cases, the first two lines of the file are logged.
     */

    // 1.2: @AfterEach
    /*
    The @AfterEach annotation indicates to JUnit that it should execute the annotated method after the execution of each test in the current class.
    We typically use this lifecycle method when we have some common cleanup logic to run after each test.
    In the previous example, we already have some common cleanup logic in each test which closes the fileReader.
    Let’s move this common logic to a separate method:
     */

    //@AfterEach
    void cleanupResource() throws Exception {
        fileReader.close();
        logger.info("fileReader is closed");
    }

    @Test
    void givenOpenResource_whenReadLines1__thenLineIsLogged() throws Exception {
        for (int i = 0; i < 2; i++) {
            logger.info(fileReader.readLine());
        }
    }

    @Test
    void givenOpenResource_whenReadLines2__thenLineIsLogged() throws Exception {
        for (int i = 0; i < 2; i++) {
            logger.info(fileReader.readLine());
        }
    }
    /*
    We’ll see the following output printed to the console when running the updated test class:

    09:17:36.283 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- fileReader is ready: true
    09:17:36.286 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (1/4) This will help understand the test's lifecycle and how to handle resources.
    09:17:36.286 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (2/4) It contains several lines.
    09:17:36.287 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- fileReader is closed
    09:17:36.293 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- fileReader is ready: true
    09:17:36.293 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (1/4) This will help understand the test's lifecycle and how to handle resources.
    09:17:36.294 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (2/4) It contains several lines.
    09:17:36.294 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- fileReader is closed

    We can notice that JUnit executed the @AfterEach annotated method twice, once after each test.
     */

    // 1.3: @BeforeAll
    /*
    There are times when the setup required for tests is expensive, and we’d like to run it only once for all tests in a class.
    Typical scenarios include fetching external data, connecting or setting up a remote or embedded database, or starting a local container or an Apache Kafka server for the integration tests.
    This is the perfect use case for the @BeforeAll annotation, which indicates to JUnit that it should execute the annotated method only once before running any test in the current class.
    Let’s comment out our @BeforeEach and @AfterEach annotations to open the file only once and explore the effects of such an operation:
     */

    @BeforeAll
    static void setupResource() throws Exception {
        InputStream fileStream = LifecycleMethodsAndResourcesHandlingUnitTest.class.getClassLoader()
                .getResourceAsStream("file.txt");
        fileReader = new BufferedReader(new InputStreamReader(fileStream));

        logger.info("static fileReader is ready: {}", fileReader.ready());
    }

    /* @BeforeEach
    void setupUsingResource() throws Exception {
        // ...
    } */

    /*
    Running the updated LifecycleMethodsAndResourcesHandlingUnitTest class produces the output below:

    12:22:18.889 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- static fileReader is ready: true
    12:22:18.896 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (1/4) This will help understand the test's lifecycle and how to handle resources.
    12:22:18.896 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (2/4) It contains several lines.
    12:22:18.902 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (3/4) And we'll read one line at a time.
    12:22:18.902 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (4/4) This is the last line.

    Inspecting the output, we can see that JUnit executed the @BeforeAll annotated method only once for both tests, in contrast to the @BeforeEach and @AfterEach annotated methods.
    We can also notice that the second test continued reading the file contents from where the first test left it.
    Therefore, the console output now contains all the contents of the file.
     */

    // 1.4: @AfterAll
    /*
    The @AfterAll annotation indicates to JUnit that it should execute the annotated method only once after running all the tests in the current class.
    Typical scenarios include cleanup tasks after the integration tests, like closing a database connection, shutting down a container, and freeing up temporary resources.
    Let’s include the cleanup task for the fileReader class variable:
     */

    @AfterAll
    static void cleanupStaticResource() throws Exception {
        fileReader.close();
        logger.info("static fileReader is closed");
    }
    /*
    Note that the method annotated with @AfterAll is static. Running the updated LifecycleMethodsAndResourcesHandlingUnitTest class produces the output below:

    12:24:10.451 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- static fileReader is ready: true
    12:24:10.459 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (1/4) This will help understand the test's lifecycle and how to handle resources.
    12:24:10.459 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (2/4) It contains several lines.
    12:24:10.464 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (3/4) And we'll read one line at a time.
    12:24:10.464 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- (4/4) This is the last line.
    12:24:10.465 [main] INFO com.baeldung.lju.persistence.repository.impl.LifecycleMethodsAndResourcesHandlingUnitTest -- static fileReader is closed

    Looking at the last line in the output, we can see that JUnit executed the @AfterAll annotated method only once for all tests, in contrast to the @BeforeEach and @AfterEach annotated methods.
     */
}
