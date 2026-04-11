package day49.test_cases;

import day49.page_objects.AccountRegistrationPage;
import day49.page_objects.HomePage;
import day49.test_base.BaseClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.AssertJUnit.assertEquals;

public class TC001AccountRegistrationTest extends BaseClass {

    private WebDriver driver;

    @BeforeClass
    void setup() {
        driver = new ChromeDriver();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver.get("http://localhost/opencart/");
        driver.manage().window().maximize();
    }

    @AfterClass
    void tearDown() {
        if (driver != null) {
            driver.close();
        }
    }

    @Test
    void givenWebpage_whenRegisterNewCount_theSuccess() {

        // Create HomePage Objects
        HomePage homePage = new HomePage(driver);
        homePage.clickAccount();
        homePage.clickRegister();

        // Create AccountRegistrationPage Objects
        AccountRegistrationPage accountPage = new AccountRegistrationPage(driver);

        // Set user details
        accountPage.setFirstName(generateRandomString().toUpperCase());
        accountPage.setLastName(generateRandomString().toUpperCase());
        accountPage.setEmail(generateRandomEmail());

        // String password = randomAlphaNumeric();
        accountPage.setPassword(generateRandomPassword());

        accountPage.clickPolicy();
        accountPage.clickContinue();

        // Get confirmation message
        String confirmationMessage = accountPage.getConfirmationMessage();

        // Assert confirmation message
        assertEquals("Your Account Has Been Created!", confirmationMessage);

    }
}
