package day43.suite;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

@Slf4j
public class C2Test {
    @Test
    void test1() {
        log.info("Executing test1 from C2");
    }

    @AfterTest
    void test2() {
        log.info("Executing test2 (After Test) from C2");
    }
}
