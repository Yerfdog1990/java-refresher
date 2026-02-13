package day34;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.interning.qual.CompareToMethod;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@Slf4j
public class HandleDatePickerTest {
    private static final String DROPDOWN_URL  = "https://testautomationpractice.blogspot.com/";

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
    public void givenWebElement_whenHandlingDate_thenSelectFutureDate() {
        String requiredYear = "2020";
        String requiredMonth = "01";
        String requiredDay = "01";

        // Select requiredYear
        driver.findElement(By.xpath("//input[@id='txtDate']")).click();
        WebElement webElement1 = driver.findElement(By.xpath("//select[@aria-label='Select year']"));
        Select yearDropdown = new Select(webElement1);
        yearDropdown.selectByValue(requiredYear);
        int parsedInt = Integer.parseInt(requiredYear);
        assertEquals(2020, parsedInt);

        while (true){
            
            // Select month
            //TODO find the problems with the select month xpath since Selenium could not locate it.
            String displayedMonth = driver.findElement(By.xpath("//span[@class='ui-datepicker-month']")).getText();

            // Convert months
            Month requiredMonthObject = convertToMonth(requiredMonth);
            Month displayedMonthObject = convertToMonth(displayedMonth);
            // Compared the two months
            int result = requiredMonthObject.compareTo(displayedMonthObject);
            if(result < 0) {
                // Required month is future
                driver.findElement(By.xpath("//span[@class='ui-icon ui-icon-circle-triangle-e']")).click();
            }else if(result > 0) {
                // Required month is past
                driver.findElement(By.xpath("//span[@class='ui-icon ui-icon-circle-triangle-w']")).click();
            } else {
                // Months match, no navigation needed
                break;
            }
        }
        List<WebElement> allDates = driver.findElements(By.xpath("//table[@class='ui-datepicker-calender']//tbody//tr//td//a"));
        for (WebElement dt : allDates) {
            if (dt.getText().equals(requiredDay)) {
                dt.click();
                break;
            }
        }
    }

    // Convert month strings to Month objects
    public static Month convertToMonth(String month) {
       Map<String, Month> monthsMap = new HashMap<>();
       monthsMap.put("January", Month.JANUARY);
       monthsMap.put("February", Month.FEBRUARY);
       monthsMap.put("March", Month.MARCH);
       monthsMap.put("April", Month.APRIL);
       monthsMap.put("May", Month.MAY);
       monthsMap.put("June", Month.JUNE);
       monthsMap.put("July", Month.JULY);
       monthsMap.put("August", Month.AUGUST);
       monthsMap.put("September", Month.SEPTEMBER);
       monthsMap.put("October", Month.OCTOBER);
       monthsMap.put("November", Month.NOVEMBER);
       monthsMap.put("December", Month.DECEMBER);

        Month selectedMonth = monthsMap.get(month);
        if (selectedMonth == null) {
            log.info("Invalid month selected");
        }
        return selectedMonth;
    }
}
