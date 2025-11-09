package org.example;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.safari.SafariDriver;

public class FirstTestInSelenium {
    public static void main(String[] args) {

        // Initialize ChromeDriver
        WebDriver driver = new SafariDriver();

        // Waits and setup
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        // Open website
        driver.get("https://www.google.com");
        System.out.println("Title: " + driver.getTitle());

        // Close browser
        driver.close();
    }
}
