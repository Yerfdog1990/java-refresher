package day54.test_cases;

import day54.page_objects.AccountRegistrationPage;
import day54.page_objects.HomePage;
import day54.test_base.BaseClass;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

public class TC001AccountRegistrationTest extends BaseClass {

    @Test(groups = {"Regression", "Master"})
    public void givenWebpage_whenRegisterNewAccount_theSuccess() {
        try {
            logger.info("***** TC001AccountRegistrationTest started *****");
            // Create HomePage Objects
            HomePage homePage = new HomePage(getDriver());
            homePage.clickAccount();
            logger.info("Clicked on Account link");
            homePage.clickRegister();
            logger.info("Clicked on Register link");

            // Create AccountRegistrationPage Objects
            AccountRegistrationPage accountPage = new AccountRegistrationPage(getDriver());

            // Set user details
            logger.info("Providing user details");
            accountPage.setFirstName(generateRandomString().toUpperCase());
            accountPage.setLastName(generateRandomString().toUpperCase());
            accountPage.setEmail(generateRandomEmail());

            // String password = randomAlphaNumeric();
            accountPage.setPassword(generateRandomPassword());

            logger.info("Clicking on policy");
            accountPage.clickPolicy();
            logger.info("Clicking on continue");
            accountPage.clickContinue();

            // Get confirmation message
            logger.info("Getting confirmation message");
            String confirmationMessage = accountPage.getConfirmationMessage();

            // Assert confirmation message
            logger.info("Asserting confirmation message");
            String registrationSuccessMessage = "Your Account Has Been Created!";
            if(confirmationMessage.equals(registrationSuccessMessage)){
                logger.info("Test passed");
                assertTrue(true);
            } else {
                logger.error("Test failed");
                logger.debug("Debug logs....");
                fail();
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            fail();
        } finally {
            logger.info("***** TC001AccountRegistrationTest ended *****");
        }
    }
}
