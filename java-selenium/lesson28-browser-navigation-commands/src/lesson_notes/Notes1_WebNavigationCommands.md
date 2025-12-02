
---

# ⭐ **Lesson Notes — Selenium Browser & Navigation Commands (Beginner Level)**

When automating web browsers using Selenium WebDriver, we often need to perform actions that mimic how a user naturally moves around the web:

* Opening pages
* Moving backward
* Going forward
* Refreshing pages
* Switching between multiple windows or tabs

Selenium provides a set of **Browser and Navigation Commands** that let WebDriver fully control browser movement and behavior.

These commands make automation predictable, reliable, and more human-like.

---

# 🎯 **Learning Objectives**

By the end of this lesson, a beginner should understand:

* What navigation commands are in WebDriver
* How to use:
  ✔ `navigate().to()`
  ✔ `navigate().back()`
  ✔ `navigate().forward()`
  ✔ `navigate().refresh()`
* The difference between `driver.get()` and `navigate().to()`
* How to switch between browser tabs/windows
* How to close a specific browser window programmatically
* How to use your test structure with JUnit 5

---

# 🧭 **1. What Are Navigation Commands?**

Navigation commands control browser movement. They allow WebDriver to:

* Load a new URL
* Go back in browser history
* Go forward after going back
* Refresh the current page

These commands are accessed through:

```java
driver.navigate()
```

---

# 🌐 **2. The `navigate().to()` Command**

### ✔ What It Does

Loads a new web page using a URL.

Works similarly to:

```java
driver.get("https://example.com");
```

### ✔ Syntax

```java
driver.navigate().to(url);
```

### ✔ Example From Your Code

```java
@Test
public void givenUrl_whenNavigateTo_thenVerifyPageTitle() throws MalformedURLException {
    url = new URL("https://www.orangehrm.com/");
    driver.navigate().to(url);

    String title = driver.getTitle();
    LOG.info("Page title: {}", title);

    assertTrue(title.contains("OrangeHRM"));
}
```

### ✔ Notes for Beginners

* Perfect for loading a URL dynamically using a `URL` object
* Faster than `driver.get()` because it **does not wait until the entire page loads**

---

# 🔙 **3. The `navigate().back()` Command**

### ✔ What It Does

Moves back one step in the browser’s history.

Same as clicking the browser’s **back button**.

### ✔ Syntax

```java
driver.navigate().back();
```

### ✔ Example From Your Code

```java
@Test
public void givenUrl_whenNavigateBack_thenVerifyPageTitle() throws MalformedURLException {
    url = new URL("https://www.orangehrm.com/");
    driver.navigate().to(url);

    String title = driver.getTitle();
    driver.navigate().back();

    LOG.info("Page title: {}", title);
    assertTrue(title.contains("OrangeHRM"));
}
```

### ✔ When to Use

* After clicking a link
* When moving backward in workflow steps

---

# 🔜 **4. The `navigate().forward()` Command**

### ✔ What It Does

Moves forward in browser history—only works after using `.back()`.

### ✔ Syntax

```java
driver.navigate().forward();
```

### ✔ Example From Your Code

```java
@Test
public void givenUrl_whenNavigateForward_thenVerifyPageTitle() throws MalformedURLException {
    url = new URL("https://www.orangehrm.com/en/company/about-us#");
    driver.navigate().to(url);

    driver.navigate().forward();

    String title = driver.getTitle();
    LOG.info("Page title: {}", title);

    assertTrue(title.contains("OrangeHRM"));
}
```

### ✔ When to Use

* After going back, to simulate user forward navigation

---

# 🔄 **5. The `navigate().refresh()` Command**

### ✔ What It Does

Reloads the current page.

Equivalent to pressing **F5**.

### ✔ Syntax

```java
driver.navigate().refresh();
```

### ✔ Example From Your Code

```java
@Test
public void givenUrl_whenRefreshPage_thenVerifyPageTitle() throws MalformedURLException {
    url = new URL("https://www.orangehrm.com/");
    driver.navigate().to(url);

    String beforeRefresh = driver.getTitle();
    driver.navigate().refresh();
    String afterRefresh = driver.getTitle();

    LOG.info("Page title before refresh: {}", beforeRefresh);
    LOG.info("Page title after refresh: {}", afterRefresh);

    assertEquals(beforeRefresh, afterRefresh);
}
```

### ✔ Why Use It

* Refresh page content
* Retry loading elements
* Reset dynamic page data

---

# 🪟 **6. Switching Between Browser Windows**

Modern websites frequently open new tabs or windows, especially during login, signup, and checkout flows.

Selenium allows you to:

* Retrieve all window handles
* Switch to a specific window
* Compare window titles
* Loop through all windows

### ✔ Example From Your Code

```java
@Test
public void givenUrl_whenSwitchTo_thenVerifyPageTitle() throws MalformedURLException {
    url = new URL("https://www.orangehrm.com/");
    driver.navigate().to(url);

    String parentWindowTitle = driver.getTitle();

    ((JavascriptExecutor) driver)
        .executeScript("window.open('https://www.orangehrm.com/contact-sales/')");

    Set<String> windowHandles = driver.getWindowHandles();
    List<String> windowHandlesList = new ArrayList<>(windowHandles);

    String parentWindowHandle = windowHandlesList.get(0);
    String childWindowHandle = windowHandlesList.get(1);

    driver.switchTo().window(childWindowHandle);
    String childWindowTitle = driver.getTitle();

    LOG.info("Parent window title: {}", parentWindowTitle);
    LOG.info("Child window title: {}", childWindowTitle);

    assertNotEquals(parentWindowTitle, childWindowTitle);

    for (String handle : windowHandles) {
        String title = driver.switchTo().window(handle).getTitle();
        LOG.info("Window title: {}", title);
    }
}
```

### ✔ Beginner Explanation

* A **window handle** is a unique ID for each browser tab
* Switching windows lets you continue automation in a new tab
* Use loops to iterate through all open windows

---

# ❌ **7. Closing a Specific Window**

Selenium allows you to close one tab while keeping others open.

### ✔ Example From Your Code

```java
@Test
public void givenTwoWindows_closeSpecificWindow_thenVerify() throws MalformedURLException {
    url = new URL("https://www.orangehrm.com/");
    driver.navigate().to(url);

    ((JavascriptExecutor) driver)
        .executeScript("window.open('https://www.orangehrm.com/contact-sales/')");

    Set<String> windowHandles = driver.getWindowHandles();

    for (String handle : windowHandles) {
        String title = driver.switchTo().window(handle).getTitle();
        LOG.info("Window title open: {}", title);

        if (title.contains("Contact Sales")) {
            driver.close(); // closes only the matching window
            break;
        }

        LOG.info("Window title not closed: {}", title);
        assertFalse(title.contains("Contact Sales"));
    }
}
```

### ✔ When This Helps

* Closing ads/pop-ups
* Closing child windows after scraping or testing
* Keeping only the main window open

---

# 📝 **Mini Summary (Beginner-Friendly)**

| Command                 | Purpose                             |
| ----------------------- | ----------------------------------- |
| `navigate().to(url)`    | Loads a new page                    |
| `navigate().back()`     | Goes back in history                |
| `navigate().forward()`  | Goes forward                        |
| `navigate().refresh()`  | Reloads the current page            |
| `switchTo().window(id)` | Moves to a specific browser tab     |
| `driver.close()`        | Closes the currently focused window |

---
