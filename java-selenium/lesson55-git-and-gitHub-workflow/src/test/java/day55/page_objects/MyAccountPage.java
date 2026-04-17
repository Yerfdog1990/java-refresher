package day55.page_objects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class MyAccountPage extends BasePage{
    public MyAccountPage(WebDriver driver) {
        super(driver);
    }

    @FindBy(xpath ="//h1[normalize-space()='My Account']")
    WebElement loginPageTitle;

    @FindBy(xpath ="//a[@class='list-group-item'][normalize-space()='Logout']")
    WebElement logoutButton;

    public boolean checkIfAccountExists() {
        return loginPageTitle.isDisplayed();
    }

    public void clickLogout() {
        logoutButton.click();
    }
}
