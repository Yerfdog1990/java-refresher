package day52.page_objects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

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
        firstNameField.sendKeys(fname);
    }

    public void setLastName(String lname) {
        lastNameField.sendKeys(lname);
    }

    public void setEmail(String email) {
        emailAddressField.sendKeys(email);
    }

    public void setPassword(String pass) {
        passwordField.sendKeys(pass);
    }

    public void clickPolicy() {
        policyAgreement.click();
    }

    public void clickContinue() {
        //sol 1
        // btnContinue.click();
        //sol 2
        //btnContinue.submit();
        //sol 3
        //Actions act=new Actions(driver);
        //act.moveToElement(btnContinue).click().perform();
        //sol 4
        JavascriptExecutor js=(JavascriptExecutor)driver;
        js.executeScript("arguments[0].click();", continueButton);
        // Sol 5
        // btnContinue.sendkeys(Keys.RETURN);
        // Sol 6
        // WebDriverWait mywait = new WebDriverWait(driver, Duration.ofSeconds (10));
        //mywait.until(ExpectedConditions.elementToBeClickable(btnContinue)).click();
    }

    public String getConfirmationMessage() {
        return accountCreatedMessage.getText();
    }
}
