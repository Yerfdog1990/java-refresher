package day35;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class MouseActionsTest {
    private static final String URL1  = "https://www.orangehrm.com/";
    private static final String URL2  = "https://swisnl.github.io/jQuery-contextMenu/demo.html";
    private static final String URL3  = "https://www.w3schools.com/tags/tryit.asp?filename=tryhtml5_ev_onclick3";
    private static final String URL4 = "https://testautomationpractice.blogspot.com/";

    private static WebDriver driver1;
    private static WebDriver driver2;
    private static WebDriver driver3;
    private static WebDriver driver4;

    @BeforeEach
    public void setUp() {
        driver1 = new ChromeDriver();
        driver1.manage().window().maximize();
        driver1.get(URL1);
        driver1.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver2 = new ChromeDriver();
        driver2.manage().window().maximize();
        driver2.get(URL2);
        driver2.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver3 = new ChromeDriver();
        driver3.manage().window().maximize();
        driver3.get(URL3);
        driver3.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        driver4 = new ChromeDriver();
        driver4.manage().window().maximize();
        driver4.get(URL4);
        driver4.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
       if(driver1 != null){
           driver1.close();
       }
       if(driver2 != null){
           driver2.close();
       }
       if(driver3 != null){
           driver3.close();
       }
       if(driver4 != null){
           driver4.close();
       }
    }

    @Test
    public void givenWebElement_whenPerformHoverAction_thenVerify() {
        WebElement company = driver1.findElement(By.xpath("//a[normalize-space()='Company']"));
        WebElement aboutCompany = driver1.findElement(By.xpath("//div[@class='col-md-12 nav-section-main']//a[normalize-space()='About Us']"));

        // Perform mouse action
        Actions actions = new Actions(driver1);
        actions.moveToElement(company).moveToElement(aboutCompany).click().build().perform();
    }

    @Test
    public void givenWebElement_whenPerformRightClickAction_thenVerify() throws InterruptedException {
        WebElement button = driver2.findElement(By.xpath("//span[@class='context-menu-one btn btn-neutral']"));

        // Perform mouse action
        Actions actions = new Actions(driver2);
        actions.contextClick(button).build().perform();

        // Wait for context menu to appear
        Thread.sleep(1000);

        // Click copy
        driver2.findElement(By.xpath("//span[normalize-space()='Copy']")).click();

        // Close alert
        driver2.switchTo().alert().accept();

    }

    @Test
    public void givenWebElement_whenPerformDoubleClickAction_thenVerify() throws InterruptedException {
       // Switch to frame
        driver3.switchTo().frame("iframeResult");

        WebElement box1 = driver3.findElement(By.xpath("//input[@id='field1']"));

        WebElement box2 = driver3.findElement(By.xpath("//input[@id='field2']"));

        WebElement button = driver3.findElement(By.xpath("//button[@onclick='myFunction()']"));

        // Clear first then send keys to input box 1
        box1.clear();
        box1.sendKeys("Hello world!");

        Thread.sleep(2000);
        // Double click actions on the button
        Actions actions = new Actions(driver3);
        actions.doubleClick(button).build().perform();
        
        Thread.sleep(2000);

        // Validate the text copied to input box 2
        String text = box2.getAttribute("value");
        log.info("Text = {}", text);
        assertEquals("Hello world!", text);
    }

    @Test
    public void givenWebElement_whenPerformDragAndDropAction_thenVerify() throws InterruptedException {
        WebElement sourceElement = driver4.findElement(By.xpath("//p[normalize-space()='Drag me to my target']"));
        WebElement targetElement = driver4.findElement(By.xpath("//div[@id='droppable']"));

        // Perform mouse action
        Actions actions = new Actions(driver4);
        actions.dragAndDrop(sourceElement, targetElement).build().perform();
        Thread.sleep(2000);

    }
}


