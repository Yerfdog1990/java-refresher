package day39;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@Slf4j
public class HandlingBrokenLinkTest {

    private static final String URL  = "http://www.deadlinkcity.com/";
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
    void givenUrl_whenGetRequest_thenTestBrokenLinks() {
        // Find all links on the page
        List<WebElement> links = driver.findElements(By.tagName("a"));

        // Initialize broken link count
        int brokenLinkCount = 0;

        // Retrieve href attributes
        for (WebElement link : links) {
            String href = link.getAttribute("href");
            if(href == null || href.isEmpty()) {
                log.info("href attribute is null or empty");
                continue;
            }
            // Hit url to the server
            try {
                URI linkUrl = new URI(href);

                // Convert href value from String to URL format
                URL url = linkUrl.toURL();

                // Open connection to the server
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                // Connect to the server and sent request
                urlConnection.connect();

                // Get the response code
                int responseCode = urlConnection.getResponseCode();
                log.info("Response code: " + responseCode);

                // Check if the link is broken
                if (responseCode >= 400) {
                    log.info("Broken link found: {}", href);
                    brokenLinkCount++;
                } else {
                    log.info("No broken links found: {}", href);
                }
                // Close the connection
                urlConnection.disconnect();
            } catch (Exception e) {
                log.error("Error occurred while connecting to link: {} - Error: {}", href, e.getMessage());
                brokenLinkCount++;
            }
        }

        // Check the total number of links
        int totalLinks = links.size();
        log.info("Total number of links: " + totalLinks);
        assertThat(totalLinks).isEqualTo(48);
    }
}
