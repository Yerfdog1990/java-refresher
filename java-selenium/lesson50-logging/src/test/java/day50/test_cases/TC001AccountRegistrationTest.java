package day50.test_cases;

import day50.page_objects.AccountRegistrationPage;
import day50.page_objects.HomePage;
import day50.test_base.BaseClass;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.AssertJUnit.*;

@Slf4j
public class TC001AccountRegistrationTest extends BaseClass {

    @Test
    void givenWebpage_whenRegisterNewCount_theSuccess() {
        log.info("***** TC001AccountRegistrationTest started *****");
        // Create HomePage Objects
        HomePage homePage = new HomePage(driver);
        homePage.clickAccount();
        log.info("Clicked on Account link");
        homePage.clickRegister();
        log.info("Clicked on Register link");

        // Create AccountRegistrationPage Objects
        AccountRegistrationPage accountPage = new AccountRegistrationPage(driver);

        // Set user details
        log.info("Providing user details");
        accountPage.setFirstName(generateRandomString().toUpperCase());
        accountPage.setLastName(generateRandomString().toUpperCase());
        accountPage.setEmail(generateRandomEmail());

        // String password = randomAlphaNumeric();
        accountPage.setPassword(generateRandomPassword());

        log.info("Clicking on policy");
        accountPage.clickPolicy();
        log.info("Clicking on continue");
        accountPage.clickContinue();

        // Get confirmation message
        log.info("Getting confirmation message");
        String confirmationMessage = accountPage.getConfirmationMessage();

        // Assert confirmation message
        log.info("Asserting confirmation message");
        String registrationSuccessMessage = "Your Account Has Been Created!";
        if(confirmationMessage.equals(registrationSuccessMessage)){
            log.info("Test passed");
            assertTrue(true);
        } else {
            log.error("Test failed");
            log.debug("Debug logs....");
            fail();
        }
        log.info("***** TC001AccountRegistrationTest ended *****");
    }
}
