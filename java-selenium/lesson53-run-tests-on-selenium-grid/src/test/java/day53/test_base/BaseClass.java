package day53.test_base;

import lombok.Getter;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;

public class BaseClass {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    @Getter
    protected Properties properties;

    protected final Logger logger = LogManager.getLogger(getClass());

    protected WebDriver getDriver() {
        return DRIVER.get();
    }

    protected void setDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    @BeforeClass(groups = {"Master", "Sanity", "Regression", "Data driven"})
    @Parameters({"os", "browser"})
    public void setup(String os, String browser) throws IOException {
        properties = new Properties();

        try (FileInputStream fis = new FileInputStream(
                System.getProperty("user.dir") + "/src/test/resources/config.properties")) {
            properties.load(fis);
        }

        String executionEnv = properties.getProperty("execution_env");
        if (executionEnv == null || executionEnv.trim().isEmpty()) {
            throw new IllegalStateException("Missing required property: execution_env");
        }

        String normalizedEnv = executionEnv.trim().toLowerCase();
        String normalizedBrowser = Objects.requireNonNull(browser, "browser parameter must not be null").trim().toLowerCase();

        WebDriver webDriver;

        if ("remote".equals(normalizedEnv)) {
            DesiredCapabilities capabilities = new DesiredCapabilities();

            if (os == null) {
                throw new IllegalArgumentException("OS parameter must not be null");
            }

            switch (os.trim().toLowerCase()) {
                case "windows" -> capabilities.setPlatform(Platform.WIN11);
                case "mac" -> capabilities.setPlatform(Platform.MAC);
                case "linux" -> capabilities.setPlatform(Platform.LINUX);
                default -> throw new IllegalArgumentException("Invalid OS: " + os);
            }

            switch (normalizedBrowser) {
                case "chrome" -> capabilities.setBrowserName("chrome");
                case "firefox" -> capabilities.setBrowserName("firefox");
                case "safari" -> capabilities.setBrowserName("safari");
                default -> throw new IllegalArgumentException("Invalid browser: " + browser);
            }

            String remoteUrl = properties.getProperty("remoteURL");
            if (remoteUrl == null || remoteUrl.trim().isEmpty()) {
                throw new IllegalStateException("Missing required property: remoteURL");
            }

            webDriver = new RemoteWebDriver(new URL(remoteUrl.trim()), capabilities);

        } else if ("local".equals(normalizedEnv)) {
            switch (normalizedBrowser) {
                case "chrome" -> webDriver = new ChromeDriver();
                case "firefox" -> webDriver = new FirefoxDriver();
                case "safari" -> webDriver = new SafariDriver();
                default -> throw new IllegalArgumentException("Invalid browser: " + browser);
            }
        } else {
            throw new IllegalArgumentException("Invalid execution_env: " + executionEnv);
        }

        setDriver(webDriver);

        try {
            getDriver().manage().deleteAllCookies();
        } catch (Exception e) {
            logger.warn("Could not delete cookies: {}", e.getMessage());
        }

        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        String appUrl = properties.getProperty("appURL");
        if (appUrl == null || appUrl.trim().isEmpty()) {
            throw new IllegalStateException("Missing required property: appURL");
        }

        getDriver().get(appUrl.trim());
        getDriver().manage().window().maximize();
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        WebDriver webDriver = getDriver();
        if (webDriver != null) {
            try {
                webDriver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }

    public String generateRandomString() {
        return RandomStringUtils.randomAlphabetic(5);
    }

    public String generateRandomNumber() {
        return RandomStringUtils.randomNumeric(10);
    }

    public String generateRandomEmail() {
        return generateRandomString() + "@gmail.com";
    }

    public String generateRandomPassword() {
        return RandomStringUtils.randomAlphabetic(3) + "#" + RandomStringUtils.randomNumeric(3);
    }

    public String captureScreen(String name) throws IOException {
        WebDriver webDriver = getDriver();
        if (webDriver == null) {
            throw new IllegalStateException("WebDriver is not initialized");
        }

        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        File sourceFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);

        File screenshotsDir = new File(System.getProperty("user.dir") + "/screenshots");
        if (!screenshotsDir.exists()) {
            Files.createDirectories(screenshotsDir.toPath());
        }

        File targetFile = new File(screenshotsDir, name + "_" + timeStamp + ".png");
        Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        return targetFile.getAbsolutePath();
    }
}