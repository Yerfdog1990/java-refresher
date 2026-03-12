package day38;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.time.Duration;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class EnableBrowserExtensionTest {

    private static final String URL  = "https://text-compare.com/";
    private static WebDriver driver;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        
        // Note: Extensions do NOT work in Headless mode
        // Ensure the path is correct relative to the working directory (e.g., the module root)
        File selectorsHubExtension = new File("/Users/godfrey/IdeaProjects/Baeldung-boot-camp/java-refresher/java-selenium/lesson38-screenshots-headless-ssl-adBlock-extensions/crx/SelectorsHub-Chrome-Web-Store.crx");
        options.addExtensions(selectorsHubExtension);

        // Add uBlock Origin extension
        File uBlockOriginExtension = new File("/Users/godfrey/IdeaProjects/Baeldung-boot-camp/java-refresher/java-selenium/lesson38-screenshots-headless-ssl-adBlock-extensions/crx/uBlock-Origin-Lite-Chrome-Web-Store.crx");
        options.addExtensions(uBlockOriginExtension);

        driver = new ChromeDriver(options); // Passing options to the driver
        driver.manage().window().maximize();
        driver.get(URL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if(driver != null){
            driver.close();
        }
    }

    @Test
    public void givenWebPage_whenEnableBrowserExtension_thenVerifyExtensionIsEnabled() throws InterruptedException {
        // Extensions generally add a specific element to the page or are visible in chrome://extensions/
        // For demonstration, let's wait a bit to observe
        Thread.sleep(5000); 
    }
}
