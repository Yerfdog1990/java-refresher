package day53.test_cases;

import day53.page_objects.AccountRegistrationPage;
import day53.page_objects.HomePage;
import day53.test_base.BaseClass;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

@Slf4j
public class TC001AccountRegistrationTest extends BaseClass {

    @Test(groups = {"Regression", "Master"})
    public void givenWebpage_whenRegisterNewAccount_theSuccess() {
        try {
            log.info("***** TC001AccountRegistrationTest started *****");
            // Create HomePage Objects
            HomePage homePage = new HomePage(getDriver());
            homePage.clickAccount();
            log.info("Clicked on Account link");
            homePage.clickRegister();
            log.info("Clicked on Register link");

            // Create AccountRegistrationPage Objects
            AccountRegistrationPage accountPage = new AccountRegistrationPage(getDriver());

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
        } catch (Exception e) {
            log.info(e.getMessage());
            fail();
        } finally {
            log.info("***** TC001AccountRegistrationTest ended *****");
        }
    }
}
