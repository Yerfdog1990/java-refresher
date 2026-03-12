package day38;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class HandleHeadlessTest {

    private static final String URL  = "https://testautomationpractice.blogspot.com/";
    private static WebDriver driver;

    @BeforeEach
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new"); // Setting headless mode


        driver = new ChromeDriver(options); // Passing options to the driver
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
    public void givenWebPage_whenCapturingFullPage_thenVerifyScreenshotExists() throws IOException {

        // Take screenshot
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        File sourceFile = screenshot.getScreenshotAs(OutputType.FILE);

        // Define target location
        Path targetPath = Paths.get(System.getProperty("user.dir"), "src/test/resources/screenshots/headless_fullPage.png");

        // Ensure parent directory exists
        Files.createDirectories(targetPath.getParent());

        // Copy screenshot to the target file
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        File targetFile = targetPath.toFile();

        // Assert the file exists
        assertTrue(targetFile.exists(), "Screenshot file should exist");
    }

    @Test
    public void givenWebPage_whenCapturingSpecificSection_thenVerifyScreenshotExists() throws IOException {

        // Take screenshot
        WebElement staticWebTable = driver.findElement(By.xpath("//div[@id='HTML1']"));
        File sourceFile = staticWebTable.getScreenshotAs(OutputType.FILE);

        // Define target location
        Path targetPath = Paths.get(System.getProperty("user.dir"), "src/test/resources/screenshots/headless_staticWebTable.png");

        // Ensure parent directory exists
        Files.createDirectories(targetPath.getParent());

        // Copy screenshot to the target file
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        File targetFile = targetPath.toFile();

        // Assert the file exists
        assertTrue(targetFile.exists(), "Screenshot file should exist");
    }

    @Test
    public void givenWebPage_whenCapturingStartButton_thenVerifyScreenshotExists() throws IOException {

        // Take screenshot
        WebElement startButton = driver.findElement(By.xpath("//div[@id='HTML5']//div[@class='widget-content']"));
        File sourceFile = startButton.getScreenshotAs(OutputType.FILE);

        // Define target location
        Path targetPath = Paths.get(System.getProperty("user.dir"), "src/test/resources/screenshots/headless_startButton.png");

        // Ensure parent directory exists
        Files.createDirectories(targetPath.getParent());

        // Copy screenshot to the target file
        Files.copy(sourceFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        File targetFile = targetPath.toFile();

        // Assert the file exists
        assertTrue(targetFile.exists(), "Screenshot file should exist");
    }
}
