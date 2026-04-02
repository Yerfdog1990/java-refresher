# Guide to TestNG Assertions in Selenium-Based Test Automation
### Detailed Notes for Understanding and Applying Assertions in TestNG Frameworks
*Assertions • Hard vs Soft • Assert Methods • Best Practices*

---

## Overview

Testing is a major part of the SDLC and can be performed either manually or in an automated manner. While manual testing benefits from human judgment to spot blockers, automation testing requires an **explicit validation strategy** to determine whether expected results match actual results.

This is where **assertions** come into the picture. Assertions instruct the test execution engine to either throw an exception or halt execution when an expected condition is not met. They are the primary mechanism for marking an automated test as **passed** or **failed**.

---

## What are Assertions in TestNG?

Every major test automation framework — TestNG, JUnit, NUnit, Nightwatch — provides an assertion mechanism for validating end results. In a Selenium-based TestNG framework, assertions are the primary source of determining whether a test case passes or fails.

TestNG provides a built-in `Assert` class with multiple methods to raise assertions.

### Required Import

```java
import org.testng.Assert;
```

### Generic Syntax

```java
Assert.methodName(actual, expected);
```

| Parameter | Description |
|---|---|
| `Assert` | Built-in TestNG class |
| `methodName` | The name of the Assert class method being called |
| `actual` | The value retrieved from the application under test |
| `expected` | The hardcoded or known-correct value to validate against |

### Real-World Example — Login Page

A common use case for assertions is validating a login flow:

1. Open the login page
2. Enter username and password
3. Click submit
4. **Assert** the title of the landing page after logging in

After logging in, Selenium fetches the current page title and `Assert.assertEquals` validates it against the expected title hardcoded in the test script.

---

## Types of Assertions in TestNG

There are two types of assertions in TestNG: **Hard Assertions** and **Soft Assertions**.

---

### 1. Hard Assertion

A hard assertion **immediately throws an exception** and **terminates the test case** the moment an assertion condition fails. The test is marked as failed and execution moves on to the next test case in the suite.

#### Behaviour
- Assertion fails → exception thrown immediately
- Current test case execution stops
- Next test case in the suite continues

#### Example — Hard Assertion for Login Test

```java
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class TestLogin {
    WebDriver driver;

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.pcloudy.com/");
    }

    @Test(priority = 0)
    public void testPCloudyLogin() {
        WebElement loginHeader = driver.findElement(By.xpath("//a[text()='Login']"));
        loginHeader.click();
        WebElement username = driver.findElement(By.id("userId"));
        username.sendKeys("ramit.dhamija@gmail.com");
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys("ramit9876");
        WebElement loginButton = driver.findElement(By.id("loginSubmitBtn"));
        loginButton.click();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        String expectedTitle = "Mobile App Testing, Continuous Testing Cloud, Mobile Testing Tools | pCloudy";
        String actualTitle = driver.getTitle();
        Assert.assertEquals(actualTitle, expectedTitle, "pCloudy Login Test Failed");
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

#### When to Use Hard Assertion

Hard assertions are best used when **subsequent steps are entirely dependent on the current step passing**. The login test is a classic example — if login fails, there is no point continuing any further steps that require an authenticated session.

---

### 2. Soft Assertion

A soft assertion **does not throw an exception immediately** when a condition fails. Instead, it **collects all failures** and reports them all at once when `assertAll()` is explicitly called at the end of the test.

To implement soft assertions, use the `SoftAssert` class instead of the `Assert` class.

#### Behaviour
- Assertion fails → exception is recorded, but execution continues
- All subsequent statements in the test method run
- `assertAll()` is called at the end to throw all collected exceptions at once

#### Example — Soft Assertion for Login Test

```java
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import io.github.bonigarcia.wdm.WebDriverManager;

public class TestLogin {
    WebDriver driver;
    SoftAssert softassert;

    @BeforeTest
    public void setup() {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        softassert = new SoftAssert();
        driver.manage().window().maximize();
        driver.get("https://www.pcloudy.com/");
    }

    @Test(priority = 0)
    public void testPCloudyLogin() {
        WebElement loginHeader = driver.findElement(By.xpath("//a[text()='Login']"));
        loginHeader.click();
        WebElement username = driver.findElement(By.id("userId"));
        username.sendKeys("ramit.dhamija@gmail.com");
        WebElement password = driver.findElement(By.name("password"));
        password.sendKeys("ramit9876");
        WebElement loginButton = driver.findElement(By.id("loginSubmitBtn"));
        loginButton.click();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        String expectedTitle = "Mobile App Testing, Continuous Testing Cloud, Mobile Testing Tools | pCloudy";
        String actualTitle = driver.getTitle();
        softassert.assertEquals(actualTitle, expectedTitle, "pCloudy Login Test Failed");
        System.out.println("Soft Assertion statement is executed");
        softassert.assertAll();
    }

    @AfterTest
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
```

#### When to Use Soft Assertion

Soft assertions are best used when **test steps are independent of each other**. For example, when validating multiple fields on a form — each field validation is independent, so all assertions should run and all failures should be reported at once via `assertAll()`.

---

## Hard Assertion vs Soft Assertion — Comparison

| | Hard Assertion | Soft Assertion |
|---|---|---|
| **Class Used** | `Assert` | `SoftAssert` |
| **On Failure** | Throws exception immediately, stops test | Records exception, continues execution |
| **Exception Thrown** | Instantly at the failing assertion | When `assertAll()` is called |
| **Best Use Case** | Steps with dependencies (e.g., login must pass before proceeding) | Independent steps (e.g., validating multiple form fields) |
| **Test Result** | Marked failed at point of failure | All failures reported together at end |

---

## TestNG Assert Methods

All TestNG assert methods follow the same fundamental pattern but accept different parameter types. Choosing the right method for the right scenario is essential for writing clear, accurate validations.

---

### `Assert.assertEquals(String actual, String expected)`

Validates that the actual string equals the expected string. Throws an assertion exception if they are not equal.

```java
Assert.assertEquals(actualTitle, expectedTitle);
```

---

### `Assert.assertEquals(String actual, String expected, String message)`

Same as above but accepts an additional custom message parameter. The message is displayed alongside the assertion error if the condition fails, making debugging easier.

```java
Assert.assertEquals(actualTitle, expectedTitle, "Page title does not match after login");
```

---

### `Assert.assertEquals(boolean actual, boolean expected)`

Validates that two boolean values are equal.

```java
Assert.assertEquals(isLoggedIn, true);
```

---

### `Assert.assertTrue(condition)`

Asserts that the given condition evaluates to `true`. Throws an assertion error if the condition is `false`.

```java
Assert.assertTrue(driver.getCurrentUrl().contains("dashboard"));
```

---

### `Assert.assertTrue(condition, message)`

Same as above with an additional custom message displayed on assertion failure.

```java
Assert.assertTrue(isElementVisible, "Element should be visible but was not found");
```

---

### `Assert.assertFalse(condition)`

Asserts that the given condition evaluates to `false`. Throws an assertion error if the condition is `true`.

```java
Assert.assertFalse(errorMessage.isDisplayed());
```

---

### `Assert.assertFalse(condition, message)`

Same as above with an additional custom message displayed on assertion failure.

```java
Assert.assertFalse(isErrorDisplayed, "Error message should not be visible after valid login");
```

---

### `Assert.assertNull(object)`

Asserts that the given object or condition returns `null`. Throws an assertion error if it is not null.

```java
Assert.assertNull(driver.findElements(By.id("errorMsg")));
```

---

### `Assert.assertNotNull(object)`

Asserts that the given object or condition returns a non-null value. Throws an assertion error if it is null.

```java
Assert.assertNotNull(driver.findElement(By.id("welcomeMessage")));
```

---

## Quick Reference — Assert Methods Summary

| Method | Passes When | Fails When |
|---|---|---|
| `assertEquals(actual, expected)` | `actual` equals `expected` | Values differ |
| `assertEquals(actual, expected, message)` | `actual` equals `expected` | Values differ — shows message |
| `assertEquals(boolean, boolean)` | Both booleans are equal | Booleans differ |
| `assertTrue(condition)` | Condition is `true` | Condition is `false` |
| `assertTrue(condition, message)` | Condition is `true` | Condition is `false` — shows message |
| `assertFalse(condition)` | Condition is `false` | Condition is `true` |
| `assertFalse(condition, message)` | Condition is `false` | Condition is `true` — shows message |
| `assertNull(object)` | Object is `null` | Object is not null |
| `assertNotNull(object)` | Object is not `null` | Object is null |

---

## Best Practices for Using TestNG in Selenium Automation

### 1. Structure Test Suites
Organise test suites based on logical groupings such as functional areas or test priorities. Divide tests into smaller, focused suites rather than one large suite for better maintainability and easier management.

### 2. Use Descriptive Test Names
Give meaningful names to test methods using descriptive language. This makes it easier to understand the purpose of each test and quickly identify failures or issues.

### 3. Leverage TestNG Annotations
Use lifecycle annotations (`@BeforeMethod`, `@AfterMethod`, `@BeforeClass`, `@AfterClass`) to control test execution flow and define preconditions and postconditions effectively.

### 4. Group and Prioritize Tests
Use the `groups` and `priority` attributes of `@Test` to categorize and prioritize tests. This allows selective execution based on requirements (e.g., run only smoke tests) or ensures critical tests run first.

### 5. Data-Driven Testing
Use `@DataProvider` to separate test data from test logic. Fetch data from external sources such as Excel, CSV files, or databases to enhance coverage and simplify test data maintenance.

### 6. Manage Test Dependencies
Use `dependsOnMethods` or `dependsOnGroups` attributes to manage dependencies between tests. This ensures tests execute in a specific order for smoother, more reliable execution.

### 7. Use Parameterized Tests
Use `@Parameters` to create parameterized tests that execute the same test method with different input values, reducing code duplication and improving versatility.

### 8. Apply Assertions Wisely
Use the appropriate assertion method for each scenario — `assertEquals`, `assertTrue`, etc. Always include descriptive messages in assertions to provide clear failure information for easier debugging.

### 9. Implement TestNG Listeners
Use listeners (`ITestListener`, `ISuiteListener`) to enhance reporting and add custom functionality such as capturing screenshots on failure, logging additional information, or integrating with ExtentReports.

### 10. Run Tests in Parallel
Leverage TestNG's parallel execution feature to run tests simultaneously, reducing overall execution time. Configure parallelism at the method, class, or suite level based on system capabilities and requirements.

### 11. Extend TestNG Reports
Implement custom reporters using `IReporter` or `ITestReporter` to generate tailored test reports with additional information or to integrate with third-party reporting frameworks.

### 12. Integrate with CI/CD
Integrate TestNG with CI/CD tools like Jenkins or Bamboo to trigger automated test executions whenever new code changes are pushed, enabling continuous testing throughout the development pipeline.

### 13. Maintain Test Environment Independence
Design tests to be independent of specific environments. Avoid hard-coding URLs, credentials, or environment-specific values — use configuration files, environment variables, or data providers instead.

### 14. Perform Regular Test Maintenance
Periodically review and update test suites. Remove redundant or obsolete tests, update assertions as the application evolves, and ensure test scripts stay aligned with current requirements.

---

## Conclusion

Assertions are the **core of any test method** in TestNG. Understanding when and how to use hard assertions, soft assertions, and the various assert methods is essential for building an efficient, robust, and maintainable Selenium test automation suite. The right assertion strategy ensures meaningful test results and clear, actionable failure reports.

> For a complete list of available assertion methods, refer to the [TestNG official documentation](https://testng.org/doc/).
