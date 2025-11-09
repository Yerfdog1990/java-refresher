
---

# **Module 1: Learn Selenium with Java to Run Automated Tests**

---

## **1. Overview**

When your web app ships new features every other week, **manual testing becomes inefficient**. You need **automated tests** that are fast, reliable, and scalable.

That’s where **Selenium with Java** comes in. Selenium is one of the most trusted open-source automation tools, and Java remains one of the most widely used languages for automation.

> 💡 *In 2025, 29.4% of developers still use Java (StackOverflow Developer Survey, 2025).*

---

## **2. Why Use Selenium with Java?**

| Advantage                            | Description                                                                 |
| ------------------------------------ | --------------------------------------------------------------------------- |
| **Wide Adoption & Support**          | Java has a massive developer community and abundant Selenium documentation. |
| **Stable & Reliable**                | Java-based Selenium tests are known for consistent execution.               |
| **Testing Framework Integration**    | Works seamlessly with TestNG, JUnit, and Maven.                             |
| **Cross-Platform**                   | Runs smoothly on Windows, Linux, and macOS.                                 |
| **Rich Libraries**                   | Access libraries for reporting (Extent, Allure), logging, assertions, etc.  |
| **Performance**                      | Java can outperform dynamic languages like Python in test execution.        |
| **Robust IDEs**                      | Eclipse and IntelliJ IDEA provide excellent support for Selenium projects.  |
| **Parallel & Cross-Browser Testing** | Scales easily with Selenium Grid, TestNG, and BrowserStack.                 |
| **CI/CD Integration**                | Connects seamlessly with Jenkins, GitHub Actions, and other DevOps tools.   |

---

## **3. Getting Started with Selenium Automation Framework in Java**

### Selenium supports:

* **Multiple Operating Systems** – Windows, Linux, macOS, Solaris
* **Multiple Browsers** – Chrome, Firefox, Edge, Safari
* **CI/CD Integration** – Jenkins, Maven, Docker
* **Testing Tools** – TestNG and JUnit for structure and reports

---

## **4. Pre-requisites**

Before you begin, make sure you have:

* ✅ Installed **Java JDK**
* ✅ Installed **Eclipse or IntelliJ IDEA**
* ✅ Downloaded **Selenium Java Client & WebDriver bindings**
* ✅ Configured **WebDriver with your IDE**

---

## **5. Step-by-Step Setup**

### **Step 1 – Install Java (JDK)**

1. Download and install JDK from [Oracle](https://www.oracle.com/java/) or [OpenJDK](https://openjdk.org/).
2. Set the environment variable `JAVA_HOME`.
3. Verify the installation:

   ```bash
   java -version
   ```

---

### **Step 2 – Install Eclipse IDE**

1. Download Eclipse IDE for Java Developers.
2. Extract and launch `eclipse.exe`.
3. Create a **workspace** to store all test projects.

---

### **Step 3 – Download Selenium Client & WebDriver**

1. Visit the [Selenium official site](https://www.selenium.dev/downloads/).
2. Download **Selenium Java Client Driver**.
3. Extract the JARs and keep them ready to add to your project.

---

### **Step 4 – Configure Selenium WebDriver in Eclipse**

1. Create a **new Java Project** → *File → New → Java Project*
2. Create a **Package** under `src`.
3. Add a **Class** file (e.g., `FirstTestInSelenium`).
4. Add external Selenium JARs:

    * Right-click Project → *Properties → Java Build Path → Add External JARs*
    * Select all downloaded Selenium JARs.
5. Apply and close.

---

### **Step 5 – Create and Run Your First Test**

#### **Example: Open Google Homepage**

```java
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class FirstTestInSelenium {
    public static void main(String[] args) {
        // Set path to ChromeDriver
        System.setProperty("webdriver.chrome.driver", ".\\Driver\\chromedriver.exe");

        // Initialize ChromeDriver
        WebDriver driver = new ChromeDriver();

        // Waits and setup
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        // Open website
        driver.get("https://www.google.com");
        System.out.println("Title: " + driver.getTitle());

        // Close browser
        driver.close();
    }
}
```

---

## **6. Running Tests Locally**

1. **Install Java JDK**
2. **Install IDE (Eclipse/IntelliJ)**
3. **Set Up Maven Project**

**Example `pom.xml`:**

```xml
<dependencies>
  <dependency>
    <groupId>org.seleniumhq.selenium</groupId>
    <artifactId>selenium-java</artifactId>
    <version>4.32.0</version>
  </dependency>
</dependencies>
```

4. **Write Your First Selenium Test**

```java
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class MyFirstTest {
    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");
        WebDriver driver = new ChromeDriver();

        driver.get("https://www.google.com");
        System.out.println("Title: " + driver.getTitle());

        driver.quit();
    }
}
```

---

## **7. Running Selenium Tests on the Cloud (BrowserStack Example)**

### **Steps:**

1. **Get BrowserStack Credentials** (username & access key)
2. **Include Dependencies in `pom.xml`:**

```xml
<dependency>
  <groupId>org.seleniumhq.selenium</groupId>
  <artifactId>selenium-java</artifactId>
  <version>4.32.0</version>
</dependency>
<dependency>
  <groupId>junit</groupId>
  <artifactId>junit</artifactId>
  <version>4.13.2</version>
  <scope>test</scope>
</dependency>
```

3. **Write Remote WebDriver Test:**

```java
import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;

public class BStackTest {
    private WebDriver driver;

    @Before
    public void setUp() throws Exception {
        String USERNAME = System.getenv("BROWSERSTACK_USERNAME");
        String ACCESS_KEY = System.getenv("BROWSERSTACK_ACCESS_KEY");
        String URL = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browser", "Chrome");
        caps.setCapability("os", "Windows");
        caps.setCapability("os_version", "11");
        caps.setCapability("name", "BrowserStackTest");

        driver = new RemoteWebDriver(new URL(URL), caps);
    }

    @Test
    public void testGoogleTitle() {
        driver.get("https://www.google.com");
        System.out.println("Page title is: " + driver.getTitle());
    }

    @After
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
```

---

## **8. Running Tests in Parallel with TestNG**

**Dependencies:**

```xml
<dependency>
  <groupId>org.testng</groupId>
  <artifactId>testng</artifactId>
  <version>7.11.0</version>
  <scope>test</scope>
</dependency>
```

**Example Tests:**

```java
// TestGoogle.java
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class TestGoogle {
    @Test
    public void testGoogle() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.google.com");
        System.out.println("Title: " + driver.getTitle());
        driver.quit();
    }
}
```

```java
// TestBing.java
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;

public class TestBing {
    @Test
    public void testBing() {
        WebDriver driver = new ChromeDriver();
        driver.get("https://www.bing.com");
        System.out.println("Title: " + driver.getTitle());
        driver.quit();
    }
}
```

**Parallel Execution (testng.xml):**

```xml
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="ParallelSuite" parallel="classes" thread-count="2">
  <test name="ParallelTests">
    <classes>
      <class name="TestGoogle"/>
      <class name="TestBing"/>
    </classes>
  </test>
</suite>
```

Run from IDE or with:

```bash
mvn clean test
```

---

## **9. Advanced Use Cases**

* **Parallel Test Execution** – Run faster using Selenium Grid or TestNG.
* **Cross-Browser Testing** – Verify app behavior across browsers.
* **Cloud Testing** – Scale on BrowserStack or SauceLabs.
* **CI/CD Integration** – Automate testing in Jenkins or GitHub Actions.
* **Page Object Model (POM)** – Simplify maintenance.
* **Data-Driven Tests** – Read input data from Excel or databases.
* **Headless Testing** – Run faster in CI using headless Chrome.

---

## **10. Best Practices**

✅ **Use the Right Locator:** Prefer `ID` or `Name` for stability.
✅ **Apply Waits Correctly:** Use **implicit** or **explicit waits**, not `Thread.sleep()`.
✅ **Keep Tests Generic:** Avoid hardcoding browsers; parameterize them.
✅ **Use Assertions:** Validate expected vs. actual results with TestNG or JUnit.
✅ **Capture Screenshots:** Useful for debugging and reporting failures.
✅ **Follow Page Object Model:** Separate locators and actions for maintainability.

---

## **11. Why Run Selenium Tests on a Real Device Cloud**

* Realistic user conditions
* Enhanced security and coverage
* Performance metrics and debugging
* Seamless CI/CD integration
* Scalable and cost-effective testing

---

## **12. Conclusion**

Selenium with Java enables **efficient, repeatable, and scalable automation** across browsers and platforms.
When combined with frameworks like TestNG, CI/CD tools, and real-device clouds such as BrowserStack, you get an **end-to-end automated testing ecosystem** that ensures software quality at scale.

---

