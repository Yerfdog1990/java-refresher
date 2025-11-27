
---

# 📘 **Lesson Notes: Selenium Wait Methods**

### *Why Do We Need Waits?*

Before interacting with elements, Selenium expects them to be *ready*.
But in real applications, elements may be delayed because of:

* Network speed
* Server response time
* JavaScript rendering
* AJAX calls
* CSS transitions
* Lazy loading (common in modern UI frameworks)

If Selenium tries interacting *too early*, you get failures like:

* `NoSuchElementException`
* `ElementNotInteractableException`
* `StaleElementReferenceException`
* `TimeoutException`

This is why **waits** are essential:
👉 They allow Selenium to pause *smartly* until the element is ready.

---

# 🧩 **What Is a Selenium Wait?**

A Selenium Wait is a mechanism that instructs WebDriver to **wait for a condition** before continuing test execution.

Examples of conditions:

* Element appears in the DOM
* Element becomes visible
* Page title changes
* AJAX call finishes
* Alert appears
* URL contains text

This creates **stable**, **reliable**, and **fast-running** automated tests.

There are **3 official Selenium waits**:

1. **Implicit Wait**
2. **Explicit Wait**
3. **Fluent Wait**

Let’s go deeper.

---

# ⏱️ **1. Implicit Wait**

### ✔ Definition

A global timeout that applies to **all WebDriver element lookups**.

### ✔ How Selenium uses it

When `findElement()` is called, WebDriver will keep retrying until either:

* the element appears (test continues), or
* timeout runs out (exception thrown)

### ✔ When Implicit Wait is useful

* Simple static websites
* Basic test suites
* When all elements load predictably

### ✔ When NOT to use Implicit Wait

* React/Angular/Vue apps
* AJAX-heavy pages
* When you need different wait times per element
* When using Explicit Waits → **causes unpredictable behavior**

### ✔ Code Example (From your Test Class)

```java
driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

WebElement emailInput = driver.findElement(By.xpath("//input[@id='gh-ac']"));
emailInput.sendKeys("MacBook");
emailInput.submit();
```

---

# 🎯 **2. Explicit Wait (WebDriverWait)**

Explicit wait is the **most important and most used** wait in Selenium.

### ✔ Definition

A wait applied to **a specific element** with **a specific condition**.

### ✔ How It Works

* Polls the DOM every **500ms (default)**
* If the condition is met → continue
* If timeout expires → throw `TimeoutException`

### ✔ Why Explicit Wait is better than Implicit Wait

* Condition-based
* Element-specific
* Much faster (stops as soon as condition is true)
* Doesn’t slow down unrelated elements
* Works with visibility, clickability, frames, alerts, URLs, and more

---

## ⭐ **FULL LIST of Explicit Wait Methods**

*(Use this list for interviews + documentation)*

### ▶ Element State Conditions

| Expected Condition                             | Description                   |
| ---------------------------------------------- | ----------------------------- |
| `visibilityOf(element)`                        | Element must be visible       |
| `visibilityOfElementLocated(locator)`          | Located AND visible           |
| `presenceOfElementLocated(locator)`            | Exists in DOM (may be hidden) |
| `elementToBeClickable(locator)`                | Visible + enabled             |
| `elementToBeSelected(locator)`                 | Is selected (checkbox, radio) |
| `elementSelectionStateToBe(locator, selected)` | True/false state              |

---

### ▶ Text Conditions

| Expected Condition                               | Description         |
| ------------------------------------------------ | ------------------- |
| `textToBePresentInElement(element, text)`        | Wait for inner text |
| `textToBePresentInElementLocated(locator, text)` | Same with locator   |
| `textToBe(element, text)`                        | Full text match     |
| `textToBePresentInElementValue(locator, text)`   | Input value         |

---

### ▶ Visibility / Invisibility Conditions

| Condition                               | Description               |
| --------------------------------------- | ------------------------- |
| `invisibilityOf(element)`               | Element hidden or removed |
| `invisibilityOfElementLocated(locator)` | Locator version           |
| `stalenessOf(element)`                  | Element detached from DOM |

---

### ▶ Alert Conditions

| Condition          | Description         |
| ------------------ | ------------------- |
| `alertIsPresent()` | Alert popup appears |

---

### ▶ Frame Conditions

| Condition                                  | Description               |
| ------------------------------------------ | ------------------------- |
| `frameToBeAvailableAndSwitchToIt(locator)` | Frame ready → auto-switch |

---

### ▶ Window & URL Conditions

| Condition                    | Description            |
| ---------------------------- | ---------------------- |
| `titleIs(title)`             | Exact page title       |
| `titleContains(text)`        | Partial title match    |
| `urlToBe(url)`               | Exact URL              |
| `urlContains(text)`          | URL contains substring |
| `numberOfWindowsToBe(count)` | Window count changes   |

---

## 🧪 Explicit Wait Example — From Your Test Class

### ✔ Visibility Example

```java
WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

WebElement emailInput = wait.until(
        ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='gh-ac']")
        )
);
```

### ✔ Clickability Example

```java
WebElement emailInput = wait.until(
        ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='gh-ac']"))
);
```

---

# 🚀 **3. Fluent Wait (Advanced Explicit Wait)**

Fluent Wait = Explicit Wait **+ extra control**

### ✔ What Fluent Wait Allows

| Feature           | Description            |
| ----------------- | ---------------------- |
| Custom timeout    | How long to wait       |
| Custom polling    | How often to check     |
| Ignore exceptions | Prevent early failures |
| Custom conditions | Write your own lambda  |

### ✔ When to Use Fluent Wait

* AJAX-heavy sites
* Dynamic tables
* Loading spinners
* Slowly updating elements
* Waiting for attribute changes

---

## ⭐ **FULL LIST of Fluent Wait Configuration Methods**

### 1️⃣ `.withTimeout(Duration)`

Sets the maximum wait time.

### 2️⃣ `.pollingEvery(Duration)`

Defines how frequently Selenium checks the condition.

### 3️⃣ `.ignoring(Exception.class)`

Specifies exceptions to skip while waiting (common: `NoSuchElementException`).

### 4️⃣ `.until(Function)`

Defines your custom condition logic.

---

## 🧪 Fluent Wait Example (From Your Test Class)

```java
Wait<WebDriver> wait = new FluentWait<>(driver)
        .withTimeout(Duration.ofSeconds(30L))
        .pollingEvery(Duration.ofSeconds(2L))
        .ignoring(NoSuchElementException.class);

WebElement searchInput = wait.until(driver -> {
    WebElement element = driver.findElement(By.id("gh-ac"));
    return element.isDisplayed() ? element : null;
});
```

Here’s what happens:

1. Check for `#gh-ac` every 2 seconds
2. If not found → ignore exception
3. If found AND visible → return element
4. If not found within 30 seconds → timeout

---

# 🔥 Difference Between Selenium Wait and Thread.sleep()

| Topic            | Selenium Wait          | Thread.sleep   |
| ---------------- | ---------------------- | -------------- |
| Type             | Dynamic                | Static         |
| Efficiency       | High                   | Low            |
| Stops early?     | ✔ Yes if condition met | ❌ No           |
| Uses conditions? | ✔ Yes                  | ❌ No           |
| Robustness       | High                   | Low            |
| Recommended?     | ✔ Yes                  | ❌ Generally No |

---

# 🎓 Summary Table

| Wait Type         | Scope            | Condition-Based? | Use Case                  |
| ----------------- | ---------------- | ---------------- | ------------------------- |
| **Implicit Wait** | Global           | ❌ No             | Simple apps               |
| **Explicit Wait** | Element-specific | ✔ Yes            | Most tests                |
| **Fluent Wait**   | Element-specific | ✔ Yes            | AJAX-heavy, dynamic pages |
| **Thread.sleep**  | Entire thread    | ❌ No             | Debug-only                |

---

