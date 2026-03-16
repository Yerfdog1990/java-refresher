package day39;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@Slf4j
public class HandlingShadowDOMTest {

    private static final String URL  = "https://books-pwakit.appspot.com/explore?q=";
    private static WebDriver driver;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Setting headless mode


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
    void givenShadowDOM_whenInteractWithElement_thenValidateInput() throws InterruptedException {

        // 1. Find the shadow root
        SearchContext shadow = driver.findElement(By.cssSelector("book-app[apptitle='BOOKS']")).getShadowRoot();
        Thread.sleep(1000);

        // 2. Find the element inside the shadow root
        WebElement shadowElement = shadow.findElement(By.cssSelector("#input"));

        // 3. Interact with the element
        shadowElement.sendKeys("test");

        // 4. Validate the text input
        String value = shadowElement.getAttribute("value");
        assertThat(value).isEqualTo("test");
    }
}
