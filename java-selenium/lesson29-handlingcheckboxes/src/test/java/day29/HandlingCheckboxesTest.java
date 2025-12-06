package day29;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class HandlingCheckboxesTest {
    private static final String ORANGEHRM_URL = "https://testautomationpractice.blogspot.com/";
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(HandlingCheckboxesTest.class);

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


    // Select one checkbox
    @Test
    void givenWebElements_whenSelectOneCheckbox_thenVerifyCheckboxIsSelected() {
        WebElement checkbox = driver.findElement(By.xpath("//input[@id='male']"));
        checkbox.click();
        assertTrue(checkbox.isSelected());
        String checkboxValue = checkbox.getAttribute("value");
        assertEquals("male", checkboxValue);
        LOG.info("Checkbox attribute value: {}", checkboxValue);
    }

    // Select multiple checkboxes
    @Test
    void givenWebElements_whenSelectMultipleCheckboxes_thenVerifyAllCheckboxesAreSelected() {
        List<WebElement> checkboxes = driver.findElements(By.xpath("//input[@class='form-check-input' and @type='checkbox']"));
        for (WebElement checkbox : checkboxes) {
            checkbox.click();
            LOG.info("Checkbox is selected: {}", checkbox.isSelected());
            LOG.info("Checkbox value: {}", checkbox.getAttribute("value"));
        }
        for (WebElement checkbox : checkboxes) {
            assertTrue(checkbox.isSelected());
        }
        LOG.info("Total number of checkboxes selected: {}", checkboxes.size());
        assertEquals(7, checkboxes.size());
    }

    // Select the first three checkboxes
    @Test
    void givenWebElements_whenSelectFirstThreeCheckboxes_thenVerifyFirstThreeCheckboxesAreSelected() {
        List<WebElement> checkboxes = driver.findElements(By.xpath("//input[@class='form-check-input' and @type='checkbox']"));
        int sum = 0;
        for (int i = 0; i < 3; i++) {
            checkboxes.get(i).click();
            sum = sum + 1;
            assertTrue(checkboxes.get(i).isSelected());
            LOG.info("Checkbox is selected: {}", checkboxes.get(i).isSelected());
            LOG.info("Checkbox value: {}", checkboxes.get(i).getAttribute("value"));
        }
        LOG.info("Total number of checkboxes selected: {}", sum);
        assertEquals(3, sum);
    }
    // Select the last three checkboxes
    @Test
    void givenWebElements_whenSelectLastThreeCheckboxes_thenVerifyLastThreeCheckboxesAreSelected() {
        List<WebElement> checkboxes = driver.findElements(By.xpath("//input[@class='form-check-input' and @type='checkbox']"));
        int sum = 0;
        for (int i = 4; i < checkboxes.size(); i++) {
            checkboxes.get(i).click();
            sum = sum + 1;
            assertTrue(checkboxes.get(i).isSelected());
            LOG.info("Checkbox value: {}", checkboxes.get(i).getAttribute("value"));
            LOG.info("Checkbox is selected: {}", checkboxes.get(i).isSelected());
        }
        LOG.info("Total number of checkboxes selected: {}", sum);
        assertEquals(3, sum);
    }

    // Unselect checkboxes
    @Test
    void givenCheckboxes_whenUnselectingAll_thenNoneShouldBeSelected() {
        // Uncheck all checkboxes
        uncheckAllCheckboxes();

        // Verify no checkboxes are selected
        long selectedCount = driver.findElements(
                By.xpath("//input[@type='checkbox' and @class='form-check-input']")
        ).stream().filter(WebElement::isSelected).count();

        assertEquals(0, selectedCount, "No checkboxes should be selected");
    }

    // Uncheck all checkboxes
    private void uncheckAllCheckboxes() {
        List<WebElement> checkboxes = driver.findElements(
                By.xpath("//input[@type='checkbox' and @class='form-check-input']")
        );

        for (WebElement checkbox : checkboxes) {
            if (checkbox.isSelected()) {
                checkbox.click();
            }
        }
    }

}