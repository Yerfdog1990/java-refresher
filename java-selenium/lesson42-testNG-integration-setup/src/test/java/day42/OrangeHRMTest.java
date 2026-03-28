package day42;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.AssertJUnit.assertEquals;

public class OrangeHRMTest {
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
        WebElement loginUsername = driver.findElement(By.xpath("//input[@placeholder='Username']"));
        loginUsername.sendKeys("Admin");
        WebElement loginPassword = driver.findElement(By.xpath("//input[@placeholder='Password']"));
        loginPassword.sendKeys("admin123");
        WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));
        loginButton.click();

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Validate that the login was successful by checking the dashboard title
        WebElement dashboardHeader = driver.findElement(By.xpath("//h6[contains(@class, 'oxd-topbar-header-breadcrumb-module')]"));
        assertEquals("Dashboard", dashboardHeader.getText());
    }
}
