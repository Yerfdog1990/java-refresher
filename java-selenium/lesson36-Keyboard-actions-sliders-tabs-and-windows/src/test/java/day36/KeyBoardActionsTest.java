package day36;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
@Slf4j
public class KeyBoardActionsTest {

    WebDriver driver = new ChromeDriver();
    @BeforeEach
    public void setUp() {
        driver.manage().window().maximize();
        driver.get("https://text-compare.com/");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Switch to the new tab
        driver.switchTo().newWindow(WindowType.TAB);
        driver.get("https://www.nopcommerce.com/en");
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if(driver != null){
            driver.close();
        }

    }

    @Test
    public void givenWebElement_whenPerformKeyboardAction_thenCoyAndPasteText() throws InterruptedException {
        // Locate the source text area
        WebElement sourceTextArea = driver.findElement(By.xpath("//textarea[@id='inputText1']"));

        String inputText = "What is Lorem Ipsum?\n" +
                "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum.";

        // Paste input text
        sourceTextArea.sendKeys(inputText);

        // Create the Actions object
        Actions actions = new Actions(driver);

        // Command + A -> Select All
        actions.keyDown(Keys.COMMAND).sendKeys("a").keyUp(Keys.COMMAND).perform();

        // Sleep for 2 seconds
        Thread.sleep(2000);

        // Command + C -> Copy
        actions.keyDown(Keys.COMMAND).sendKeys("c").keyUp(Keys.COMMAND).perform();

        // tab -> Switch to the target text area
        actions.keyDown(Keys.TAB).keyUp(Keys.TAB).perform();

        // Command + V -> Paste
        actions.keyDown(Keys.COMMAND).sendKeys("v").keyUp(Keys.COMMAND).perform();

        // Sleep for 2 seconds
        Thread.sleep(5000);

        // Locate the target text area
        WebElement targetTextArea = driver.findElement(By.xpath("//textarea[@id='inputText2']"));

        // Get the text from the target text area
        String targetText = targetTextArea.getAttribute("value");

        // Assert that the text is the same as the input text
        assertThat(targetText).isEqualTo(inputText);
    }

    @Test
    public void givenWebElement_whenPerformKeyboardAction_thenOpenLinkInNewTab() throws InterruptedException {
        // Locate the registration link
        WebElement registerLink = driver.findElement(By.linkText("Register"));
        log.info("Registration text: [{}]", registerLink.getText());

        // Create the Actions object
        Actions actions = new Actions(driver);

        // Click on the registration link to open it in a new tab using Command + Click
        actions.keyDown(Keys.COMMAND).click(registerLink).keyUp(Keys.COMMAND).perform();

       // Switch to the registration tab
        ArrayList<String> windowHandles = new ArrayList<>(driver.getWindowHandles());
        log.info("Window handles: {}", windowHandles);
        driver.switchTo().window(windowHandles.get(windowHandles.size() - 1));
        log.info("Switched to window: {}, Title: {}", driver.getWindowHandle(), driver.getTitle());

        // Enter personal details
        driver.findElement(By.xpath("//input[@id='FirstName']")).sendKeys("John");
        driver.findElement(By.xpath("//input[@id='LastName']")).sendKeys("Doe");
        driver.findElement(By.xpath("//input[@id='Email']")).sendKeys("joe@example.com");
        driver.findElement(By.xpath("//input[@id='ConfirmEmail']")).sendKeys("joe@example.com");
        driver.findElement(By.xpath("//input[@id='Username']")).sendKeys("Jodoe");

        // Sleep for 2 seconds
        Thread.sleep(2000);

        // Check availability of username
        driver.findElement(By.xpath("//input[@id='check-availability-button']")).click();
    }

}
