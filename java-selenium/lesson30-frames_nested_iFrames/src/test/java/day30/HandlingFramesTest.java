package day30;

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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class HandlingFramesTest {
    private static final String ORANGEHRM_URL = "https://ui.vision/demo/webtest/frames/";
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(HandlingFramesTest.class);

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
    public void givenWebElements_WhenSwitchToFrame1_thenSuccess() {
        // Find and switch to Frame 1
        WebElement frame1 = driver.findElement(By.xpath("//frame[@src='frame_1.html']"));
        driver.switchTo().frame(frame1);

        // Now find and interact with elements inside the frame
        WebElement inputField = driver.findElement(By.xpath("//input[@name='mytext1']"));
        String inputText = "Welcome to frame 1";
        inputField.sendKeys(inputText);

        String attribute = inputField.getAttribute("value");
        assertEquals("Welcome to frame 1", attribute);
        LOG.info("Frame 1 input text: {} ", attribute);

        // Switch back to default content to interact with elements outside the frame
        driver.switchTo().defaultContent();
    }

    @Test
    public void givenWebElements_WhenSwitchToFrame2_thenSuccess() {
        // Find and switch to Frame 2
        WebElement frame2 = driver.findElement(By.xpath("//frame[@src='frame_2.html']"));
        driver.switchTo().frame(frame2);

        // Now find and interact with elements inside the frame
        WebElement inputField = driver.findElement(By.xpath("//input[@name='mytext2']"));
        String inputText = "Welcome to frame 2";
        inputField.sendKeys(inputText);

        String attribute = inputField.getAttribute("value");
        assertEquals("Welcome to frame 2", attribute);
        LOG.info("Frame 2 input text: {} ", attribute);

        // Switch back to default content to interact with elements outside the frame
        driver.switchTo().defaultContent();
    }

    @Test
    public void givenWebElements_WhenSwitchToFrame3_thenSuccess() {
        // Find and switch to Frame 2
        WebElement frame3 = driver.findElement(By.xpath("//frame[@src='frame_3.html']"));
        driver.switchTo().frame(frame3);

        // Now find and interact with elements inside the frame
        WebElement inputField = driver.findElement(By.xpath("//input[@name='mytext3']"));
        String inputText = "Welcome to frame 3";
        inputField.sendKeys(inputText);

        String attribute = inputField.getAttribute("value");
        assertEquals("Welcome to frame 3", attribute);
        LOG.info("Frame 3 input text: {} ", attribute);

        // Switch back to default content to interact with elements outside the frame
        driver.switchTo().defaultContent();
    }

    @Test
    public void givenWebElements_WhenSwitchToFrame4_thenSuccess() {
        // Find and switch to Frame 4
        WebElement frame4 = driver.findElement(By.xpath("//frame[@src='frame_4.html']"));
        driver.switchTo().frame(frame4);

        // Now find and interact with elements inside the frame
        WebElement inputField = driver.findElement(By.xpath("//input[@name='mytext4']"));
        String inputText = "Welcome to frame 4";
        inputField.sendKeys(inputText);

        String attribute = inputField.getAttribute("value");
        assertEquals("Welcome to frame 4", attribute);
        LOG.info("Frame 4 input text: {} ", attribute);

        // Switch back to default content to interact with elements outside the frame
        driver.switchTo().defaultContent();
    }

    @Test
    public void givenWebElements_WhenSwitchToFrame5_thenSuccess() {
        // Find and switch to Frame 5
        WebElement frame5 = driver.findElement(By.xpath("//frame[@src='frame_5.html']"));
        driver.switchTo().frame(frame5);

        // Now find and interact with elements inside the frame
        WebElement inputField = driver.findElement(By.xpath("//input[@name='mytext5']"));
        String inputText = "Welcome to frame 5";
        inputField.sendKeys(inputText);

        String attribute = inputField.getAttribute("value");
        assertEquals("Welcome to frame 5", attribute);
        LOG.info("Frame 5 input text: {} ", attribute);

        // Switch back to default content to interact with elements outside the frame
        driver.switchTo().defaultContent();
    }

    @Test
    public void givenWebElements_WhenSwitchToFrameByIndex_thenCheckRadioButton() {
        try{
            driver.switchTo().frame(0);

            // Now find and interact with elements inside the frame
            WebElement radioButton = driver.findElement(By.xpath("//div[@id='i9']//div[@class='rseUEf nQOrEb']"));
            radioButton.click();
            if(radioButton.isSelected()) {
                String radioButtonValue = radioButton.getAttribute("value");
                assertEquals("I am a human", radioButtonValue);
                assertTrue(radioButton.isSelected());
                LOG.info("Radio button attribute value: {}", radioButtonValue);
                LOG.info("Radio button is selected: {}", radioButton.isSelected());
            }else {
                LOG.error("Radio button is not selected");
            }
        } catch (Exception e) {
            LOG.error("Exception occurred: {}", e.getMessage());
        }
        // Switch back to default content to interact with elements outside the frame
        driver.switchTo().defaultContent();
    }
}
