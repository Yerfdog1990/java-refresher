package day29;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class HandlingAlertsTest {
    private static final String ORANGEHRM_URL = "https://the-internet.herokuapp.com/javascript_alerts";
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(HandlingAlertsTest.class);

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(ORANGEHRM_URL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // Close alert window using accept()
    @Test
    void givenWebElements_whenOpenAlertWindow_thenUseAcceptToClose() throws InterruptedException {
        WebElement element = driver.findElement(By.xpath("//button[@onclick='jsAlert()']"));
        element.click();
        Thread.sleep(5000);
        Alert alert = driver.switchTo().alert();
        LOG.info("Alert text: {}", alert.getText());
        alert.accept();
        assertTrue(driver.getCurrentUrl().contains("javascript_alerts"), "Expected to be on JavaScript Alerts page after accepting alert");
    }

    // Close alert window using dismiss()
    @Test
    void givenWebElements_whenOpenAlertWindow_thenUseDismissToClose() throws InterruptedException {
        WebElement element = driver.findElement(By.xpath("//button[@onclick='jsConfirm()']"));
        element.click();
        Thread.sleep(5000);
        Alert alert = driver.switchTo().alert();
        LOG.info("Alert text: {}", alert.getText());
        alert.dismiss();
        assertTrue(driver.getCurrentUrl().contains("javascript_alerts"), "Expected to be on JavaScript Alerts page after accepting alert");
    }

    // Pass value into the alert window using sendKeys()
    @Test
    void givenWebElements_whenOpenAlertWindow_thenUseSendKeysToEnterValue() throws InterruptedException {
        WebElement element = driver.findElement(By.xpath("//button[@onclick='jsPrompt()']"));
        element.click();
        Thread.sleep(5000);
        Alert alert = driver.switchTo().alert();
        alert.sendKeys("Hello World!");
        alert.accept();
        assertTrue(driver.getCurrentUrl().contains("javascript_alerts"), "Expected to be on JavaScript Alerts page after accepting alert");
    }

    // Handle alert using explicit wait
    @Test
    void givenWebElements_whenOpenAlertWindow_thenUseExplicitWaitToHandle() throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement element = driver.findElement(By.xpath("//button[@onclick='jsAlert()']"));
        element.click();
        Thread.sleep(5000);

        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        String alertText = alert.getText();
        LOG.info("Alert text: {}", alertText);
        alert.accept();
        assertEquals("I am a JS Alert", alertText);
    }

    // Handle authenticated pop up
    @Test
    void givenWebElements_whenOpenAuthenticationPopUp_thenHandlePopUp() {
        driver.get("https://admin:admin@the-internet.herokuapp.com/basic_auth");
        LOG.info("Current URL: {}", driver.getCurrentUrl());
        assertTrue(driver.getCurrentUrl().contains("basic_auth"), "Expected to be on Basic Auth page");
        String title = driver.getTitle();
        LOG.info("Page title: {}", title);
        assertEquals("The Internet", title);
    }
}
