package day54.page_objects;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class HomePage extends BasePage{
    public HomePage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath="//span[normalize-space()='My Account' ]")
    WebElement linkMyAccount;
    @FindBy(xpath="//a[normalize-space()='Register']")
    WebElement linkRegister;
    @FindBy(xpath ="//a[normalize-space()='Login']")
    WebElement linkLogin;

    public void clickAccount () {
        linkMyAccount.click();
    }

    public void clickRegister() {
        linkRegister.click();
    }

    public void clickLogin() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", linkLogin);
    }
}
