package com.baeldung.lju.excutionorder;

import org.junit.jupiter.api.*;

import java.util.concurrent.atomic.AtomicInteger;

@TestClassOrder(ClassOrderer.OrderAnnotation.class)
public class ClassOrderAnnotationUnitTest {
     /*
    2. Using @TestClassOrder
    Note that @TestClassOrder controls the order of nested classes within the annotated parent test class—it doesn’t apply globally across all test classes on the classpath.
    JUnit reads the annotation when it encounters the parent class and applies the specified ordering (e.g., by class name) to its nested classes.
    Let’s write a test While method ordering applies within a single class, @TestClassOrder lets us define the order in which nested test classes are executed within their enclosing test class.
    At runtime, the JUnit engine scans the test classes in a package and applies the specified ordering strategy when running them.
    The supported approaches are similar to those available for method-level ordering.

    2.1: ClassOrderer.OrderAnnotation with @Order
    The @Order annotation lets us specify the execution order within a parent test class. It’s helpful when we need precise control over the sequence in which tests run.
    to set class priorities using the ClassOrderer.OrderAnnotation with @TestClassOrder. Each nested class contains a single test method that prints a message and checks the counter to enforce the sequence:
     */

    private static final AtomicInteger counter = new AtomicInteger(1);

    @Nested
    @Order(3)
    class One {
        @Test
        void testOne() {
            System.out.println("Running testOne, order 3");
            Assertions.assertEquals(3, counter.getAndIncrement());
        }
    }

    @Nested
    @Order(1)
    class Two {
        @Test
        void testTwo() {
            System.out.println("Running testTwo, order 1");
            Assertions.assertEquals(1, counter.getAndIncrement());
        }
    }

    @Nested
    @Order(2)
    class Three {
        @Test
        void testThree() {
            System.out.println("Running testThree, order 2");
            Assertions.assertEquals(2, counter.getAndIncrement());
        }
    }
    /*
    It ensures that nested classes execute according to their @Order annotations, with lower values running first, producing the following output:
        -Running TestOne, order 1
        -Running TestThree, order 2
        -Running TestTwo, order 3
    This makes @TestClassOrder useful for organizing complex test logic locally by subclass, while keeping execution order predictable.
     */

    /*
    2.2: ClassOrderer.ClassName
    Similar to the previous behavior, we can sort test classes using either their class name or display name.

    ClassName orders test classes alphabetically based on their fully qualified class names (i.e., package + class name).
    It’s useful when classes are spread across multiple packages and we want consistent ordering:

    @TestClassOrder(ClassOrderer.ClassName.class)
    public class ClassClassNameOrderUnitTest {
        }

    2.3: ClassOrderer.DisplayName with @DisplayName
    DisplayName sorts test classes by their display names. If no display name is provided, the class name is used by default.
    It’s ideal for achieving predictable alphabetical order based on custom display names:
     */
    @Nested
    @DisplayName("1. Class One")
    class One_ {
    }

    @Nested
    @DisplayName("2. Class Two")
    class Two_ {
    }

    @Nested
    @DisplayName("3. Class Three")
    class Three_ {
    }
    /*
    4. ClassOrderer.Random
    Like test methods, we can use ClassOrderer.Random.class to make nested test classes run in a different order each time we execute the test set:

    @TestClassOrder(ClassOrderer.Random.class)
    public class ClassRandomOrderUnitTest {
        // ...
    }
     */
}
