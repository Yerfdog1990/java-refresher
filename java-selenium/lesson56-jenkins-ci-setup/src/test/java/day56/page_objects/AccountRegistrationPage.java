package day56.page_objects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AccountRegistrationPage extends BasePage{

    public AccountRegistrationPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath="//input[@id='input-firstname']")
    WebElement firstNameField;
    @FindBy(xpath="//input[@id='input-lastname']")
    WebElement lastNameField;
    @FindBy(xpath="//input[@id='input-email']")
    WebElement emailAddressField;
    @FindBy(xpath="//input[@id='input-password']")
    WebElement passwordField;
    @FindBy(xpath="//input[@name='agree']")
    WebElement policyAgreement;
    @FindBy(xpath="//button[normalize-space()='Continue']")
    WebElement continueButton;
    @FindBy(xpath ="//h1[normalize-space()='Your Account Has Been Created!']")
    WebElement accountCreatedMessage;

    public void setFirstName(String fname) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(firstNameField));
        firstNameField.sendKeys(fname);
    }

    public void setLastName(String lname) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(lastNameField));
        lastNameField.sendKeys(lname);
    }

    public void setEmail(String email) {
        // Safari-specific validation
        if (driver instanceof SafariDriver) {
            try {
                driver.getCurrentUrl();
                if (driver.getWindowHandles().isEmpty()) {
                    throw new RuntimeException("Safari window is no longer available");
                }
            } catch (Exception e) {
                throw new RuntimeException("Safari window validation failed: " + e.getMessage(), e);
            }
        }
        
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOf(emailAddressField));
        emailAddressField.sendKeys(email);
    }

    public void setPassword(String pass) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(passwordField));
        passwordField.sendKeys(pass);
    }

    public void clickPolicy() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(policyAgreement));
        policyAgreement.click();
    }

    public void clickContinue() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(continueButton));
        JavascriptExecutor js=(JavascriptExecutor)driver;
        js.executeScript("arguments[0].click();", continueButton);
    }

    public String getConfirmationMessage() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOf(accountCreatedMessage));
        return accountCreatedMessage.getText();
    }
}
