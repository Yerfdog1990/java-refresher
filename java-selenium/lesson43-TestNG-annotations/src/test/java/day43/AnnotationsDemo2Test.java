package day43;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.*;

@Slf4j
public class AnnotationsDemo2Test {

    // Run before each method
    @BeforeClass
    void setUp() {
        log.info("Setting up");
    }

    @Test
    void test1() {
        log.info("Executing test1");
    }

    @Test
    void test2() {
        log.info("Executing test2");
    }

    @Test
    void test3() {
        log.info("Executing test3");
    }

    //Run after each method
    @AfterClass
    void tearDown() {
        log.info("Tearing down");
    }
}
