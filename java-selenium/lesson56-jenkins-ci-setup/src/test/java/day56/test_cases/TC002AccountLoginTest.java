package day56.test_cases;

import day56.page_objects.AccountLoginPage;
import day56.page_objects.HomePage;
import day56.test_base.BaseClass;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

public class TC002AccountLoginTest extends BaseClass {

    @Test(groups = {"Sanity", "Master"})
    public void givenWebpage_whenLogin_theSuccess() throws IOException {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            Properties properties = new Properties();
            properties.load(fis);

            logger.info("***** TC002AccountLoginTest started *****");
            // Create HomePage Objects
            HomePage homePage = new HomePage(getDriver());
            homePage.clickAccount();
            logger.info("Clicked on Account link");
            homePage.clickLogin();
            logger.info("Clicked on Login link");

            // Create AccountLoginPage Objects
            AccountLoginPage accountPage = new AccountLoginPage(getDriver());

            // Set user details
            logger.info("Providing user details");
            accountPage.setEmail(properties.getProperty("email"));
            accountPage.setPassword(properties.getProperty("password"));

            logger.info("Clicking on login button");
            accountPage.clickLogin();

            // Assert confirmation message
            logger.info("Asserting if account exists");
            if(accountPage.checkIfAccountExists()){
                logger.info("Test passed");
                assertTrue(true);
            } else {
                logger.error("Test failed. Email or password is incorrect");
                logger.debug("Debug logs....");
                fail();
            }
        } catch (IOException e) {
            logger.info(e.getMessage());
            fail();
        } finally {
            logger.info("***** TC002AccountLoginTest ended *****");
        }
    }
}
