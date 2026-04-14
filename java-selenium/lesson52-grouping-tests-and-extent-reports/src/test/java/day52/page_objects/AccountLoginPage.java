package day52.page_objects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AccountLoginPage extends BasePage{

    public AccountLoginPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath = "//input[@id='input-email']")
    WebElement emailAddressField;

    @FindBy(xpath = "//input[@id='input-password']")
    WebElement passwordField;

    @FindBy(xpath = "//button[normalize-space()='Login']")
    WebElement loginButton;

    @FindBy(xpath ="//h1[normalize-space()='My Account']")
    WebElement loginPageTitle;

    public void setEmail(String email) {
        emailAddressField.sendKeys(email);
    }

    public void setPassword(String pass) {
        passwordField.sendKeys(pass);
    }

    public void clickLogin() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", loginButton);
    }

    public boolean checkIfAccountExists() {
        return loginPageTitle.isDisplayed();
    }
}
