
---

# 📘 **Advanced Locators: XPath Axes**

*From the session: Selenium with Java | Advanced Locators – XPath Axes*

XPath Axes represent one of the most powerful and flexible locator strategies in Selenium automation. They allow you to locate elements **not by their attributes**, but by their **relationship** with other elements in the DOM tree. This is extremely important when you encounter:

* Elements without a unique id
* Dynamic attributes
* Deeply nested elements
* UI frameworks rendering complex HTML structures
* Data tables where you must locate values relative to labels

Understanding XPath Axes requires a solid grasp of the DOM as a hierarchical **tree structure**, where every node has relationships such as:

* Parent
* Child
* Ancestor
* Descendant
* Sibling

XPath Axes allow you to navigate between these relationships precisely and reliably.

---

# 🌳 **1. Deep Dive Into the DOM Tree Structure**

Before using XPath axes, you must visualize the DOM the same way Selenium sees it.

Example DOM snippet from nopCommerce:

```html
<ul class="top-menu notmobile">
    <li><a href="/computers">Computers</a></li>
    <li><a href="/electronics">Electronics</a></li>
    <li><a href="/apparel">Apparel</a></li>
</ul>
```

### From this tree:

* `<li>` elements are **siblings**
* Each `<a>` element is a **child** of `<li>`
* `<ul>` is the **parent** of `<li>`
* `<ul>` is the **ancestor** of `<a>`
* `<a>` elements are **descendants** of `<ul>`

Understanding these relationships is what makes XPath Axes so powerful.

---

# 🔧 **2. XPath Axes Syntax Explained**

```
/startingNode/axis_name::targetNode[conditions]
```

### Component Breakdown:

| Component        | Meaning                                                      |
| ---------------- | ------------------------------------------------------------ |
| **startingNode** | Your anchor element (unique locator)                         |
| **axis_name**    | Direction of traversal (parent, child, sibling, ancestor, …) |
| **targetNode**   | Which element you want to find                               |
| **condition**    | Optional filter such as `[@class='something']` or `[1]`      |

---

# 🧭 **3. The Most Important XPath Axes**

## **3.1 `self::` Axis**

### ✔ What it does

Selects the current node itself.

### ✔ Why important?

Useful when:

* You locate an element in a generic way, but want to confirm its tag type.
* You use complex locators and want to validate that the final node is indeed the expected element.

### ✔ Example:

```xpath
//a[contains(text(),'Computers')]/self::a
```

This ensures the final resolved node is exactly the `<a>` element.

---

## **3.2 `parent::` Axis**

### ✔ What it does

Selects the **immediate parent** of the current node.

### ✔ Use cases

* The child element is unique but the parent is what you want.
* Dropdown structures where `<a>` is inside a `<li>` that you need to click.
* Label/input pairs where the input has no attributes but label is unique.

### ✔ Example:

```xpath
//a[text()='Computers']/parent::li
```

Shorthand:

```xpath
//a[text()='Computers']/..
```

---

## **3.3 `child::` Axis**

### ✔ What it does

Selects **direct children** of the current node.

### ✔ Use cases

* Fetching list items in menus
* Rows in tables
* Items in containers

### ✔ Example:

```xpath
//ul[@class='top-menu notmobile']/child::li
```

Equivalent shorthand:

```xpath
//ul[@class='top-menu notmobile']/li
```

---

## **3.4 `ancestor::` Axis**

### ✔ What it does

Selects **all ancestor nodes** of the current node including parent, grandparent, etc.

### ✔ Use cases

* Locating the main container holding a nested link
* Checking a page section by starting from a unique link/text
* Handling nested components in UI frameworks (Angular, React, etc.)

### ✔ Example:

```xpath
//a[text()='Register']/ancestor::div[@class='header-links']
```

This finds the container wrapping the header link area.

---

## **3.5 `descendant::` Axis**

### ✔ What it does

Selects **all nested elements** inside a node (children, grandchildren, etc.)

### ✔ Use cases

* Narrowing down the search area inside a specific container
* Avoiding global XPath queries
* Locating menu items deeply nested under multiple `<div>` layers

### ✔ Example:

```xpath
//div[@class='header-menu']/descendant::a[contains(text(),'Apparel')]
```

---

## **3.6 `following-sibling::` & `preceding-sibling::`**

These axes operate on elements **with the same parent**.

### ✔ `following-sibling::`

Selects siblings that come *after* the current node.

Example:

```xpath
//li[a[contains(text(),'Computers')]]/following-sibling::li[1]/a
```

### ✔ `preceding-sibling::`

Selects siblings that come *before* the current node.

Example:

```xpath
//li[a[contains(text(),'Electronics')]]/preceding-sibling::li[1]/a
```

---

# 🧪 **4. Fully Detailed Selenium Code (JUnit 5)**

Below is a **complete test suite** using all XPath Axes concepts explained above.
Each test includes:

* What the XPath does
* Why the axis is valid
* Assertions to validate correctness
* Logging for verification

```java
public class XPathAxesDemoTest {

    private static WebDriver driver;
    private static final Logger LOG = LoggerFactory.getLogger(XPathAxesDemoTest.class.getName());

    @BeforeEach
    public void setup() {
        driver = new ChromeDriver();
        driver.get("https://demo.nopcommerce.com/");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    // ------------------------------------------
    // 1. SELF AXIS
    // ------------------------------------------
    @Test
    public void givenWebElements_whenFindCurrentNode_thenVerify() {
        WebElement element = driver.findElement(
                By.xpath("//a[contains(text(),'Computers')]/self::a")
        );

        LOG.info("Self axis element text: {}", element.getText());
        assertEquals("Computers", element.getText());
    }

    // ------------------------------------------
    // 2. PARENT AXIS
    // ------------------------------------------
    @Test
    public void givenWebElements_whenFindParentNode_thenVerify() {
        WebElement parent = driver.findElement(
                By.xpath("//a[contains(text(),'Computers')]/parent::li")
        );

        LOG.info("Parent tag: {}", parent.getTagName());
        assertEquals("li", parent.getTagName());
    }

    // ------------------------------------------
    // 3. CHILD AXIS
    // ------------------------------------------
    @Test
    public void givenWebElements_whenFindChildNode_thenVerify() {
        List<WebElement> children = driver.findElements(
                By.xpath("//ul[@class='top-menu notmobile']/child::li")
        );

        LOG.info("Number of child <li> elements: {}", children.size());
        assertTrue(children.size() > 0);
    }

    // ------------------------------------------
    // 4. SIBLING AXIS
    // ------------------------------------------
    @Test
    public void givenWebElements_whenFindSiblingNode_thenVerify() {
        WebElement nextCategory = driver.findElement(
                By.xpath("//li[a[contains(text(),'Computers')]]/following-sibling::li[1]/a")
        );

        LOG.info("Next sibling category: {}", nextCategory.getText());
        assertEquals("Electronics", nextCategory.getText());
    }

    // ------------------------------------------
    // 5. ANCESTOR AXIS
    // ------------------------------------------
    @Test
    public void givenWebElements_whenFindAncestorNode_thenVerify() {
        WebElement ancestor = driver.findElement(
                By.xpath("//a[text()='Register']/ancestor::div[@class='header-links']")
        );

        LOG.info("Ancestor found? {}", ancestor.isDisplayed());
        assertTrue(ancestor.isDisplayed());
    }

    // ------------------------------------------
    // 6. DESCENDANT AXIS
    // ------------------------------------------
    @Test
    public void givenWebElements_whenFindDescendantNode_thenVerify() {
        WebElement apparel = driver.findElement(
                By.xpath("//div[@class='header-menu']/descendant::a[contains(text(),'Apparel')]")
        );

        LOG.info("Descendant link text: {}", apparel.getText());
        assertEquals("Apparel", apparel.getText());
    }
}
```

---

# 🎓 Final Summary

| Axis                  | Meaning                    | Common Use Case                    |
| --------------------- | -------------------------- | ---------------------------------- |
| `self::`              | Selects the element itself | Validation, filtering              |
| `parent::`            | Moves one level up         | Find container from child          |
| `child::`             | Selects direct children    | Menu items, table rows             |
| `ancestor::`          | Moves upward to any level  | Find structure around a child      |
| `descendant::`        | Selects all nested nodes   | Scoped searching inside containers |
| `following-sibling::` | Same-level element after   | Tables, menus                      |
| `preceding-sibling::` | Same-level element before  | Navigation backwards               |

XPath Axes give you **surgical precision** when creating advanced Selenium locators.

---

