package day49.page_objects;

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

    public void clickAccount () {
        linkMyAccount.click();
    }

    public void clickRegister() {
        linkRegister.click();
    }
}
