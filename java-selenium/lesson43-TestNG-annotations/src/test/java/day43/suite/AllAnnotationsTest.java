package day43.suite;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.*;

@Slf4j
public class AllAnnotationsTest {

    @BeforeSuite
    void test1() {
        log.info("This is BeforeSuite");
    }

    @Test
    void test2() {
        log.info("This is Test 2");
    }

    @Test
    void test3() {
        log.info("This is Test 3");
    }

    @BeforeTest
    void test4() {
        log.info("This is BeforeTest");
    }

    @AfterTest
    void test5() {
        log.info("This is AfterTest");
    }

    @AfterSuite
    void test6() {
        log.info("This is AfterSuite");
    }

    @BeforeClass
    void test7() {
        log.info("This is BeforeClass");
    }

    @AfterClass
    void test8() {
        log.info("This is AfterClass");
    }

    @BeforeMethod
    void test9() {
        log.info("This is BeforeMethod");
    }

    @AfterMethod
    void test10() {
        log.info("This is AfterMethod");
    }
}
