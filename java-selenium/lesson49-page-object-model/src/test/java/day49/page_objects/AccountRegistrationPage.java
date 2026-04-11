package day49.page_objects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class AccountRegistrationPage extends HomePage{

    public AccountRegistrationPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath="//input[@id='input-firstname']")
    WebElement txtFirstname;
    @FindBy(xpath="//input[@id='input-lastname']")
    WebElement txtLasttname;
    @FindBy(xpath="//input[@id='input-email']")
    WebElement txtEmail;
    @FindBy(xpath="//input[@id='input-telephone']")
    WebElement txtTelephone;
    @FindBy(xpath="//input[@id='input-password']")
    WebElement txtPassword;
    @FindBy(xpath="//input[@id='input-confirm']")
    WebElement txtConfirmPassword;
    @FindBy(xpath="//input[@name='agree']")
    WebElement chkdPolicy;
    @FindBy(xpath="//button[normalize-space()='Continue']")
    WebElement btnContinue;
    @FindBy(xpath ="//h1[normalize-space()='Your Account Has Been Created!']")
    WebElement msgConfirmation;

    public void setFirstName(String fname) {
        txtFirstname.sendKeys(fname);
    }

    public void setLastName(String lname) {
        txtLasttname.sendKeys(lname);
    }

    public void setEmail(String email) {
        txtEmail.sendKeys(email);
    }

    public void setPassword(String pass) {
        txtPassword.sendKeys(pass);
    }

    public void clickPolicy() {
        chkdPolicy.click();
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
       js.executeScript("arguments[0].click();", btnContinue);
        // Sol 5
        // btnContinue.sendkeys(Keys.RETURN);
        // Sol 6
        // WebDriverWait mywait = new WebDriverWait(driver, Duration.ofSeconds (10));
        //mywait.until(ExpectedConditions.elementToBeClickable(btnContinue)).click();
    }

    public String getConfirmationMessage() {
        try {
            return msgConfirmation.getText();
        } catch (Exception e) {
            return (e.getMessage());
        }
    }
}
