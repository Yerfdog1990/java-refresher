package day43.assertions;

import lombok.extern.slf4j.Slf4j;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Slf4j
public class HardVsSoftAssertionsTest {

    @Test
    void hardAssert() {
        log.info("My first statement in hard assert");
        Assert.assertEquals(2, 2);
        log.info("My second statement in hard assert");
    }

    @Test
    void softAssert() {
        SoftAssert softAssert = new SoftAssert();

        log.info("My first statement in soft assert");
        softAssert.assertEquals(2, 2); // Soft assertion
        log.info("My second statement in soft assert");

        softAssert.assertAll(); // Collect all soft assertions and throw them
    }
}
