package day46;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.*;

import java.time.Duration;

import static org.testng.AssertJUnit.*;

// @Listeners(day46.MyListener.class)
public class ListenersDemoTest {
    private WebDriver driver;

    @BeforeClass
    public void setUp() {
        String url = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(url);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test(priority = 1)
    void givenWebpage_whenTestLogoVisibility_thenSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        boolean status = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//img[@alt='company-branding']"))).isDisplayed();
        assertFalse("Company branding logo should be visible", status);
    }

    @Test(priority = 2, dependsOnMethods = "givenWebpage_whenTestLogoVisibility_thenSuccess")
    void givenWebpage_whenTestTitleVisibility_thenSuccess() {
        assertEquals("OrangeHRM", driver.getTitle());
    }

    @Test(priority = 3)
    void givenWebpage_whenTestURLVisibility_thenSuccess() {
        String url = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
        assertEquals(url, driver.getCurrentUrl());
    }
}
