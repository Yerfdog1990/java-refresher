package day47.objects;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;

public class PageFactoryLoginPage {

    private final WebDriver driver;

    // Constructor
    public PageFactoryLoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    // Locators
    @FindBy(how = How.XPATH, using = "//input[@placeholder='Username']")
    private WebElement usernameField;
    @FindBy(how = How.XPATH, using = "//input[@placeholder='Password']")
    private WebElement passwordField;
    @FindBy(how = How.XPATH, using = "//button[@type='submit']")
    private WebElement submitButtonField;

    // Alternative method to retrieve the locators
    // @FindBy(xpath = "//input[@placeholder='Username']")
    // WebElement usernameField;
    // @FindBy(xpath = "//input[@placeholder='Password']")
    // WebElement passwordField;
    // @FindBy(xpath = "//button[@type='submit']")
    // WebElement submitButtonField;


    // Actions
    public void setUsername(String username) {
        usernameField.sendKeys(username);
    }

    public void setPassword(String password) {
        passwordField.sendKeys(password);
    }

    public void clickSubmitButton() {
        submitButtonField.click();
    }

}
