package day47.test_case;


import day47.objects.LoginPage;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.AssertJUnit.assertEquals;

@Slf4j
public class LoginTest {
    private static final String URL  = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
    private static WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(URL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        if(driver != null){
            driver.close();
        }
    }

    @Test
    void givenOrangeHRMWebPage_whenLoginAsAdmin_thenHomePageIsDisplayed() throws InterruptedException {
        // Login details
        String username = "Admin";
        String password = "admin123";

        // Create login page object
        LoginPage loginPage = new LoginPage(driver);
        loginPage.setUsername(username);
        loginPage.setPassword(password);
        loginPage.clickSubmitButton();

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Validate that the login was successful by checking the dashboard title
        assertEquals("OrangeHRM", driver.getTitle());
    }
}
