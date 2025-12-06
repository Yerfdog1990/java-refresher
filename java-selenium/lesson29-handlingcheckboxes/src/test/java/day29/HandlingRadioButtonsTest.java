package day29;

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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class HandlingRadioButtonsTest {
    private static final String ORANGEHRM_URL = "https://testautomationpractice.blogspot.com/";
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(HandlingRadioButtonsTest.class);

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

    // Select a radio button
    @Test
    void givenWebElements_whenSelectRadioButton_thenVerifyRadioButtonIsSelected() {
        try {
            WebElement radioButton = driver.findElement(By.xpath("//input[@id='female']"));
            radioButton.click();
            if(radioButton.isSelected()) {
                String radioButtonValue = radioButton.getAttribute("value");
                assertEquals("female", radioButtonValue);
                LOG.info("Radio button attribute value: {}", radioButtonValue);
                LOG.info("Radio button is selected: {}", radioButton.isSelected());
            }else {
                LOG.error("Radio button is not selected");
            }
        } catch (Exception e) {
            LOG.error("Exception occurred: {}", e.getMessage());
        }
    }
    // Get a list of radio buttons
    @Test
    void givenWebElements_whenGetListOfRadioButtons_thenVerifyNumberOfRadioButtons() {
        List<WebElement> RadioButtonsList = driver.findElements(By.xpath("//input[@type='radio']"));
        int numberOfRadioButtons = RadioButtonsList.size();
        LOG.info("Total number of radio buttons: {}", numberOfRadioButtons);
        assertTrue(numberOfRadioButtons > 0);

        // print radio buttons
        for (WebElement radioButton : RadioButtonsList) {
            LOG.info("Radio button value: {}", radioButton.getAttribute("value"));
        }
    }
}
