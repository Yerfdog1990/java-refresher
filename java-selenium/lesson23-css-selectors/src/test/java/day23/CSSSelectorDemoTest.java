package day23;

import org.junit.jupiter.api.*;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class CSSSelectorDemoTest {
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(CSSSelectorDemoTest.class);

    @BeforeEach
    public void setup() {
        driver = new SafariDriver();
        driver.manage().window().maximize();
        driver.get("https://demo.nopcommerce.com/");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void givenWebElements_whenFindTagIdByCssSelector_thenVerifyElementFound() {
        String searchTerm = "Iphone";
        //WebElement element = driver.findElement(By.cssSelector("input#small-searchterms"));
        WebElement element = driver.findElement(By.cssSelector("#small-searchterms")); // The tag name can be ignored and use Id only but ensure # is included
        element.sendKeys(searchTerm);
        element.submit();

        String searchResult = element.getAttribute("value");

        LOG.info(() -> "Search result: " + searchResult);

        assertEquals(searchTerm, searchResult);
    }
    @Test
    public void givenWebElements_whenFindTagClassByCssSelector_thenVerifyElementFound() {
        WebElement element = driver.findElement(By.cssSelector("input.search-box-text"));
        String searchTerm = "MacBook";
        element.sendKeys(searchTerm);
        element.submit();
        String searchResult = element.getAttribute("value");
        assertEquals(searchTerm, searchResult);
        LOG.info(() -> "Search result: " + searchResult);
    }
    @Test
    public void givenWebElements_whenFindTagAttributeByCssSelector_thenVerifyElementFound() {
        WebElement element = driver.findElement(By.cssSelector("img[alt='Picture for category Electronics']"));
        boolean isDisplayed = element.isDisplayed();
        LOG.info(() -> "Is image displayed: " + isDisplayed);
        assertTrue(isDisplayed);
    }
    @Test
    public void givenWebElements_whenFindClassAttributeByCssSelector_thenVerifyElementFound() {
        WebElement element = driver.findElement(By.cssSelector("input.search-box-text[type='text']"));
        element.sendKeys("MacBook Pro");
        element.submit();

        String searchResult = element.getAttribute("value");
        assertEquals("MacBook Pro", searchResult);
        LOG.info(() -> "Search result: " + searchResult);
    }
}
