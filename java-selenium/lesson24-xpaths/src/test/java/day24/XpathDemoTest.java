package day24;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class XpathDemoTest {
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(XpathDemoTest.class);
    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://demo.nopcommerce.com/");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void givenStaticWebElement_whenFindXpathWithSingleAttribute_thenVerify(){
        WebElement element = driver.findElement(By.xpath("//input[@id='small-searchterms']"));
        String searchTerm = "MacBook";
        element.sendKeys(searchTerm);

        String searchResult = element.getAttribute("value");
        LOG.info(()->"Search Result: " + searchResult);
        assertEquals(searchTerm, searchResult);
    }

    @Test
    public void givenStaticWebElement_whenFindXpathWithMultipleAttributes_thenVerify(){
        WebElement element = driver.findElement(By.xpath("//input[@id='small-searchterms'][@type='text']"));
        String searchTerm = "Iphone";
        element.sendKeys(searchTerm);

        String searchResult = element.getAttribute("value");
        LOG.info(()->"Search Result: " + searchResult);
        assertEquals(searchTerm, searchResult);
    }
    @Test
    public void givenStaticWebElement_whenFindXpathWithMultipleAttributesByOrOperator_thenSuccess(){
        WebElement element = driver.findElement(By.xpath("//input[@id='small-searchterms' or @type='text']"));
        String searchTerm = "Android";
        element.sendKeys(searchTerm);

        String searchResult = element.getAttribute("value");
        LOG.info(()->"Search Result: " + searchResult);
        assertEquals(searchTerm, searchResult);
    }
    @Test
    public void givenStaticWebElement_whenFindXpathWithMultipleAttributesByAndOperator_thenSuccess(){
        WebElement element = driver.findElement(By.xpath("//input[@id='small-searchterms' and @type='text']"));
        String searchTerm = "Laptop";
        element.sendKeys(searchTerm);

        String searchResult = element.getAttribute("value");
        LOG.info(()->"Search Result: " + searchResult);
        assertEquals(searchTerm, searchResult);
    }
    @Test
    public void givenStaticWebElement_whenFindXpathWithMultipleAttributesByNotOperator_thenSuccess(){
        WebElement element = driver.findElement(By.xpath("//input[@id='small-searchterms' and not(@type='wrong-attribute')]"));
        String searchTerm = "Desktop";
        element.sendKeys(searchTerm);

        String searchResult = element.getAttribute("value");
        LOG.info(()->"Search Result: " + searchResult);
        assertEquals(searchTerm, searchResult);
    }

    @Test
    public void givenStaticWebElement_whenFindXpathWithLinkText_thenSuccess(){
        WebElement element = driver.findElement(By.xpath("//div[@class='footer-powered-by']/a[text()='nopCommerce']"));
        String linkText = "nopCommerce";
        boolean isDisplayed = element.isDisplayed();
        LOG.info(()->"Is link text displayed: " + isDisplayed);
        assertEquals(linkText, element.getText());
    }

    @Test
    public void givenStaticWebElement_whenFindXpathWithInnerText_thenSuccess(){
        WebElement element = driver.findElement(By.xpath("//a[normalize-space()='Electronics']"));
        String innerText = "Electronics";
        boolean isDisplayed = element.isDisplayed();
        LOG.info(()->"Is inner text displayed: " + isDisplayed);
        assertEquals(innerText, element.getText());
    }

    @Test
    public void givenStaticWebElement_whenFindXpathByTextContainsMethod_thenSuccess(){
        WebElement element = driver.findElement(By.xpath("//input[contains(@placeholder, 'Sea')]"));
        String searchTerm = "Smartphone";
        element.sendKeys(searchTerm);

        String searchResult = element.getAttribute("value");
        LOG.info(()->"Search Result: " + searchResult);
        assertEquals(searchTerm, searchResult);
    }

    @Test
    public void givenStaticWebElement_whenFindXpathByTextStartsWithMethod_thenSuccess(){
        WebElement element = driver.findElement(By.xpath("//img[starts-with(@title, 'Show')]"));
        boolean isDisplayed = element.isDisplayed();
        LOG.info(()->"Is the image displayed: " + isDisplayed);
        assertTrue(isDisplayed);
    }
}
