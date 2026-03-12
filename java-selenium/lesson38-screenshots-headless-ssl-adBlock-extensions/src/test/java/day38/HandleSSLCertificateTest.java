package day38;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@Slf4j
public class HandleSSLCertificateTest {

    private static final String URL  = "https://expired.badssl.com/";
    private static WebDriver driver;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        // Remove "Chrome is being controlled by automated test software" message
        options.setExperimentalOption("excludeSwitches", new String[] {"enable-automation"});

        // Accepting insecure certificates
        options.setAcceptInsecureCerts(true);

        // Open browser in incognito mode
        options.addArguments("--incognito");

        driver = new ChromeDriver(options);
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
    public void givenInsecureWebPage_whenGetTitle_thenVerifyTitle() throws InterruptedException {

        // Retrieve the title directly using the driver instance
        String title = driver.getTitle();
        log.info("Title: " + title);

        // Sleep for 2 seconds
        Thread.sleep(2000);

        // Retrieve the title using xpath
        WebElement webElement = driver.findElement(By.xpath("//h1[normalize-space()='expired.badssl.com']"));
        log.info("Title: " + webElement);

        // Sleep for 2 seconds
        Thread.sleep(2000);

        // Assert the title contains "expired"
        assertTrue(title.contains("expired"));

        // Assert the web element contains "expired"
        assertTrue(webElement.getText().contains("expired"));
    }
}
