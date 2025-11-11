package day22;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;


import java.time.Duration;

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
    public void givenUrl_whenGetTitle_thenSuccess() {
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
    public void givenUrl_whenFindElementByExistingName_thenSuccess() {
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
    public void givenUrl_whenFindElementByClassName_thenSuccess() {
        String className = "gh-header__logo-cats-wrap";
        WebElement element = driver.findElement(By.className(className));
        boolean isDisplayed = element.isDisplayed();
        LOG.info(() -> "Is logo displayed: " + isDisplayed);
        assertTrue(isDisplayed);
    }
}