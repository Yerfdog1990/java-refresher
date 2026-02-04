package day33;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class DynamicPaginationTableTest {

    private static final String ADMIN_URL = "http://localhost:8080/admin/";

    private static WebDriver driver;

    @BeforeEach
    void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Navigate admin page
        driver.get(ADMIN_URL);

        // Navigate the login form and clear the saved username and password
        WebElement username = driver.findElement(By.xpath("//input[@id='input-username']"));
        username.clear();
        WebElement password = driver.findElement(By.xpath("//input[@id='input-password']"));
        password.clear();

        // Enter the new username and password
        username.sendKeys("admin");
        password.sendKeys("admin");

        // Click the login button
        driver.findElement(By.xpath("//button[@type='submit']")).click();

        // Dismiss the security modal if it appears
        try {
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modal-security")));
            // Try the Bootstrap 5 close button or dismiss any button inside the modal
            WebElement closeBtn = modal.findElement(By.cssSelector("button.btn-close, button[data-bs-dismiss='modal'], .modal-footer .btn"));
            closeBtn.click();
            wait.until(ExpectedConditions.invisibilityOf(modal));
        } catch (TimeoutException ignored) {
            // Modal didn't appear; continue
        }

        // Navigate the "customers" page and click to open to access the web dynamic table
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[contains(text(),'Customers')]"))).click();
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ul[@id='collapse-5']//a[contains(text(),'Customers')]"))).click();

    }


    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // 1) Finding the number of pages then print selected customer details
    @Test
    public void givenDynamicWebTable_whenFindNumberOfPages_thenSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Showing')]")));
        String string = webElement.getText();
        
        String pages = string.substring(string.indexOf("(") + 1, string.indexOf("Pages") - 1);
        int totalNumberOfPages = Integer.parseInt(pages);
        log.info("Total number of pages: {}", totalNumberOfPages);
        assertThat(totalNumberOfPages).isEqualTo(3);
        for (int page = 1; page <= totalNumberOfPages; page++) {
            if(page > 1){
                WebElement pageLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ul[@class='pagination']//*[text()=" + page + "]")));
                pageLink.click();
                // Wait for the table to reload by checking that the "Showing..." text has updated to reflect the new page
                // The text format is usually "Showing 1 to 10 of 27 (3 Pages)"
                // We can wait for it to contain the expected starting record for the page
                int startRecord = (page - 1) * 10 + 1;
                wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//div[contains(text(),'Showing')]"), "Showing " + startRecord));
            }
            // Wait for the rows to be present and visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table table-bordered table-hover']//tbody//tr")));
            
            // print customer details
            List<WebElement> rows = driver.findElements(By.xpath("//table[@class='table table-bordered table-hover']//tbody//tr"));
            for (int row = 1; row <= rows.size(); row++) {
                // Re-finding the row element in each iteration to avoid staleness if the table happens to refresh
                WebElement rowElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//table[@class='table table-bordered table-hover']//tbody//tr[" + row + "]")));
                try {
                    String customerName = rowElement.findElement(By.xpath("td[2]")).getText();
                    String customerEmail = rowElement.findElement(By.xpath("td[3]")).getText();
                    String customerStatus = rowElement.findElement(By.xpath("td[5]")).getText();
                    log.info("|" + customerName + " |\t" + customerEmail + " |\t" + customerStatus + "|");
                } catch (org.openqa.selenium.StaleElementReferenceException e) {
                    // Fallback: if it still becomes stale, try one more time by re-finding everything
                    rowElement = driver.findElement(By.xpath("//table[@class='table table-bordered table-hover']//tbody//tr[" + row + "]"));
                    String customerName = rowElement.findElement(By.xpath("td[2]")).getText();
                    String customerEmail = rowElement.findElement(By.xpath("td[3]")).getText();
                    String customerStatus = rowElement.findElement(By.xpath("td[5]")).getText();
                    log.info("|" + customerName + " |\t" + customerEmail + " |\t" + customerStatus + "|");
                }
            }
        }
    }

    // 2) Finding the number of Rows in all pages
    @Test
    public void givenDynamicWebTable_whenCountRows_thenSuccess() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement webElement = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(text(),'Showing')]")));
        String text = webElement.getText();

        // Extract total the number of pages: "Showing 1 to 10 of 27 (3 Pages)"
        int totalNumberOfPages = Integer.parseInt(text.substring(text.indexOf("(") + 1, text.indexOf("Pages") - 1));
        log.info("Total number of pages: {}", totalNumberOfPages);

        int totalRows = 0;
        for (int page = 1; page <= totalNumberOfPages; page++) {
            if (page > 1) {
                WebElement pageLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//ul[@class='pagination']//*[text()=" + page + "]")));
                pageLink.click();
                // Wait for the table to reload by checking that the "Showing..." text has updated to reflect the new page
                int startRecord = (page - 1) * 10 + 1;
                wait.until(ExpectedConditions.textToBePresentInElementLocated(By.xpath("//div[contains(text(),'Showing')]"), "Showing " + startRecord));
            }

            // Wait for the rows to be present and visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//table[@class='table table-bordered table-hover']//tbody//tr")));

            List<WebElement> rows = driver.findElements(By.xpath("//table[@class='table table-bordered table-hover']//tbody//tr"));
            totalRows += rows.size();
        }

        log.info("Total number of rows in all pages: {}", totalRows);

        // Verification: The "Showing..." text also contains the total number of records
        // Format: "Showing 1 to 10 of 27 (3 Pages)"
        int expectedTotalRows = Integer.parseInt(text.substring(text.indexOf("of") + 3, text.indexOf("(") - 1));
        assertThat(totalRows).isEqualTo(expectedTotalRows);
    }

    // 3) Finding number of Columns
    @Test
    public void givenDynamicWebTable_whenCountColumns_thenSuccess() {
        List<WebElement> columnsNumber = driver.findElements(By.xpath("//table[@class='table table-bordered table-hover']//thead//tr//td"));
        int columnCount = columnsNumber.size();
        log.info("No of columns in this table : {}", columnCount);
        assertThat(columnCount).isEqualTo(6);
    }

    // 4) Finding cell value at the 4th row and 3rd column
    @Test
    public void givenDynamicWebTable_whenGetCellValue_thenSuccess() {
        WebElement cellAddress = driver.findElement(By.xpath("//table[@class='table table-bordered table-hover']//tbody//tr[4]//td[3]"));
        String value = cellAddress.getText();
        log.info("The Cell Value is : {}", value);
        assertThat(value).isEqualTo("eve.w@example.com");
    }
}
