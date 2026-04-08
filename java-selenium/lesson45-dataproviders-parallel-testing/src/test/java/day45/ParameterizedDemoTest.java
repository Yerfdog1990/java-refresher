package day45;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

public class ParameterizedDemoTest {

    private WebDriver driver;

    @BeforeClass
    @Parameters({"browser", "url"})
    public void setUp(String br, String url) {

        switch (br.toLowerCase()){
            case "chrome" : driver = new ChromeDriver();
            break;
            case "firefox" : driver = new FirefoxDriver();
            break;
            case "edge" : driver = new EdgeDriver();
            break;
            case "safari" : driver = new SafariDriver();
            break;
            default : System.out.println("Invalid browser");
                return;
        }
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
        assertTrue("Company branding logo should be visible", status);
    }

    @Test(priority = 2)
    void givenWebpage_whenTestTitleVisibility_thenSuccess() {
        assertEquals("OrangeHRM", driver.getTitle());
    }

    @Test(priority = 3)
    void givenWebpage_whenTestURLVisibility_thenSuccess() {
        String url = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";
        assertEquals(url, driver.getCurrentUrl());
    }
}