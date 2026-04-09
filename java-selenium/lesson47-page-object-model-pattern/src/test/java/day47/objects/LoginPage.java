package day47.objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private final WebDriver driver;

    // Constructor
    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    // Locators
    By username = By.xpath("//input[@placeholder='Username']");
    By password = By.xpath("//input[@placeholder='Password']");
    By submitButton = By.xpath("//button[@type='submit']");

    // Actions
    public void setUsername(String username) {
        driver.findElement(this.username).sendKeys(username);
    }

    public void setPassword(String password) {
        driver.findElement(this.password).sendKeys(password);
    }

    public void clickSubmitButton() {
        driver.findElement(this.submitButton).click();
    }


}
