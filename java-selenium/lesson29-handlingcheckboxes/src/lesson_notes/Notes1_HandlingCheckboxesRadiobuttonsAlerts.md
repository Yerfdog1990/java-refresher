
---

# **Lesson Notes: Handling Checkboxes, Radio Buttons, and Alerts in Selenium**

Selenium WebDriver allows us to interact with various UI components such as **checkboxes**, **radio buttons**, and **browser alerts**. These are common elements in almost all web applications, so understanding how to identify, select, and control them is essential for building stable automation scripts.

This lesson covers:

1. What checkboxes and radio buttons are
2. How to select, unselect, and conditionally handle them
3. Handling multiple checkboxes
4. Handling JavaScript alerts (Simple, Confirmation, Prompt)
5. Essential Selenium methods used for these elements

---

# **1. Handling Checkboxes**

A **checkbox** allows the user to select **multiple options** at once.
In Selenium:

* `click()` is used to **select** or **unselect** a checkbox
* The same method is used for both operations
* `.isSelected()` helps verify the current state

Example from transcript: Checkboxes for days such as Sunday, Monday, Tuesday, etc.

---

## **1.1 Selecting a Single Checkbox**

To select a particular checkbox such as *Sunday*, locate it using a reliable XPath (often using `id`, `type`, or class attributes).

```java
driver.findElement(By.xpath("//input[@id='sunday']")).click();
```

The transcript emphasizes using **unique attributes** like `id` to avoid selecting multiple elements.

---

## **1.2 Selecting All Checkboxes**

When many checkboxes share a common attribute (e.g., same `class`), we can:

1. Locate them using `findElements()`
2. Loop through the result and click each checkbox

### Step 1 — Identify a common XPath

From transcript:
Use a combination of attributes to avoid selecting unwanted elements (like radio buttons).

```xpath
//input[@class='form-check-input' and @type='checkbox']
```

This XPath selects **only the checkboxes**, not radio buttons.

### Step 2 — Select all checkboxes

```java
List<WebElement> checkboxes = driver.findElements(
        By.xpath("//input[@class='form-check-input' and @type='checkbox']"));

for (WebElement checkbox : checkboxes) {
    checkbox.click();
}
```

---

## **1.3 Selecting the **Last N** Checkboxes**

Transcript method:
Use formula:

```
startingIndex = totalCheckboxes − numberToSelect
```

Example: Select last **3** checkboxes.

```java
List<WebElement> checkboxes = driver.findElements(
        By.xpath("//input[@class='form-check-input' and @type='checkbox']"));

int total = checkboxes.size();
int selectLast = 3;
int startIndex = total - selectLast;

for (int i = startIndex; i < total; i++) {
    checkboxes.get(i).click();
}
```

This technique is very useful when the number of checkboxes changes dynamically.

---

## **1.4 Selecting the First N Checkboxes**

For first **3**:

```java
for (int i = 0; i < 3; i++) {
    checkboxes.get(i).click();
}
```

No formula is needed because indexing starts at 0.

---

## **1.5 Conditionally Unselecting Only the Selected Checkboxes**

Using the `.isSelected()` method:

```java
for (int i = 0; i < checkboxes.size(); i++) {
    if (checkboxes.get(i).isSelected()) {
        checkboxes.get(i).click(); // unselect
    }
}
```

Transcript emphasized:
✔ Do **not** blindly unselect all checkboxes
✔ Always check state first to avoid unintended selections


---

# **2. Handling Radio Buttons**

A **radio button** allows selecting **only one option** at a time.
Handling is similar to checkboxes:

* `click()` selects the button
* `.isSelected()` verifies the state

BUT unlike checkboxes, selecting one radio button **automatically unselects others**.

## **Example:**

```java
driver.findElement(By.xpath("//input[@id='gender-male']")).click();
```

or selecting multiple radio buttons using loops:

```java
List<WebElement> radios = driver.findElements(
        By.xpath("//input[@type='radio']"));

for (WebElement radio : radios) {
    radio.click();   // each click switches selection
}
```

Note: Only one stays selected at any time.
This behavior is highlighted in the transcript.

---

# **3. Handling Alerts (JavaScript Pop-ups)**

Three important alert types:

1. **Simple Alert** – shows a message & OK button
2. **Confirmation Alert** – OK & Cancel buttons
3. **Prompt Alert** – accepts text input

In Selenium, we handle alerts using:

```java
Alert alert = driver.switchTo().alert();
```

---

## **3.1 Simple Alert**

```java
Alert alert = driver.switchTo().alert();
alert.accept(); // clicks OK
```

---

## **3.2 Confirmation Alert (OK / Cancel)**

```java
Alert alert = driver.switchTo().alert();
alert.dismiss();  // clicks Cancel
```

---

## **3.3 Prompt Alert (Input field)**

```java
Alert alert = driver.switchTo().alert();
alert.sendKeys("Hello!");
alert.accept();
```

The transcript emphasizes understanding alert types and using `accept()`, `dismiss()`, and `sendKeys()` appropriately.


---

# **Summary**

| Element          | Methods Used                                                | Notes                     |
| ---------------- | ----------------------------------------------------------- | ------------------------- |
| **Checkbox**     | `click()`, `isSelected()`                                   | Multi-select allowed      |
| **Radio Button** | `click()`, `isSelected()`                                   | Single-select only        |
| **Alerts**       | `switchTo().alert()`, `accept()`, `dismiss()`, `sendKeys()` | Handle blocking JS popups |

You now know how to:

✔ Select/unselect specific or multiple checkboxes
✔ Write dynamic logic for selecting first/last N checkboxes
✔ Verify checkbox states before unselecting
✔ Handle radio button operations
✔ Work with all types of alerts

These are foundational operations you’ll use in almost every Selenium automation project.

---

