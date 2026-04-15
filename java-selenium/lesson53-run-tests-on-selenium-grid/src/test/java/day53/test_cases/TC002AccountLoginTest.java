package day53.test_cases;

import day53.page_objects.AccountLoginPage;
import day53.page_objects.HomePage;
import day53.test_base.BaseClass;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

@Slf4j
public class TC002AccountLoginTest extends BaseClass {

    @Test(groups = {"Sanity", "Master"})
    public void givenWebpage_whenLogin_theSuccess() throws IOException {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            Properties properties = new Properties();
            properties.load(fis);

            log.info("***** TC002AccountLoginTest started *****");
            // Create HomePage Objects
            HomePage homePage = new HomePage(getDriver());
            homePage.clickAccount();
            log.info("Clicked on Account link");
            homePage.clickLogin();
            log.info("Clicked on Login link");

            // Create AccountLoginPage Objects
            AccountLoginPage accountPage = new AccountLoginPage(getDriver());

            // Set user details
            log.info("Providing user details");
            accountPage.setEmail(properties.getProperty("email"));
            accountPage.setPassword(properties.getProperty("password"));

            log.info("Clicking on login button");
            accountPage.clickLogin();

            // Assert confirmation message
            log.info("Asserting if account exists");
            if(accountPage.checkIfAccountExists()){
                log.info("Test passed");
                assertTrue(true);
            } else {
                log.error("Test failed. Email or password is incorrect");
                log.debug("Debug logs....");
                fail();
            }
        } catch (IOException e) {
            log.info(e.getMessage());
            fail();
        } finally {
            log.info("***** TC002AccountLoginTest ended *****");
        }
    }
}
