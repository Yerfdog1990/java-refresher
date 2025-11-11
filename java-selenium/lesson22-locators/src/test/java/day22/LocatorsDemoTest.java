package day22;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.openqa.selenium.*;
import org.openqa.selenium.safari.SafariDriver;


import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class LocatorsDemoTest {
    WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(LocatorsDemoTest.class);

    @BeforeEach
    void setup() {
        driver = new SafariDriver();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
        driver.get("https://www.ebay.com/"); // "https://demo.opencart.com/"
        driver.manage().window().maximize();
    }
    @AfterEach
    void tearDown() {
        driver.quit();
    }

    @Test
    public void givenWebElements_whenGetTitle_thenSuccess() {
        // Test for correct title
        String actualTitle = driver.getTitle();
        String expectedTitle = "Electronics, Cars, Fashion, Collectibles & More | eBay";
        LOG.info(() -> "Actual title: " + actualTitle);
        assertEquals(expectedTitle, actualTitle);

        // Test for wrong title
        String wrongTitle = "Wrong title";
        LOG.info(() -> "Wrong title: " + wrongTitle);
        assertNotEquals(wrongTitle, actualTitle);
    }

    @Test
    public void givenWebElements_whenFindElementByExistingName_thenSuccess() {
        // Test for the correct name element
        String actualName = driver.findElement(By.name("_trksid")).getAttribute("value");
        LOG.info(() -> "Actual name: " + actualName);
        String expectedName = "m570.l1313";
        assertEquals(expectedName, actualName);

        // Test for the non-existing name element
        String nonExistingName = "m570.l1313123";
        LOG.info(() -> "Non-existing name: " + nonExistingName);
        assertNotEquals(nonExistingName, actualName);

        // Test for the search box
        String searchTerm = "Iphone";
        LOG.info(() -> "Searching for: " + searchTerm);
        WebElement searchBox = driver.findElement(By.name("_nkw"));
        searchBox.sendKeys(searchTerm);

        String enteredValue = searchBox.getAttribute("value");
        LOG.info(() -> "Entered value in search box: " + enteredValue);
        assertEquals(searchTerm, enteredValue);
    }

    @Test
    public void givenWebElements_whenFindElementByClassName_thenSuccess() {
        String className = "gh-header__logo-cats-wrap";
        WebElement element = driver.findElement(By.className(className));
        boolean isDisplayed = element.isDisplayed();
        LOG.info(() -> "Is logo displayed: " + isDisplayed);
        assertTrue(isDisplayed);
    }

    @Test
    public void givenWebElements_whenClickLinkText_thenSuccess() {
        String linkText = "Sign in";
        WebElement linked_test = driver.findElement(By.linkText(linkText));
        linked_test.click();
        WebElement partialLinkedTest =  driver.findElement(By.partialLinkText("ign"));
        partialLinkedTest.click();
        assertTrue(driver.getCurrentUrl().contains(linkText) || driver.getCurrentUrl().contains("ign"));
        LOG.info(() -> "Current URL: " + driver.getCurrentUrl());
    }

    @Test
    public void givenWebElements_whenGetTotalClassNames_thenSuccess() {
        List<WebElement> totalClassNames = driver.findElements(By.className("gh-header__logo-cats-wrap"));
        LOG.info(() -> "Total class names: " + totalClassNames.size());
        assertEquals(1, totalClassNames.size());
    }

    @Test
    public void givenWebElements_whenCountLinkTagNames_thenSuccess() {
        List<WebElement> linkTags = driver.findElements(By.tagName("a"));
        LOG.info(() -> "Total link tags: " + linkTags.size());
        linkTags.forEach(link -> LOG.info(link::getText));
        assertTrue(linkTags.size() == 373 || linkTags.size() == 375);
    }

    @Test
    public void givenWebElements_whenCountImageTagNames_thenSuccess() {
        List<WebElement> imageTags = driver.findElements(By.tagName("img"));
        LOG.info(() -> "Total image tags: " + imageTags.size());
        assertEquals(58, imageTags.size());
    }

    @Test
    public void givenWebElements_whenCountInputTagNames_thenSuccess() {
        List<WebElement> inputTags = driver.findElements(By.tagName("input"));
        LOG.info(() -> "Total input tags: " + inputTags.size());
        assertEquals(8, inputTags.size());
    }
    @Test
    public void givenWebElements_whenFindSingleNonExistingElement_thenThrowException() {
        // Disable implicit waits for this test
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));

        try {
            WebElement element = driver.findElement(By.id("afbrwweb2-"));
            fail("Expected NoSuchElementException was not thrown");
        } catch (NoSuchElementException e) {
            // Test passes if we get here
            LOG.info(()-> "Caught expected NoSuchElementException: " + e.getMessage());
        } catch (TimeoutException e) {
            // If we get the TimeoutException, it's likely due to the WebDriver configuration
            LOG.warn(() -> "Caught TimeoutException instead of NoSuchElementException. " +
                    "This might be due to implicit waits. " + e.getMessage());
            // Re-throw if you want the test to fail on TimeoutException
            throw new AssertionError("Expected NoSuchElementException but got TimeoutException", e);
        } finally {
            // Reset implicit wait to original value
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
    }
    @Test
    public void givenWebElements_whenFindMultipleNonExistingElements_thenReturnZero(){
        // Disable implicit waits for this test
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
        try {
            List<WebElement> elements = driver.findElements(By.id("afbrwweb2-"));
            LOG.info(() -> "Total elements: " + elements.size());
            assertEquals(0, elements.size());
        } finally {
            // Reset implicit wait to original value
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        }
    }
}