package day43.suite;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Slf4j
public class C1Test {
    @Test
    void test1() {
        log.info("Executing test1 from C1");
    }

    @BeforeTest
    void test2() {
        log.info("Executing test2 (Before Test) from C1");
    }
}
