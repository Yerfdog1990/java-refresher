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
public class SelectDropDownTest {
    private static final String DROPDOWN_URL  = "https://practice.expandtesting.com/dropdown";
  
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(SelectDropDownTest.class);

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(DROPDOWN_URL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void givenSelectDropDown_whenSelectByVisibleText_thenSuccess() {
        WebElement dropDown = driver.findElement(By.xpath("//select[@id='dropdown']"));

        Select select = new Select(dropDown);
                select.selectByVisibleText("Option 1");

                String selectedValue = select.getFirstSelectedOption().getText();
                LOG.info("Selected visible text: {}", selectedValue);
                assertEquals("Option 1", selectedValue, "Selected value is as expected");

    }
    @Test
    public void givenSelectDropDown_whenSelectByValue_thenSuccess() {
        WebElement dropDown = driver.findElement(By.xpath("//select[@id='country']"));

        Select select = new Select(dropDown);
        select.selectByValue("AL");
        String selectedValue = select.getFirstSelectedOption().getText();
        LOG.info("Selected value: {}", selectedValue);
        assertEquals("Albania", selectedValue, "Selected value is as expected");
    }

    @Test
    public void givenSelectDropDown_whenSelectByIndex_thenSuccess() {
        WebElement dropDown = driver.findElement(By.xpath("//select[@id='country']"));

        Select select = new Select(dropDown);
        select.selectByIndex(4);
        String selectedValue = select.getFirstSelectedOption().getText();
        LOG.info("Selected index: {}", selectedValue);
        assertEquals("Algeria", selectedValue, "Selected value is as expected");
    }
    @Test
    public void givenSelectDropDown_whenCountNumberOfDropDownOptions_thenSuccess() {
        WebElement dropDown = driver.findElement(By.xpath("//select[@id='country']"));

        Select select = new Select(dropDown);
        int numberOfOptions = select.getOptions().size();
        LOG.info("Number of options: {}", numberOfOptions);
        assertEquals(252, numberOfOptions, "Number of options is as expected");
    }

    @Test
    public void givenSelectDropDown_whenPrintAllOptions_thenSuccess() {
        WebElement dropDown = driver.findElement(By.xpath("//select[@id='country']"));

        Select select = new Select(dropDown);
        List<WebElement> elementList = select.getOptions();
        select.getOptions().forEach(option -> LOG.info("Option: {}", option.getText()));

        select.getOptions().forEach(option -> LOG.info("Value: {}", option.getAttribute("value")));

        // Test the number of options
        assertEquals(252, elementList.size(), "Number of options is as expected");

        // Test the first option is "Select country"
        assertEquals("Select country", elementList.getFirst().getText(), "First option is as expected");

        // Test the last option is "Zimbabwe"
        assertEquals("Zimbabwe", elementList.getLast().getText(), "Last option is as expected");

        // Test the first value is "null"
        assertEquals("", elementList.getFirst().getAttribute("value"), "First value is as expected");

        // Test the last value is "ZW"
        assertEquals("ZW", elementList.getLast().getAttribute("value"), "Last value is as expected");
    }

    @Test
    public void givenSelectDropDown_whenSelectSingleOption_thenSuccess() {
        // First locate the select element
        WebElement countrySelect = driver.findElement(By.id("country"));
        Select select = new Select(countrySelect);

        // Then select by index, value, or visible text
        select.selectByIndex(3);  // 0-based index, so 3 is the 4th option
        // OR
        // select.selectByValue("AL");  // If the option has a value attribute
        // OR
        // select.selectByVisibleText("Albania");  // By the displayed text

        String selectedValue = select.getFirstSelectedOption().getText();
        LOG.info("The selected value: {}", selectedValue);
        assertEquals("Albania", selectedValue, "Selected value is as expected");
    }
}
