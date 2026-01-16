package day32;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class AutoSuggestDropDownTest {
    private static final String DROPDOWN_URL  = "https://www.google.com/";

    private static WebDriver driver;

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
    public void givenGoogleSearchBox_whenFindNumberOfSearchSuggestionResults_thenSuccess() throws InterruptedException {
        WebElement element = driver.findElement(By.xpath("//textarea[@id='APjFqb']"));

        // Send search text to the search box
        String searchText = "Selenium";
        element.sendKeys(searchText);

        // Handle search suggestions results
        List<WebElement> searchList = driver.findElements(By.xpath("//ul[@role='listbox']//li[@role='presentation']//div[@role='option']"));

        // Check number search result suggestion
        int size = searchList.size();
       log.info("Total options found: {}", size);
        assertEquals(10, size);
    }

    @Test
    public void givenGoogleSearchBox_whenPrintSearchSuggestionResults_thenSuccess() throws InterruptedException {
        WebElement element = driver.findElement(By.xpath("//textarea[@id='APjFqb']"));

        // Send search text to the search box
        String searchText = "selenium";
        element.sendKeys(searchText);

        // Handle search suggestions results
        List<WebElement> searchList = driver.findElements(By.xpath("//ul[@role='listbox']//li[@role='presentation']//div[@role='option']"));

        // Print search suggestion reuslts
        for (WebElement text: searchList) {
            log.info("Search text: {}", text.getText());

            String resultText = text.getText().contains("selenium") ? "selenium" : text.getText();
            assertEquals(searchText, resultText);
        }
    }
}
