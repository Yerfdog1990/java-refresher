package com.baeldung.lju;

import com.baeldung.lju.junit.IntegrationTest;



class ApplicationIntegrationTest {

    /*
    Tagging via Custom Annotations
    Let’s look at ApplicationIntegrationTest. It has a single test method that calls the application’s main() method, executing a complex flow with a broad scope.
    Since this test covers a larger scope and may run slower than others, we can tag it as “integration“.
    This way, we can easily exclude it when we need a faster execution:
     */
    //@Test
    //@Tag("integration")
    @IntegrationTest
    void mainAppMethodIntegrationTest() {
        LjuApp.main(new String[] {});
    }
    /*
    In Java, we can create our own composed annotations by combining multiple annotations into one while inheriting their behavior.
    For example, we can encapsulate @Tag(“integration”) and @Test into a single, more expressive annotation.
    To ensure it works correctly and can be applied to methods, we also include @Retention(RetentionPolicy.RUNTIME) and @Target(ElementType.METHOD).
    Let’s create this custom annotation in the com.baeldung.lju.junit package of src/test/java:

    @Test
    @Tag("integration")
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface IntegrationTest {
    }

    Finally, let’s update our test class and  annotate mainMethodIntegrationTest() with @IntegrationTest, removing the individual JUnit annotations:

    class ApplicationIntegrationTest {

        @IntegrationTest
        void mainAppMethodIntegrationTest() {
            LjuApp.main(new String[] {});
        }

    }

    Using composed annotations helps keep our tests clean and expressive by merging multiple other annotations.
    This can be especially useful when dealing with cluttered code, full of duplicated annotations and tags.
     */
}