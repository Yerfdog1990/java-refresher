package day21;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.safari.SafariDriver;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FirstTestCase {

    @Test
    public void givenUrl_when_getTitle_thenSuccess() {
        // Step 1: Launch the safari browser
        System.out.println("Launching the browser...");
        // SafariDriver driver = new SafariDriver();
        WebDriver driver = new SafariDriver();

        // Step 2: Navigate to the URL
        System.out.println("Navigating to the URL...");
        driver.get("https://www.selenium.dev/");

        // Step 3: Validate the title of the page -> Your page
        System.out.println("Validating the title...");
        String expectedTitle = driver.getTitle();
        String actualTitle = "Selenium";
        assertEquals(actualTitle, expectedTitle);

        // Step 4: Close the browser
        System.out.println("Closing the browser...");
        driver.quit();
    }
}
