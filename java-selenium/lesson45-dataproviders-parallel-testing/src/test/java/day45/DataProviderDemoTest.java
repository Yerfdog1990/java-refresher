package day45;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

import java.time.Duration;

import static org.testng.AssertJUnit.*;

public class DataProviderDemoTest {
    private static final String URL  = "https://tutorialsninja.com/demo/index.php?route=account/login";
    private WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(URL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        if(driver != null){
            driver.close();
        }
    }

    @Test
    void givenSingleUser_whenLogin_thenSuccess() throws InterruptedException {
        driver.findElement(By.xpath("//input[@id='input-email']")).sendKeys("gouma308@gmail.com");
        driver.findElement(By.xpath("//input[@id='input-password' ]")).sendKeys("JCY#xw#y4WJ8jj");
        driver.findElement(By.xpath("//input[@value='Login' ]")).click();
        Thread.sleep(2000);
        boolean status = driver.findElement(By.xpath("//h2[normalize-space()='My Account' ]")).isDisplayed();
        if (status) {
            driver.findElement(By.xpath("//a[@class='list-group-item'][normalize-space()='Logout']")).click();
            assertTrue(true);
        } else {
            fail();
        }
    }

    @Test(dataProvider = "dp")
    void givenMultipleUsers_whenLogin_thenSuccess(String email, String password) throws InterruptedException {
        driver.findElement(By.xpath("//input[@id='input-email']")).sendKeys(email);
        driver.findElement(By.xpath("//input[@id='input-password' ]")).sendKeys(password);
        driver.findElement(By.xpath("//input[@value='Login' ]")).click();
        Thread.sleep(2000);
        boolean status = driver.findElement(By.xpath("//h2[normalize-space()='My Account' ]")).isDisplayed();
        if (status) {
            driver.findElement(By.xpath("//a[@class='list-group-item'][normalize-space()='Logout']")).click();
            assertTrue(true);
        } else {
            fail();
        }
    }

    @DataProvider(name = "dp", indices = {0, 2})
    Object[][] loginData() {
        return new Object[][]{
                {"gouma308@gmail.com", "JCY#xw#y4WJ8jj"},
                {"gouma308@gmail.com", "JCY#xw#y4WJ8jj"},
                {"gouma308@gmail.com", "JCY#xw#y4WJ8jj"}
        };
    }
}

