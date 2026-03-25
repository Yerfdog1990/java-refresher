package day41;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.JavascriptExecutor;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class FixedDepositCalculatorTest {
    private static final String URL  = "https://www.moneycontrol.com/fixed-income/calculator/state-bank-of-india-sbi/fixed-deposit-calculator-SBI-BSB001.html?classic=true";
    private static WebDriver driver;
    String filePath = System.getProperty("user.dir") + "/src/test/resources/test_data.xlsx";

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
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
    void givenPreLoadedExcelFile_whenVerifyResult_thenSuccess() throws InterruptedException {
        // Read data from Excel
        int rowCount = ExcelUtils.getRowCount(filePath, 0);
        for (int i = 1; i <= rowCount; i++) {
            String principal = ExcelUtils.getCellData(filePath, 0, i, 0);
            String rate = ExcelUtils.getCellData(filePath, 0, i, 1);
            String period1 = ExcelUtils.getCellData(filePath, 0, i, 2);
            String period2 = ExcelUtils.getCellData(filePath, 0, i, 3);
            String frequency = ExcelUtils.getCellData(filePath, 0, i, 4);
            String expectedMaturityValue = ExcelUtils.getCellData(filePath, 0, i, 5);

            // Pass data to the calculator
            driver.findElement(By.xpath("//input[@id='principal']")).sendKeys(principal);
            driver.findElement(By.xpath("//input[@id='interest']")).sendKeys(rate);
            driver.findElement(By.xpath("//input[@id='tenure']")).sendKeys(period1);
            Select dropDownList1 = new Select(driver.findElement(By.xpath("//select[@id='tenurePeriod']")));
            dropDownList1.selectByVisibleText(period2);
            Select dropDownList2 = new Select(driver.findElement(By.xpath("//select[@id='frequency']")));
            dropDownList2.selectByVisibleText(frequency);
            
            // Handle possible overlay by using JavaScript click
            try {
                driver.findElement(By.xpath("//div[@class='CTR PT15']//a[1]")).click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.xpath("//div[@class='CTR PT15']//a[1]")));
            }
            
            String actualMaturityValue = driver.findElement(By.xpath("//span[@id='resp_matval']//strong")).getText();

            // Validation
            double expected = Double.parseDouble(expectedMaturityValue);
            double actual = Double.parseDouble(actualMaturityValue);
            if (expected == actual) {
                assertEquals(expected, actual, 0.01);
                log.info("Test passed for row {}", i);

                // Fill the "expected" cell green
                ExcelUtils.setCellData(filePath, 0, i, 7, "Passed");
                ExcelUtils.fillGreenColor(filePath, 0, i, 7);
            } else {
                log.info("Test failed for row {}", i);
                // Fill the "expected" cell red
                ExcelUtils.setCellData(filePath, 0, i, 7, "Failed");
                ExcelUtils.fillRedColor(filePath, 0, i, 7);
            }
            // Clear the calculated values
            Thread.sleep(3000);
            try {
                driver.findElement(By.xpath("//img[@class='PL5']")).click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", driver.findElement(By.xpath("//img[@class='PL5']")));
            }
        }
    }
}
