package day51.test_cases;

import day51.page_objects.AccountLoginPage;
import day51.page_objects.HomePage;
import day51.test_base.BaseClass;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

@Slf4j
public class TC002AccountLoginTest extends BaseClass {

    @Test
    void givenWebpage_whenLogin_theSuccess() throws IOException {
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            Properties properties = new Properties();
            properties.load(fis);

            log.info("***** TC002AccountLoginTest started *****");
            // Create HomePage Objects
            HomePage homePage = new HomePage(driver);
            homePage.clickAccount();
            log.info("Clicked on Account link");
            homePage.clickLogin();
            log.info("Clicked on Login link");

            // Create AccountLoginPage Objects
            AccountLoginPage accountPage = new AccountLoginPage(driver);

            // Set user details
            log.info("Providing user details");
            accountPage.setEmail(properties.getProperty("email"));
            accountPage.setPassword(properties.getProperty("password"));

            log.info("Clicking on login button");
            accountPage.clickLogin();

            // Check if account exists
            log.info("Checking if account exists");
            boolean accountExists = accountPage.checkIfAccountExists();

            // Assert confirmation message
            log.info("Asserting if account exists");
            if(accountExists){
                log.info("Test passed");
                assertTrue(true);
            } else {
                log.error("Test failed");
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
