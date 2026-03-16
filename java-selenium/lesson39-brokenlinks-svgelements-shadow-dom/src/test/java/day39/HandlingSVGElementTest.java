package day39;

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

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@Slf4j
public class HandlingSVGElementTest {

    private static final String URL  = "https://testautomationpractice.blogspot.com/";
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
    void givenWebPage_whenTestSVGElement_thenValidate() {
        WebElement svgElement = driver.findElement(By.xpath("//*[name()='circle' and contains(@cx,'15')]"));

        // Check if the SVG element is displayed
        boolean isDisplayed = svgElement.isDisplayed();
        log.info("SVG Element is displayed: {}", isDisplayed);
        assertTrue(isDisplayed, "SVG Element should be displayed");

        // Check if the SVG element is enabled
        boolean isEnabled = svgElement.isEnabled();
        log.info("SVG Element is enabled: {}", isEnabled);
        assertTrue(isEnabled, "SVG Element should be enabled");

        // Check if the SVG element is clickable
        boolean isClickable = svgElement.isDisplayed() && svgElement.isEnabled();
        log.info("SVG Element is clickable: {}", isClickable);
        assertTrue(isClickable, "SVG Element should be clickable");

        // Check if the SVG element is selected
        boolean isSelected = svgElement.isSelected();
        log.info("SVG Element is selected: {}", isSelected);
        assertFalse(isSelected, "SVG Element should be selected");
    }
}
