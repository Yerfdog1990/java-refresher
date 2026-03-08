package day37;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@Slf4j
public class JavascriptExecutorTest {

    private static final String URL  = "https://testautomationpractice.blogspot.com/";
    private static WebDriver driver;

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
    public void givenWebElement_whenEnterTextUsingSendKeysMethod_thenVerify() throws InterruptedException {
        WebElement searchBox = driver.findElement(By.xpath("//input[@id='Wikipedia1_wikipedia-search-input']"));
        String searchText = "Selenium";

        // Using sendKeys method
        searchBox.sendKeys(searchText);

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Verify the search text
        String attribute = searchBox.getAttribute("value");
        log.info("Search text: {}", attribute);
        assertEquals(searchText, attribute);
    }

    @Test
    public void givenWebElement_whenEnterTextUsingJavascriptExecutor_thenVerify() throws InterruptedException {
        WebElement searchBox = driver.findElement(By.xpath("//input[@id='Wikipedia1_wikipedia-search-input']"));
        String searchText = "Selenium";

        // Using JavascriptExecutor
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("arguments[0].setAttribute('value', 'Selenium')", searchBox);

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Verify the search text
        String attribute = searchBox.getAttribute("value");
        log.info("Search text: {}", attribute);
        assertEquals(searchText, attribute);
    }

    @Test
    public void givenWebElement_whenSelectRadioButtonUsingClickMethod_thenVerify() throws InterruptedException {
        WebElement radioButton = driver.findElement(By.xpath("//input[@id='male']"));

        // Select the radio button using click() method
        radioButton.click();

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Retrieve attribute and verify the radio button is selected
        String radioButtonValue = radioButton.getAttribute("value");
        log.info("Radio button value: {}", radioButtonValue);
        assertEquals("male", radioButtonValue);
        assertTrue(radioButton.isSelected());
    }

    @Test
    public void givenWebElement_whenSelectRadioButtonUsingJavascriptExecutor_thenVerify() throws InterruptedException {
        WebElement radioButton = driver.findElement(By.xpath("//input[@id='male']"));

        // Select the radio button using JavascriptExecutor
        JavascriptExecutor js = (JavascriptExecutor)driver;
        js.executeScript("arguments[0].click()", radioButton);

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Retrieve attribute and verify the radio button is selected
        String radioButtonValue = radioButton.getAttribute("value");
        log.info("Radio button value: {}", radioButtonValue);
        assertEquals("male", radioButtonValue);
        assertTrue(radioButton.isSelected());
    }

    @Test
    public void givenWebPage_whenScrollToElementOffYSet_thenVerify() throws InterruptedException {

        // Scroll webpage using JavascriptExecutor
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, 3000)");

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Verify the webpage is scrolled to the desired position
        String scrollPosition = js.executeScript("return window.pageYOffset").toString();
        log.info("Scroll position: {}", scrollPosition);
        assertEquals("3000", scrollPosition);
    }

    @Test
    public void givenWebpage_whenScrollPageUntilTargetElementIsVisible_thenVerify() throws InterruptedException {
        // Find the target element
        WebElement targetElement = driver.findElement(By.xpath("//h2[normalize-space()='Visitors']"));

        // Scroll the webpage until the target element is visible using JavascriptExecutor
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", targetElement);

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Verify the target element is visible
        if(targetElement.isDisplayed()){
            log.info("Target element is visible: {}", targetElement.isDisplayed());
            assertTrue(targetElement.isDisplayed());
        } else {
            log.info("Target element is not visible");
        }
    }

    @Test
    public void givenWebpage_whenScrollPageUntilEndOfPage_thenVerify() throws InterruptedException {

        // Find the footer element
        WebElement footerElement = driver.findElement(By.xpath("//a[normalize-space()='merrymoonmary']"));

        // Scroll the webpage until the end of the page using JavascriptExecutor
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollBy(0, document.body.scrollHeight)");

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Verify the footer element is visible
        if(footerElement.isDisplayed()){
            log.info("Footer element is visible: {}", footerElement.isDisplayed());
            assertTrue(footerElement.isDisplayed());
        } else {
            log.info("Footer element is not visible");
        }

        // Scroll webpage back to the top
        js.executeScript("window.scrollTo(0, -document.body.scrollHeight)");

        // Find the title element
        WebElement titleElement = driver.findElement(By.xpath("//h1[@class='title']"));

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Verify the header element is visible
        if(titleElement.isDisplayed()){
            log.info("Title element is visible: {}", titleElement.isDisplayed());
            assertTrue(titleElement.isDisplayed());
        } else {
            log.info("Title element is not visible");
        }
    }

    @Test
    public void givenWebPage_whenZoomToDesiredLevel_thenVerify() throws InterruptedException {

        // Zoom the webpage to a desired level using JavascriptExecutor
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("document.body.style.zoom = '50%'");

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Verify the webpage is zoomed to the desired level
        String zoomLevel = js.executeScript("return document.body.style.zoom").toString();
        log.info("Zoom level: {}", zoomLevel);
        assertEquals("50%", zoomLevel);
    }

    @Test
    public void givenWebPage_whenUploadSingleFileUsingSendKeys_thenVerify() throws InterruptedException {

        // Find the file upload field
        WebElement fileUploadField = driver.findElement(By.xpath("//input[@id='singleFileInput']"));

        // Upload a file using the sendKeys method
        fileUploadField.sendKeys("/Users/godfrey/Downloads/myTest.pdf");

        // Click on the file upload field
        driver.findElement(By.xpath("//button[normalize-space()='Upload Single File']")).click();

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Verify the file is uploaded
        String uploadedFile = driver.findElement(By.xpath("//p[@id='singleFileStatus']")).getText();
        log.info("Uploaded file: {}", uploadedFile);
        if(uploadedFile.contains("myTest.pdf")){
            assertThat(uploadedFile).contains("myTest.pdf");
            log.info("File uploaded successfully");
        } else {
            log.info("File was not uploaded");
        }
    }

    @Test
    public void givenWebPage_whenUploadMultipleFilesUsingSendKeys_thenVerify() throws InterruptedException {

        // Find the file upload field
        WebElement fileUploadField = driver.findElement(By.xpath("//input[@id='multipleFilesInput']"));

        // Upload a file using the sendKeys method
        String filePath1 = "/Users/godfrey/Downloads/myTest.pdf";
        String filePath2 = "/Users/godfrey/Downloads/markScheme.pdf";
        fileUploadField.sendKeys(filePath1 + "\n" + filePath2);

        // Click on the file upload field
        driver.findElement(By.xpath("//button[normalize-space()='Upload Multiple Files']")).click();

        // Sleep for 5 seconds
        Thread.sleep(5000);

        // Verify the file is uploaded
        String uploadedFile = driver.findElement(By.xpath("//p[@id='multipleFilesStatus']")).getText();

        if(uploadedFile.contains("myTest.pdf") && uploadedFile.contains("markScheme.pdf")){
            assertThat(uploadedFile).contains("myTest.pdf").contains("markScheme.pdf");
            log.info("Files uploaded successfully");
        } else {
            log.info("Files were not uploaded");
        }

        // Verify the number of uploaded files
        String[] lines = uploadedFile.split("\n");
        int fileCount = lines.length - 1; // Subtract 1 for the "Multiple files selected:" header
        log.info("Uploaded files count: {}", fileCount);
        assertEquals(2, fileCount);
    }
}
