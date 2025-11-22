package day24;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class ConditionalMethodsTest {
    private static final String ORANGEHRM_URL = "https://demo.nopcommerce.com/register?returnUrl=%2F";
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(ConditionalMethodsTest.class);

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

    // isDisplayed()
    @Test
    void givenWebElements_whenCheckIfSelected_returnTrue(){
        WebElement element = driver.findElement(By.xpath("//img[@alt='nopCommerce demo store']"));
        boolean isDisplayed = element.isDisplayed();
        LOG.info("Element is displayed: {}", isDisplayed);
        assertTrue(isDisplayed);
    }
    // isEnabled()
    @Test
    void givenWebElements_whenCheckIfEnabled_returnTrue(){
        WebElement element = driver.findElement(By.xpath("//input[@id='small-searchterms']"));
        String searchTerm = "MacBook";
        element.sendKeys(searchTerm);
        boolean isEnabled = element.isEnabled();
        LOG.info("Element is enabled: {}", isEnabled);
        assertTrue(isEnabled);
    }

    // isSelected()
    @Test
    void givenWebElements_whenCheckIfSelected_thenReturnTrue(){
        WebElement maleGenderButton = driver.findElement(By.cssSelector("#gender-male"));
        WebElement femaleGenderButton = driver.findElement(By.cssSelector("#gender-female"));
        WebElement newsletterButton = driver.findElement(By.xpath("//input[@id='NewsLetterSubscriptions_0__IsActive']"));
        maleGenderButton.click();

        boolean isMaleSelected = maleGenderButton.isSelected();
        boolean isFemaleSelected = femaleGenderButton.isSelected();
        boolean isNewsletterSelected = newsletterButton.isSelected();

        LOG.info("Element is selected: {}", isMaleSelected);
        LOG.info("Element is selected: {}", isFemaleSelected);
        LOG.info("Newsletter is selected: {}", isNewsletterSelected);

        assertTrue(isMaleSelected);
        assertFalse(isFemaleSelected);
        assertTrue(isNewsletterSelected);
    }

}
