package day26;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.logging.Logs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class WebBrowserCommandTest {
    private static URL url;
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(WebBrowserCommandTest.class);

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void givenUrl_whenNavigateTo_thenVerifyPageTitle() throws MalformedURLException {
        url = new URL("https://www.orangehrm.com/");
        driver.navigate().to(url);
        String title = driver.getTitle();
        LOG.info("Page title: {}", title);
        assertTrue(title.contains("OrangeHRM"));
    }

    @Test
    public void givenUrl_whenNavigateBack_thenVerifyPageTitle() throws MalformedURLException {
        url = new URL("https://www.orangehrm.com/");
        driver.navigate().to(url);
        String title = driver.getTitle();
        driver.navigate().back();
        LOG.info("Page title: {}", title);
        assertTrue(title.contains("OrangeHRM"));
    }

    @Test
    public void givenUrl_whenNavigateForward_thenVerifyPageTitle() throws MalformedURLException {
        url = new URL("https://www.orangehrm.com/en/company/about-us#");
        driver.navigate().to(url);
        driver.navigate().forward();
        String title = driver.getTitle();
        LOG.info("Page title: {}", title);
        assertTrue(title.contains("OrangeHRM"));
    }

    @Test
    public void givenUrl_whenRefreshPage_thenVerifyPageTitle() throws MalformedURLException {
        url = new URL("https://www.orangehrm.com/");
        driver.navigate().to(url);
        String beforeRefresh = driver.getTitle();
        driver.navigate().refresh();
        String afterRefresh = driver.getTitle();
        LOG.info("Page title before refresh: {}", beforeRefresh);
        LOG.info("Page title after refresh: {}", afterRefresh);
        assertEquals(beforeRefresh, afterRefresh);
    }

    @Test
    public void givenUrl_whenSwitchTo_thenVerifyPageTitle() throws MalformedURLException {
        url = new URL("https://www.orangehrm.com/");

        // navigate to the main page
        driver.navigate().to(url);
        String parentWindowTitle = driver.getTitle();
        // navigate to child page
        ((JavascriptExecutor) driver).executeScript("window.open('https://www.orangehrm.com/contact-sales/')");

        // retrieve all window handles
        Set<String> windowHandles = driver.getWindowHandles();
        LOG.info("Window handles: {}", windowHandles);
        List<String> windowHandlesList = new ArrayList<>(windowHandles);

        String parentWindowHandle = windowHandlesList.get(0);
        String childWindowHandle = windowHandlesList.get(1);

        // switch to the child window
        driver.switchTo().window(childWindowHandle);
        String childWindowTitle = driver.getTitle();
        LOG.info("Parent window title: {}", parentWindowTitle);
        LOG.info("Child window title: {}", childWindowTitle);
        assertNotEquals(parentWindowTitle, childWindowTitle);

        // Switch between windows using an enhanced for loop
        for (String handle : windowHandles) {
            String title = driver.switchTo().window(handle).getTitle();
            LOG.info("Window title: {}", title);
        }
    }

    @Test
    public void givenTwoWindows_closeSpecificWindow_thenVerify() throws MalformedURLException {
        url = new URL("https://www.orangehrm.com/");

        // navigate to the main page
        driver.navigate().to(url);
        String parentWindowTitle = driver.getTitle();
        // navigate to child page
        ((JavascriptExecutor) driver).executeScript("window.open('https://www.orangehrm.com/contact-sales/')");

        // retrieve all window handles
        Set<String> windowHandles = driver.getWindowHandles();
        LOG.info("Window handles: {}", windowHandles);

        // Switch between windows using an enhanced for loop
        for (String handle : windowHandles) {
            String title = driver.switchTo().window(handle).getTitle();
            LOG.info("Window title open: {}", title);
            if (title.contains("Contact Sales")) {
                driver.close();
                break;
            }
            LOG.info("Window title not closed: {}", title);
            assertFalse(title.contains("Contact Sales"));
        }
    }
}