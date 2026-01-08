package day30;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class HiddenDropDownTest {
    private static final String ORANGEHRM_URL  = "https://opensource-demo.orangehrmlive.com/web/index.php/auth/login";

    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(HiddenDropDownTest.class);

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(ORANGEHRM_URL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Login steps
        driver.findElement(By.name("username")).sendKeys("Admin");
        driver.findElement(By.name("password")).sendKeys("admin123");
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // Clicking on PIM
        driver.findElement(By.xpath("//span[normalize-space()='PIM']")).click();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void givenHiddenDropDown_whenInspectAllOptions_thenSuccess() {

        // Locate the "Employment Status" dropdown and click it to reveal options
        WebElement webElement = driver.findElement(By.xpath("//label[text()='Employment Status']/parent::div/following-sibling::div//div[@class='oxd-select-text-input']"));
        webElement.click();

        List<WebElement> webElementList = driver.findElements(By.xpath("//div[@role='listbox']//span"));
        LOG.info("Total options found: {}", webElementList.size());

        for (WebElement element : webElementList) {
            LOG.info("Option: {}", element.getText());
        }

    }
        @Test
    public void givenHiddenDropDown_whenInspectSingleOption_thenSuccess() {

        // Locate the "Employment Status" dropdown and click it
        WebElement dropdown = driver.findElement(By.xpath("//label[text()='Employment Status']/parent::div/following-sibling::div//div[@class='oxd-select-text-input']"));
        dropdown.click();

        // Locate the "Freelance" option and click it
        WebElement freelanceOption = driver.findElement(By.xpath("//div[@role='listbox']//span[text()='Freelance']"));
        freelanceOption.click();

        // Verify the selection
        String selectedValue = dropdown.getText();
        assertEquals("Freelance", selectedValue, "Selected value is as expected");
        LOG.info("Selected value: {}", selectedValue);
    }
}
