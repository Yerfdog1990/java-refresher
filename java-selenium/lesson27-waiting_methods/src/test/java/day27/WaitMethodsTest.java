package day27;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class WaitMethodsTest {
    private static final String ORANGEHRM_URL = "https://www.ebay.com/";
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(WaitMethodsTest.class);

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(ORANGEHRM_URL);
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void givenWebElements_whenApplyImplicitWait_thenLogWaitingTime() {
        // Set implicit wait for this test only
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        String searchTerm = "MacBook";
        long startTime = System.currentTimeMillis();

        try {
            // First, find the element (will use the implicit wait)
            WebElement emailInput = driver.findElement(By.xpath("//input[@id='gh-ac']"));
            emailInput.sendKeys(searchTerm);
            emailInput.submit();

            // Find element again after submission
            emailInput = driver.findElement(By.xpath("//input[@id='gh-ac']"));
            String searchResult = emailInput.getAttribute("value");

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            LOG.info("Test completed in {} ms", duration);
            assertEquals(searchTerm, searchResult, "Search term should match input");

        } finally {
            // Reset implicit wait to default (0) to avoid affecting other tests
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        }
    }

    @Test
    void givenWebElements_whenWaitUntilElementIsVisible_thenLogWaitingTime() {
        String searchTerm = "MacBook";

        long startTime = System.currentTimeMillis();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for the element to be present and visible
        WebElement emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='gh-ac']")));

        emailInput.sendKeys(searchTerm);
        emailInput.submit();

        // Wait for the element again after submission
        emailInput = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='gh-ac']")));

        String searchResult = emailInput.getAttribute("value");

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        LOG.info("Test completed in {} ms", duration);
        assertEquals(searchTerm, searchResult, "Search term should match input");
    }

    @Test
    void givenWebElements_whenWaitUntilElementIsClickable_thenLogWaitingTime() {
        String searchTerm = "MacBook";

        long startTime = System.currentTimeMillis();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Wait for the element to be clickable
        WebElement emailInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@id='gh-ac']")));

        emailInput.sendKeys(searchTerm);
        emailInput.submit();

        // Wait for the element again after submission
        emailInput = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@id='gh-ac']")));

        String searchResult = emailInput.getAttribute("value");

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        LOG.info("Test completed in {} ms", duration);
        assertEquals(searchTerm, searchResult, "Search term should match input");
    }

    @Test
    void givenWebElements_whenApplyFluentWait_thenVerify() {
        String searchTerm = "MacBook";
        long startTime = System.currentTimeMillis();

        // Configure FluentWait
        Wait<WebDriver> wait = new FluentWait<>(driver)
                .withTimeout(Duration.ofSeconds(30L))
                .pollingEvery(Duration.ofSeconds(2L))
                .ignoring(NoSuchElementException.class);

        // Wait for the element to be present and visible
        WebElement searchInput = wait.until(driver -> {
            WebElement element = driver.findElement(By.id("gh-ac"));
            return element.isDisplayed() ? element : null;
        });

        // Interact with the element
        Objects.requireNonNull(searchInput).sendKeys(searchTerm);
        searchInput.submit();

        // Wait for results and verify
        wait.until(driver -> driver.getTitle().toLowerCase().contains(searchTerm.toLowerCase()));

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        LOG.info("Test completed in {} ms", duration);
        assertTrue(driver.getTitle().toLowerCase().contains(searchTerm.toLowerCase()), "Page title should contain search term");
    }
}

