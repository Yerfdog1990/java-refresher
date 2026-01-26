package day32;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
public class HandleWebTablesTest {
    private static final String WEB_TABLE_URL1 = "https://testautomationpractice.blogspot.com/";

    private static WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(WEB_TABLE_URL1);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void givenStaticTable_whenInspectRows_thenSuccess() {
        List<WebElement> rowList = driver.findElements(By.xpath("//table[@name='BookTable']//tr"));

        // Print the total number of rows
        log.info("Total number of rows= {}", rowList.size());

        assertThat(rowList.size()).isEqualTo(7);
    }

    @Test
    public void givenStaticTable_whenInspectColumns_thenSuccess() {
        List<WebElement> columnList = driver.findElements(By.xpath("//table[@name='BookTable']//tr[1]/th"));

        // Print the total number of rows
        log.info("Total number of columns = {}", columnList.size());

        assertThat(columnList.size()).isEqualTo(4);
    }

    @Test
    public void givenStaticTable_whenReadSingleData_thenSuccess() {
        WebElement data = driver.findElement(By.xpath("//table[@name='BookTable']//tr[2]//td[1]"));

        log.info("Data in 3rd row, 1st column = {}", data.getText());

        // Character sequence test
        assertThat(data.getText().contentEquals("Learn Selenium"));

        // Text content test
        assertThat(data.getText().contains("Learn Selenium"));

        // Compare text content test considering the case
        assertThat(data.getText().compareTo("Learn Selenium"));

        // Compare text content test ignoring the case
        assertThat(data.getText().compareToIgnoreCase("learn selenium"));

    }

    @Test
    public void givenStaticTable_whenReadMultipleData_thenSuccess() {
        // Rows
        List<WebElement> rowList = driver.findElements(By.xpath("//table[@name='BookTable']//tr"));

        // Columns
        List<WebElement> columnList = driver.findElements(By.xpath("//table[@name='BookTable']//th"));

        for (int i = 2; i < rowList.size(); i++) {
            for (int j = 1; j < columnList.size(); j++) {
                WebElement data = driver.findElement(By.xpath("//table[@name='BookTable']//tr["+i+"]//td["+j+"]"));
               log.info("Row: {}, Column: {}, Data: {}", i, j, data.getText());
            }
        }
    }

    @Test
    public void givenStaticTable_whenPrintBooksByAuthor_thenSuccess() {
        // Rows
        List<WebElement> rowList = driver.findElements(By.xpath("//table[@name='BookTable']//tr"));

        for (int i = 2; i < rowList.size(); i++) {
            String author = driver.findElement(By.xpath("//table[@name='BookTable']//tr["+i+"]//td[2]")).getText();
            if(author.contains("Mukesh")){
                String book = driver.findElement(By.xpath("//table[@name='BookTable']//tr["+i+"]//td[1]")).getText();
                log.info("Author: {} -> Book: {}", author, book);
                assertThat(book).containsAnyOf("Master In Selenium", "Learn Java");
            }
        }
    }

    @Test
    public void givenStaticTable_whenPrintBooksByPrice_thenSuccess() {
        // Rows
        List<WebElement> rowList = driver.findElements(By.xpath("//table[@name='BookTable']//tr"));

        int totalprice = 0;
        for (int i = 2; i < rowList.size(); i++) {
            // Retrieve prices
            String price = driver.findElement(By.xpath("//table[@name='BookTable']//tr["+i+"]//td[4]")).getText();

            // Update total price
            totalprice += Integer.parseInt(price);

            // Retrieve books
            String book = driver.findElement(By.xpath("//table[@name='BookTable']//tr["+i+"]//td[1]")).getText();
            log.info("Book: {} -> Price: {}", book, price);
        }
        // Print total price
        log.info("Total price: {}", totalprice);
    }

    // TODO -> Assignment
    /*
    https://blazedemo.com/
    Navigate the booking website to search for the cheapest flight to you destination then
    receive confirmation message "Your flight from TLV to SFO has been reserved."
     */
}
