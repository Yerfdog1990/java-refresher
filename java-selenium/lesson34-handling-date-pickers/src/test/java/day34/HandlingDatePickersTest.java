package day34;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@Slf4j
public class HandlingDatePickersTest {
    private static final String DROPDOWN_URL  = "https://jqueryui.com/datepicker/";

    private static WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(DROPDOWN_URL);
        driver.switchTo().frame(0);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void givenWebElement_whenHandlingDate_thenSendKeys(){
        WebElement webElement = driver.findElement(By.xpath("//input[@id='datepicker']"));
        webElement.sendKeys("02/12/2026");
        String date = webElement.getAttribute("value");
        assertThat(date).isEqualTo("02/12/2026");
    }

    @Test
    public void givenWebElement_whenHandlingDate_thenSelectCurrentDate(){
        LocalDate currentDate = LocalDate.now();
        String expectedMonth = currentDate.format(DateTimeFormatter.ofPattern("MMMM"));
        String expectedYear = String.valueOf(currentDate.getYear());
        String expectedDate = String.format("%02d", currentDate.getDayOfMonth());

        extractedCurrentMonthAndYear(expectedMonth, expectedYear);

        List<WebElement> allDates = driver.findElements(By.xpath("//table[@class='ui-datepicker-calendar']//tbody//tr/td//a"));
        boolean dateFound = false;
        for(WebElement date : allDates){
            if(date.getText().equals(expectedDate)){
                date.click();
                dateFound = true;
                break;
            }
        }

        WebElement dateInput = driver.findElement(By.xpath("//input[@id='datepicker']"));
        String selectedDate = dateInput.getAttribute("value");
        String expectedFormattedDate = currentDate.format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        assertThat(dateFound).isTrue();
        assertThat(selectedDate).isEqualTo(expectedFormattedDate);
    }

    @Test
    public void givenWebElement_whenHandlingDate_thenSelectFutureDate(){
        String expectedMonth = "February";
        String expectedYear = "2027";
        String expectedDate = "02";

        extractedFutureMonthAndYear(expectedMonth, expectedYear);
        List<WebElement> allDates = driver.findElements(By.xpath("//table[@class='ui-datepicker-calender']//tbody//tr/td//a"));
        for(WebElement date : allDates){
            if(date.getText().equals(expectedDate)){
                date.click();
                break;
            }
            assertEquals(expectedDate, date.getText());
        }
    }

    @Test
    public void givenWebElement_whenHandlingDate_thenSelectPastDate(){
        String expectedMonth = "May";
        String expectedYear = "2025";
        String expectedDate = "01";

        extractedPastMonthAndYear(expectedMonth, expectedYear);
        List<WebElement> allDates = driver.findElements(By.xpath("//table[@class='ui-datepicker-calender']//tbody//tr/td//a"));
        for(WebElement date : allDates){
            if(date.getText().equals(expectedDate)){
                date.click();
                break;
            }
            assertEquals(expectedDate, date.getText());
        }
    }

    // Current month and year
    private static void extractedCurrentMonthAndYear(String expectedMonth, String expectedYear) {
        driver.findElement(By.xpath("//input[@id='datepicker']")).click();
        while(true){
            String month = driver.findElement(By.xpath("//span[@class='ui-datepicker-month']")).getText();
            String year = driver.findElement(By.xpath("//span[@class='ui-datepicker-year']")).getText();
            if(month.equals(expectedMonth) && year.equals(expectedYear)){
                break;
            }
            
            LocalDate currentDate = LocalDate.now();
            
            if(Integer.parseInt(year) > currentDate.getYear() || 
               (Integer.parseInt(year) == currentDate.getYear() && getMonthValue(month) > currentDate.getMonthValue())) {
                driver.findElement(By.xpath("//span[@class='ui-icon ui-icon-circle-triangle-w']")).click();
            } else {
                driver.findElement(By.xpath("//span[@class='ui-icon ui-icon-circle-triangle-e']")).click();
            }
        }
    }
    
    private static int getMonthValue(String monthName) {
        return switch (monthName) {
            case "January" -> 1;
            case "February" -> 2;
            case "March" -> 3;
            case "April" -> 4;
            case "May" -> 5;
            case "June" -> 6;
            case "July" -> 7;
            case "August" -> 8;
            case "September" -> 9;
            case "October" -> 10;
            case "November" -> 11;
            case "December" -> 12;
            default -> 0;
        };
    }

    // Path Month and year
    private static void extractedPastMonthAndYear(String expectedMonth, String expectedYear) {
        while(true){
            driver.findElement(By.xpath("//input[@id='datepicker']")).click();
            String month = driver.findElement(By.xpath("//span[@class='ui-datepicker-month']")).getText();
            String year = driver.findElement(By.xpath("//span[@class='ui-datepicker-year']")).getText();
            if(month.equals(expectedMonth) && year.equals(expectedYear)){
                break;
            }
            driver.findElement(By.xpath("//span[@class='ui-icon ui-icon-circle-triangle-w']")).click();
        }
    }

    // Future month and year
    private static void extractedFutureMonthAndYear(String expectedMonth, String expectedYear) {
        while(true){
            driver.findElement(By.xpath("//input[@id='datepicker']")).click();
            String month = driver.findElement(By.xpath("//span[@class='ui-datepicker-month']")).getText();
            String year = driver.findElement(By.xpath("//span[@class='ui-datepicker-year']")).getText();
            if(month.equals(expectedMonth) && year.equals(expectedYear)){
                break;
            }
            driver.findElement(By.xpath("//span[@class='ui-icon ui-icon-circle-triangle-e']")).click();
        }
    }
}
