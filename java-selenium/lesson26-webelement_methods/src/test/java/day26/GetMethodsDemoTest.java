package day26;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class GetMethodsDemoTest {
    private static final String ORANGEHRM_URL = "https://www.orangehrm.com/";
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(GetMethodsDemoTest.class);

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

    @Test
    void givenWebElements_whenGetPageTitle_thenVerifyTitle() {
        String title = driver.getTitle();
        LOG.info("Page title: {}", title);
        assertTrue(title.contains("OrangeHRM"));
    }

    @Test
    void givenWebElements_whenGetCurrentUrl_thenVerifyUrl() {
        String currentUrl = driver.getCurrentUrl();
        LOG.info("Current URL: {}", currentUrl);
        assertTrue(currentUrl.contains("orangehrm"));
    }

    @Test
    void givenWebElements_whenGetPageSource_thenVerifySource() {
        String pageSource = driver.getPageSource();
        assertTrue(pageSource.contains("OrangeHRM"));
    }

    @Test
    void givenWebElements_whenGetWindowHandle_thenVerifyHandle() {
        String windowHandle = driver.getWindowHandle();
        LOG.info("Window handle: {}", windowHandle);
        assertNotNull(windowHandle);
    }

    @Test
    void givenWebElements_whenGetWindowHandles_thenVerifyHandles() {
        // Store main window handle
        String mainWindow = driver.getWindowHandle();

        // Open a new window
        ((JavascriptExecutor) driver).executeScript("window.open('https://www.orangehrm.com/contact-sales/')");

        // Get all window handles
        Set<String> handles = driver.getWindowHandles();
        LOG.info("Window handles: {}", handles);

        // Switch to a new window and verify
        for (String handle : handles) {
            if (!handle.equals(mainWindow)) {
                driver.switchTo().window(handle);
                assertTrue(driver.getCurrentUrl().contains("contact-sales"));
                driver.close();
            }
        }

        // Switch back to the main window
        driver.switchTo().window(mainWindow);
        assertTrue(driver.getCurrentUrl().equals(ORANGEHRM_URL));
    }

    @Test
    void givenWebElements_whenGetAttribute_thenVerifyAttribute() {
        WebElement contactSalesLink = driver.findElement(By.linkText("Contact Sales"));
        String href = contactSalesLink.getAttribute("href");
        LOG.info("Href attribute: {}", href);
        assertTrue(href.contains("contact-sales"));
    }

    @Test
    void givenWebElements_whenGetText_thenVerifyText() {
        WebElement element = driver.findElement(By.xpath("//button[@class='footer-btn']//a[contains(text(),'Contact Sales')]"));
        String text = element.getText();
        LOG.info("Element text: {}", text);
        assertTrue(text.contains("Contact Sales"));
    }

    @Test
    void givenWebElements_whenGetTagName_thenVerifyTagName() {
        WebElement element = driver.findElement(By.xpath("//h1"));
        String tagName = element.getTagName();
        LOG.info("Tag name: {}", tagName);
        assertEquals("h1", tagName.toLowerCase());
    }

    @Test
    void givenWebElements_whenGetCssValue_thenVerifyCssValue() {
        WebElement element = driver.findElement(By.xpath("//h1"));
        String color = element.getCssValue("color");
        String fontSize = element.getCssValue("font-size");
        LOG.info("Color: {}, Font Size: {}", color, fontSize);
        assertNotNull(color);
        assertNotNull(fontSize);
    }

    @Test
    void givenWebElements_whenGetSize_thenVerifySize() {
        WebElement element = driver.findElement(By.xpath("//h1"));
        Dimension size = element.getSize();
        LOG.info("Element size - Width: {}, Height: {}", size.getWidth(), size.getHeight());
        assertTrue(size.getWidth() > 0);
        assertTrue(size.getHeight() > 0);
    }

    @Test
    void givenWebElements_whenGetLocation_thenVerifyLocation() {
        WebElement element = driver.findElement(By.xpath("//h1"));
        Point location = element.getLocation();
        LOG.info("Element location - X: {}, Y: {}", location.getX(), location.getY());
        assertTrue(location.getX() >= 0);
        assertTrue(location.getY() >= 0);
    }

    @Test
    void givenWebElements_whenGetRect_thenVerifyRect() {
        WebElement element = driver.findElement(By.xpath("//h1"));
        Rectangle rect = element.getRect();
        LOG.info("Element rect - X: {}, Y: {}, Width: {}, Height: {}",
                rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
        assertTrue(rect.getWidth() > 0);
        assertTrue(rect.getHeight() > 0);
    }

    @Test
    void givenWebElements_whenIsDisplayed_thenVerifyVisibility() {
        WebElement element = driver.findElement(By.xpath("//h1"));
        boolean isDisplayed = element.isDisplayed();
        LOG.info("Is element displayed: {}", isDisplayed);
        assertTrue(isDisplayed);
    }

    @Test
    void givenWebElements_whenIsEnabled_thenVerifyEnabledState() {
        WebElement element = driver.findElement(By.xpath("//a[contains(text(),'Contact Sales')]"));
        boolean isEnabled = element.isEnabled();
        LOG.info("Is element enabled: {}", isEnabled);
        assertTrue(isEnabled);
    }

    @Test
    void givenWebElements_whenGetCssValues_thenVerifyMultipleProperties() {
        WebElement element = driver.findElement(By.xpath("//h1"));
        String color = element.getCssValue("color");
        String fontFamily = element.getCssValue("font-family");
        String textAlign = element.getCssValue("text-align");

        LOG.info("Color: {}", color);
        LOG.info("Font Family: {}", fontFamily);
        LOG.info("Text Align: {}", textAlign);

        assertNotNull(color);
        assertNotNull(fontFamily);
        assertNotNull(textAlign);
    }

    @Test
    void givenWebElements_whenHoverAndGetCssValue_thenVerifyHoverState() {
        WebElement element = driver.findElement(By.xpath("//a[contains(text(),'Contact Sales')]"));

        // Get initial color
        String initialColor = element.getCssValue("color");

        // Hover over the element
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();

        // Get color after hover
        String hoverColor = element.getCssValue("color");

        LOG.info("Initial color: {}, Hover color: {}", initialColor, hoverColor);

        // Note: Some sites might not change color on hover
        assertNotNull(initialColor);
        assertNotNull(hoverColor);
    }

    @Test
    void givenWebElements_whenFindMultipleElements_thenVerifyCount() {
        List<WebElement> links = driver.findElements(By.tagName("a"));
        LOG.info("Number of links on page: {}", links.size());
        assertTrue(links.size() > 0);
    }
}