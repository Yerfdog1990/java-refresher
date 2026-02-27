package day35;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class MouseEventActionsTest {
    private static final String URL  = "https://vinothqaacademy.com/mouse-event/";
    private static WebDriver driver;


    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(URL);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        if(driver != null){
            driver.close();
        }
    }

    @Test
    public void givenWebElement_whenPerformHoverAction_thenVerify() throws InterruptedException {
        WebElement hoverButton = driver.findElement(By.xpath("//div[@id='tooltipTarget']"));
        
        // Perform mouse action - hover over the button
        Actions actions = new Actions(driver);
        actions.moveToElement(hoverButton).build().perform();
        
        // Wait for the tooltip to appear
        Thread.sleep(1000);
        
        // Verify the tooltip is displayed after hover
        WebElement hoverStatus = driver.findElement(By.xpath("//span[@id='tooltipStatus']"));
        assertTrue(hoverStatus.isDisplayed());

        // Verify hover status
        String statusMessage = hoverStatus.getText();
        log.debug("Status message: {}", statusMessage);
        assertThat(statusMessage.contains("Tooltip Visible ✅")).isTrue();
    }

    @Test
    public void givenWebElement_whenPerformRightClickAction_thenVerify() throws InterruptedException {
        WebElement button = driver.findElement(By.xpath("//button[@id='rightBtn']"));

        // Perform mouse action
        Actions actions = new Actions(driver);
        actions.contextClick(button).build().perform();

        // Wait for the context menu to appear
        Thread.sleep(1000);

        // Click copy
        WebElement copyBtn = driver.findElement(By.xpath("//button[normalize-space()='Copy']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", copyBtn);

        // Verify copy status
        WebElement status = driver.findElement(By.xpath("//span[@id='rightStatus']"));

        // Wait for the context menu to appear
        Thread.sleep(1000);

        // Verify the attribute value
        String statusAttribute = status.getText();
        log.debug("Status attribute: {}", statusAttribute);
        assertThat(statusAttribute.contains("Copy")).isTrue();

    }

    @Test
    public void givenWebElement_whenPerformDoubleClickAction_thenVerify() throws InterruptedException {

        WebElement button = driver.findElement(By.xpath("//button[@id='doubleBtn']"));

        // Perform mouse action - hover over the button
        Actions actions = new Actions(driver);
        actions.moveToElement(button).build().perform();

        // Wait for the tooltip to appear
        Thread.sleep(1000);

        // Verify the tooltip is displayed after double-click
        WebElement doubleClickStatus = driver.findElement(By.xpath("//span[@id='doubleStatus']"));
        assertTrue(doubleClickStatus.isDisplayed());

        // Verify double status
        String statusMessage = doubleClickStatus.getText();
        log.debug("Status message: {}", statusMessage);
        assertThat(statusMessage.contains("Double Click Detected ✅")).isTrue();

    }

    @Test
    public void givenWebElement_whenPerformDragAndDropAction_thenVerify() throws InterruptedException {
        WebElement sourceElement = driver.findElement(By.xpath("//div[@id='dragItem']"));
        WebElement targetElement = driver.findElement(By.xpath("//div[@id='dropZone']"));

        // Perform mouse action
        Actions actions = new Actions(driver);
        //actions.moveToElement(sourceElement).clickAndHold().moveToElement(targetElement).release().build().perform();
        actions.dragAndDrop(sourceElement, targetElement).build().perform();
        Thread.sleep(2000);

        // Verify the tooltip is displayed after drag-and-drop
        WebElement status = driver.findElement(By.xpath("//span[@id='dragStatus']"));
        assertTrue(status.isDisplayed());

        // Wait for the drag operation to complete
        Thread.sleep(2000);
        String statusText = status.getText();
        assertEquals("Dropped Successfully ✅", statusText);

        // Reset the position of the source element and verify
        WebElement resetBtn = driver.findElement(By.xpath("//button[@id='resetBtn']"));
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", resetBtn);
        
        // Wait for the reset operation to complete
        Thread.sleep(2000);
        String resetText = status.getText();
        assertEquals("Reset Done ✅", resetText);
    }

    @Test
    public void givenWebElement_whenPerformDragSliderAction_thenVerify() throws InterruptedException {
        WebElement sliderElement = driver.findElement(By.xpath("//div[@id='handle_max']"));

        // Get the current value of the slider
        WebElement sliderValue = driver.findElement(By.xpath("//span[@id='sliderValueText']"));
        String initialValue = sliderValue.getText();
        log.debug("Initial slider value: {}", initialValue);

        // Perform mouse action - drag the slider handle
        Actions actions = new Actions(driver);
        // Moving by 100 pixels to the right
        actions.dragAndDropBy(sliderElement, 300, 0).build().perform();

        // Wait for the slider to move
        Thread.sleep(2000);

        // Verify the slider value has changed
        String updatedValue = sliderValue.getText();
        log.debug("Updated slider value: {}", updatedValue);
        assertThat(updatedValue).isNotEqualTo(initialValue);

        // Verify the status message
        WebElement sliderStatus = driver.findElement(By.xpath("//span[@id='sliderStatus']"));
        String statusText = sliderStatus.getText();
        log.debug("Slider status text: {}", statusText);
        assertThat(statusText).contains("Value: £");
        assertThat(statusText).contains("✅");
    }
}

