package com.baeldung.lju.excutionorder;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.DisplayName;
import java.util.concurrent.atomic.AtomicInteger;


//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//@TestMethodOrder(MethodOrderer.Random.class)
@TestMethodOrder(MethodOrderer.DisplayName.class)
public class MethodOrderAnnotationUnitTest {
    /*
    Default Execution Order
    By default, JUnit doesn’t guarantee any particular order for test methods within a class.
    The exact order can vary from run to run or from one environment to another.

    Let’s look at an example of how the tests run by default, without any order specified:

    @Test
    void testOne() {
        System.out.println("Test One");
    }

    @Test
    void testTwo() {
        System.out.println("Test Two");
    }

    In this case, the output could be different each time:
        -Test One
        -Test Two

    It can also be:
        -Test Two
        -Test One

    Let’s highlight an important note. Relying on a specific execution order—like expecting testOne() to run before testTwo()—can make tests fragile and tightly coupled, which reduces maintainability.

    Each test should ideally be self-contained and independent. That said, there are valid cases where enforcing a specific order is necessary, and JUnit provides ways to handle that when needed.

    Simply put, we can control the execution order of test methods using @TestMethodOrder, or the order of nested test classes within a single parent using @TestClassOrder.
     */

    /*
    1.Using @TestMethodOrder
    The @TestMethodOrder annotation supports multiple strategies: the most common is assigning explicit order values using method annotations, but we can also sort tests by method name or display name.

    1.1: MethodOrderer.OrderAnnotation with @Order
    We can define the execution order of test methods with the @Order annotation. The @Order annotation accepts an Integer and executes the test in order from smallest to largest.
    Note that using the same number in @Order annotations for test methods or classes has equal priority. In this case, their execution order is determined arbitrarily and isn’t guaranteed to be consistent.
    Let’s create a basic test to showcase how method-level ordering works:
     */

    private static final AtomicInteger counter = new AtomicInteger(1);

    @Test
    @Order(3)
    void testOne() {
        System.out.println("Running testOne, order 3");
        Assertions.assertEquals(3, counter.getAndIncrement());
    }

    @Test
    @Order(1)
    void testTwo() {
        System.out.println("Running testTwo, order 1");
        Assertions.assertEquals(1, counter.getAndIncrement());
    }

    @Test
    @Order(2)
    void testThree() {
        System.out.println("Running testThree, order 2");
        Assertions.assertEquals(2, counter.getAndIncrement());
    }
    /*
    This ensures that testTwo() runs first, followed by testThree(), and testOne() runs last based on their @Order values:
        1.Running testTwo, order 1
        2.Running testThree, order 2
        3.Running testOne, order 3

    Instead of just numeric ordering, we can also order our tests with an alphanumeric orderer.
     */

    /*
    1.2: MethodOrderer.DisplayName and MethodOrderer.MethodName
    JUnit 5 provides a few implementations for enabling alphanumeric order execution of test methods.
    The MethodOrderer.MethodName sorts by method names, while MethodOrderer.DisplayName uses display names.

    Let’s assign a display name to our test methods using the @DisplayName annotation and observe how they are executed based on their display names:
     */
    @Test
    @DisplayName("1. Test One")
    void testAlphaOne() {
        System.out.println("Running testOne");
        Assertions.assertEquals(1, counter.getAndIncrement());
    }

    @Test
    @DisplayName("2. Test Two")
    void testAlphaTwo() {
        System.out.println("Running testTwo");
        Assertions.assertEquals(2, counter.getAndIncrement());
    }

    @Test
    @DisplayName("3. Test Three")
    void testAlphaThree() {
        System.out.println("Running testThree");
        Assertions.assertEquals(3, counter.getAndIncrement());
    }
    // It sorts tests alphabetically based on their @DisplayName. Here we’re controlling the execution order by prefixing our display names with numbers like “1.”, “2.”, and “3.”.
    // Naturally, the MethodOrderer.MethodName option follows the same principle, but uses the method name itself as the sorting criterion.

    /*
    1.3: The MethodOrderer.Random
    We can also randomize the execution order of test methods. While the default behavior is deterministic but unspecified, using MethodOrderer.Random deliberately shuffles the method order on each run.
    This helps expose unintended dependencies and encourages better test isolation.

    @TestMethodOrder(MethodOrderer.Random.class)
    class MethodRandomOrderUnitTest {
        // ...
    }
     */
}
