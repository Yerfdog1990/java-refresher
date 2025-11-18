package day25;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;


import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayNameGeneration(CustomDisplayNameGenerator.class)
public class XPathAxesDemoTest {
    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(XPathAxesDemoTest.class);
    private static final String EBAY_URL = "https://www.ebay.com/";

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(EBAY_URL);
        // Using a short implicit wait for this demo
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // --- 1. Self-axes -> Select the current node ---
    @Test
    public void givenWebElements_whenFindCurrentNode_thenVerify() {
        WebElement element = driver.findElement(By.xpath("//input[@id='gh-ac']/self::input"));
        String searchTerm = "MacBook";
        element.sendKeys(searchTerm);

        String searchResult = element.getAttribute("value");
        LOG.info(()->"Search Result (Self-axes): " + searchResult);
        assertEquals(searchTerm, searchResult);
    }

    // --- 2. Parent axes -> Select the parent of the current node ---
    @Test
    public void givenWebElements_whenFindParentNode_thenVerify() {
        WebElement element = driver.findElement(By.xpath("//*[@id=\"gh-ac-wrap\"]/parent::*"));
        LOG.info(()->"Parent Element (Parent-axes): " + element.getTagName());
        String parentTagName = element.getTagName();
        assertEquals("div", parentTagName, "Expected the parent to be a <div> tag.");
    }
    // --- 3. Child axes -> Select the children of the current node ---
    @Test
    public void givenWebElements_whenFindChildNode_thenVerify() {
        List<WebElement> childElements = driver.findElements(
                By.xpath("//form[@id='gh-f']/child::*")
        );

        LOG.info(()->"Number of Child Nodes (Child-axes): " + childElements.size());
        assertTrue(childElements.size() >= 4, "Expected more than 4 direct child elements in the search form.");
    }

    // --- 4. Sibling axes -> Select the siblings of the current node ---
    @Test
    public void givenWebElements_whenFindSiblingNode_thenVerify() {
        List<WebElement> followingSiblingElements = driver.findElements(By.xpath("//*[@id=\"mainContent\"]//following-sibling::*"));
        LOG.info(()->"Number of Following Sibling Nodes (Sibling-axes): " + followingSiblingElements.size());
        assertTrue(followingSiblingElements.size() >= 600, "Expected at least 600 sibling elements.");

        List<WebElement> precedingSiblingElements = driver.findElements(By.xpath("//*[@id=\"mainContent\"]//preceding-sibling::*"));
        LOG.info(()->"Number of Preceding Sibling Nodes (Preceding-axes): " + precedingSiblingElements.size());
        assertTrue(precedingSiblingElements.size() >= 600, "Expected at least 600 preceding sibling elements.");
    }

    // --- 5. Ancestor axes -> Select the ancestors of the current node ---
    @Test
    public void givenWebElements_whenFindAncestorNode_thenVerify() {
        List<WebElement> ancestorDivs = driver.findElements(
                By.xpath("//a[text()='All Categories']/ancestor::div")
        );

        LOG.info(()->"Number of <div> Ancestor Nodes (Ancestor-axes): " + ancestorDivs.size());
        assertTrue(ancestorDivs.size() > 5, "Expected many ancestor <div> elements for the 'All Categories' link.");

        WebElement closestAncestor = driver.findElement(
                By.xpath("//a[text()='All Categories']/ancestor::div[1]")
        );
        assertTrue(closestAncestor.getAttribute("class").contains("gh-categories__foot"));
    }

    // --- 6. Descendant axes -> Select the descendants of the current node ---
    @Test
    public void givenWebElements_whenFindDescendantNode_thenVerify() {
        List<WebElement> descendantInputs = driver.findElements(By.xpath("//form[@id='gh-f']/descendant::input"));

        LOG.info(()->"Number of <input> Descendant Nodes (Descendant-axes): " + descendantInputs.size());
        assertTrue(descendantInputs.size() >= 1, "Expected at least one <input> descendant node (the search box).");

        assertEquals("gh-ac", descendantInputs.getFirst().getAttribute("id"),
                "The first descendant input should be the main search box.");
    }
}