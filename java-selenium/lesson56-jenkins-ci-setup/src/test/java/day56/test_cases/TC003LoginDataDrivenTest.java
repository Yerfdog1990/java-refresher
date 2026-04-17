package day56.test_cases;

import day56.page_objects.AccountLoginPage;
import day56.page_objects.HomePage;
import day56.page_objects.MyAccountPage;
import day56.test_base.BaseClass;
import day56.utilities.DataProviders;

import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

public class TC003LoginDataDrivenTest extends BaseClass {

    @Test(dataProvider = "LoginData", dataProviderClass = DataProviders.class, groups = "Data driven") // Getting data from a different class
    public void givenWebpage_whenLogin_thenVerify(String email, String password, String expectedValue) {
        logger.info("***** TC003LoginDataDrivenTest started *****");
        try {
            // Home Page
            HomePage homePage = new HomePage(getDriver());
            homePage.clickAccount();
            homePage.clickLogin();

            // Account Login Page
            AccountLoginPage accountPage = new AccountLoginPage(getDriver());
            accountPage.setEmail(email);
            accountPage.setPassword(password);
            accountPage.clickLogin();

            // My Account Page
            MyAccountPage myAccountPage = new MyAccountPage(getDriver());

            // Assert confirmation message
            logger.info("Asserting if account exists");
            if(myAccountPage.checkIfAccountExists()){
                logger.info("Test passed");
                assertTrue(true);
            }

            // Verify login using valid data
            if (expectedValue.equalsIgnoreCase("Valid")) {
                if(myAccountPage.checkIfAccountExists()){
                    logger.info("Test passed");
                    myAccountPage.clickLogout();
                    assertTrue(true);
                }else{
                    fail();
                    logger.error("Login failed");
                }
            }

            // Verify login using invalid data
            if(expectedValue.equalsIgnoreCase("Invalid")){
                if(!myAccountPage.checkIfAccountExists()){
                    logger.info("Test failed");
                    myAccountPage.clickLogout();
                    assertTrue(true);
                }else{
                    fail();
                    logger.error("Test passed");
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
            fail();
        } finally {
            logger.info("***** TC003LoginDataDrivenTest ended *****");
        }

    }

}
