package day24;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class BrowserMethodsTest {
    private static final String ORANGEHRM_URL = "https://www.orangehrm.com/";
    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final Logger LOG = LoggerFactory.getLogger(BrowserMethodsTest.class);

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get(ORANGEHRM_URL);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void givenDriver_whenClose_thenCloseBrowser() {
        try {
            // Scroll to the element
            WebElement ebooksLink = wait.until(ExpectedConditions.elementToBeClickable(
                    By.xpath("//a[normalize-space()='E-Books' and contains(@href,'e-books')]")
            ));

            // Scroll into view using JavaScript
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center', inline: 'nearest'});",
                    ebooksLink
            );

            // Add a small delay for the scroll to complete
            Thread.sleep(1000);

            // Click using JavaScript as a fallback
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", ebooksLink
            );

            driver.close();
            LOG.info("Successfully clicked on E-Books link");

            // Verify the new window/tab was opened
            assertEquals(1, driver.getWindowHandles().size(), "Expected a new window/tab to be opened");

        } catch (Exception e) {
            LOG.error("Error during test execution: {}", e.getMessage());
        }
    }

    @Test
    void givenDriver_whenQuit_thenCloseAllBrowsers() {
        // Open a new window/tab
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("https://www.orangehrm.com/contact-sales/");

        // Verify both windows are open
        Set<String> windowHandles = driver.getWindowHandles();
        assertEquals(2, windowHandles.size(), "Expected exactly 2 windows to be open before quit");

        try {
            // Quit the driver (closes all windows and ends the session)
            driver.quit();

            // Try to get window handles after quit (should throw NoSuchSessionException)
            driver.getWindowHandles();
            fail("Expected NoSuchSessionException to be thrown after quit()");
        } catch (NoSuchSessionException e) {
            // This is expected - the session should be invalid after quit()
            LOG.info("Successfully verified session is closed after quit()");
        }
    }
}