package day43;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

@Slf4j
public class AnnotationsDemo1Test {

    // Run before each method
    @BeforeMethod
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
    @AfterMethod
    void tearDown() {
        log.info("Tearing down");
    }
}
