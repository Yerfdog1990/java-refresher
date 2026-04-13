package day51.test_cases;

import day51.page_objects.AccountLoginPage;
import day51.page_objects.HomePage;
import day51.page_objects.MyAccountPage;
import day51.test_base.BaseClass;
import day51.utilities.DataProviders;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

@Slf4j
public class TC003LoginDataDrivenTest extends BaseClass {

    @Test(dataProvider = "LoginData", dataProviderClass = DataProviders.class) // Getting data from a different class
    public void givenWebpage_whenLogin_thenVerify(String email, String password, String expectedValue) {
        log.info("***** TC003LoginDataDrivenTest started *****");
        try {
            // Home Page
            HomePage homePage = new HomePage(driver);
            homePage.clickAccount();
            homePage.clickLogin();

            // Account Login Page
            AccountLoginPage accountPage = new AccountLoginPage(driver);
            accountPage.setEmail(email);
            accountPage.setPassword(password);
            accountPage.clickLogin();

            // My Account Page
            MyAccountPage myAccountPage = new MyAccountPage(driver);
            boolean targetPage = myAccountPage.checkIfAccountExists();

            // Verify login using valid data
            if (expectedValue.equalsIgnoreCase("Valid")) {
                if(targetPage){
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
                if(!targetPage){
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
