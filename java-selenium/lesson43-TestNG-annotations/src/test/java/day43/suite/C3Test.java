package day43.suite;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Slf4j
public class C3Test {
    @BeforeSuite
    void test1() {
        log.info("Executing test1 (Before Suite) from C3");
    }

    @Test
    void test2() {
        log.info("Executing test1 from C3");
    }

    @AfterSuite
    void test3() {
        log.info("Executing test3 (After Suite) from C3");
    }
}
