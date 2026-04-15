package day53.test_cases;

import day53.page_objects.AccountLoginPage;
import day53.page_objects.HomePage;
import day53.page_objects.MyAccountPage;
import day53.test_base.BaseClass;
import day53.utilities.DataProviders;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

@Slf4j
public class TC003LoginDataDrivenTest extends BaseClass {

    @Test(dataProvider = "LoginData", dataProviderClass = DataProviders.class, groups = "Data driven") // Getting data from a different class
    public void givenWebpage_whenLogin_thenVerify(String email, String password, String expectedValue) {
        log.info("***** TC003LoginDataDrivenTest started *****");
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
            log.info("Asserting if account exists");
            if(myAccountPage.checkIfAccountExists()){
                log.info("Test passed");
                assertTrue(true);
            }

            // Verify login using valid data
            if (expectedValue.equalsIgnoreCase("Valid")) {
                if(myAccountPage.checkIfAccountExists()){
                    log.info("Test passed");
                    myAccountPage.clickLogout();
                    assertTrue(true);
                }else{
                    fail();
                    log.error("Login failed");
                }
            }

            // Verify login using invalid data
            if(expectedValue.equalsIgnoreCase("Invalid")){
                if(!myAccountPage.checkIfAccountExists()){
                    log.info("Test failed");
                    myAccountPage.clickLogout();
                    assertTrue(true);
                }else{
                    fail();
                    log.error("Test passed");
                }
            }
        } catch (Exception e) {
            log.info(e.getMessage());
            fail();
        } finally {
            log.info("***** TC003LoginDataDrivenTest ended *****");
        }

    }

}
