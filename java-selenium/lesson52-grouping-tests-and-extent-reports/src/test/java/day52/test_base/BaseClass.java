package day52.test_base;

import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Date;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Properties;


public class BaseClass {
    // Static method to get driver instance

    public static WebDriver driver; // Make driver static to share it across tests

    Properties properties;
    protected final Logger logger = LogManager.getLogger(getClass());

    @BeforeClass(groups = {"Master", "Sanity", "Regression", "Data driven"})
    @Parameters({"os", "browser"})
    public void setup(String os, String browser) throws IOException {
        try {
            // Loading config.properties file
            FileInputStream fis = new FileInputStream(System.getProperty("user.dir") + "/src/test/resources/config.properties");
            properties = new Properties();
            properties.load(fis);

            switch (browser.toLowerCase()) {
                case "chrome" : driver = new ChromeDriver();
                break;
                case "firefox" : driver = new FirefoxDriver();
                break;
                case "safari" : driver = new SafariDriver();
                break;
                default: throw new IllegalArgumentException("Invalid browser");
            }

            try {
                driver.manage().deleteAllCookies();
            } catch (Exception e) {
                logger.warn("Could not delete cookies: " + e.getMessage());
            }
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            driver.get(properties.getProperty("appURL")); // Get app URL from config.properties
            driver.manage().window().maximize();
        } catch (Exception e) {
            logger.error("Failed to setup WebDriver", e);
            throw e;
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
    // Generate random string
    public String generateRandomString(){
        String randomString = RandomStringUtils.randomAlphabetic(5);
        return randomString;
    }

    // Generate random number
    public String generateRandomNumber(){
        String randomString = RandomStringUtils.randomNumeric(10);
        return randomString;
    }

    // Generate random email
    public String generateRandomEmail(){
        String randomString = generateRandomString();
        return randomString + "@gmail.com";
    }

    // Generate random password
    public String generateRandomPassword(){
        String randomString = RandomStringUtils.randomAlphabetic(3);
        String randomNumber = RandomStringUtils.randomNumeric(3);
        return (randomString + "#" + randomNumber);
    }

    public String captureScreen(String name) throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

        TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
        File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);

        String targetFilePath = System.getProperty("user.dir") + "/screenshots/" + name + "_" + timeStamp + ".png";
        File targetFile = new File(targetFilePath);

        sourceFile.renameTo(targetFile);

        return targetFilePath;
    }
}
