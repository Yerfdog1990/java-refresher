
---

# Differences Between `@Valid` and `@Validated` Annotations in Spring

## 1. Overview

Input validation is a **core responsibility of backend systems**. No matter how good the frontend is, a Spring application must **never trust client input**. Validation ensures:

* Data integrity
* Application stability
* Clear and meaningful error responses
* Protection against invalid or malicious input

In the Java ecosystem, validation is standardized through **Bean Validation** (JSR-303 / JSR-380), which is fully integrated into Spring Framework (from Spring 4.0 onward).

Two annotations are commonly used in Spring for validation:

* `@Valid` (standard Java)
* `@Validated` (Spring-specific)

Although they look similar, **they serve different purposes**. The most important difference lies in **validation scope** and **support for validation groups**.

---

## 2. Bean Validation in Spring (Quick Recap)

Spring uses the Bean Validation API together with providers like **Hibernate Validator** to validate objects automatically:

* At the **controller layer** (HTTP requests)
* At the **service layer** (method parameters)
* On **nested objects**

Validation is typically triggered when:

* A request body is deserialized into a DTO
* A method parameter is annotated with `@Valid` or `@Validated`

---

## 3. The `@Valid` Annotation

### 3.1 What is `@Valid`?

* Comes from **JSR-303 / JSR-380**
* Part of standard Java (`jakarta.validation.Valid`)
* Supported by Spring out of the box

### 3.2 What does `@Valid` do?

When applied:

* It **triggers validation of all constraints** on the target object
* It validates the **entire object graph**, including nested objects
* It does **not support validation groups**

In other words:

> **`@Valid` applies every constraint indiscriminately.**

---

### 3.3 Example: Simple Validation with `@Valid`

#### Domain Object

```java
public class UserAccount {

    @NotNull
    @Size(min = 4, max = 15)
    private String password;

    @NotBlank
    private String name;

    // getters and setters
}
```

#### Controller

```java
@RequestMapping(value = "/saveBasicInfo", method = RequestMethod.POST)
public String saveBasicInfo(
  @Valid @ModelAttribute("useraccount") UserAccount useraccount,
  BindingResult result,
  ModelMap model) {

    if (result.hasErrors()) {
        return "error";
    }
    return "success";
}
```

✔ All constraints are evaluated
✔ Simple and clean
✔ Perfect for **single-step forms**

---

### 3.4 Limitation of `@Valid`

The problem arises when:

* The same DTO is reused in **different contexts**
* Only **some fields** should be validated in certain scenarios

For example:

* Create vs Update
* Step 1 vs Step 2 of a wizard
* Partial updates (`PATCH`)

❌ `@Valid` cannot selectively apply constraints
❌ All constraints always run

---

## 4. Validation Groups: Why They Matter

### 4.1 What Are Validation Groups?

Validation groups allow you to:

* Define **subsets of constraints**
* Apply only the relevant constraints per use case

Typical scenarios:

* Multi-step UI wizards
* Create vs Update operations
* Draft vs Published states

---

### 4.2 Marker Interfaces

Groups are defined using **empty marker interfaces**:

```java
public interface BasicInfo {}
public interface AdvanceInfo {}
```

These interfaces **carry no logic**—they only act as identifiers.

---

## 5. The `@Validated` Annotation

### 5.1 What is `@Validated`?

* Spring-specific annotation
* Variant of `@Valid`
* Supports **group-based validation**
* Used mainly at **method level**

> Think of `@Validated` as **`@Valid + group control`**

---

### 5.2 Applying Groups to Constraints

```java
public class UserAccount {

    @NotNull(groups = BasicInfo.class)
    @Size(min = 4, max = 15, groups = BasicInfo.class)
    private String password;

    @NotBlank(groups = BasicInfo.class)
    private String name;

    @Min(value = 18, message = "Age should not be less than 18",
         groups = AdvanceInfo.class)
    private int age;

    @NotBlank(groups = AdvanceInfo.class)
    private String phone;

    // getters and setters
}
```

Now:

* Step 1 validates only **name & password**
* Step 2 validates **age & phone**

---

### 5.3 Controller Using `@Validated`

```java
@RequestMapping(value = "/saveBasicInfoStep1", method = RequestMethod.POST)
public String saveBasicInfoStep1(
  @Validated(BasicInfo.class)
  @ModelAttribute("useraccount") UserAccount useraccount,
  BindingResult result,
  ModelMap model) {

    if (result.hasErrors()) {
        return "error";
    }
    return "success";
}
```

✔ Only `BasicInfo` constraints run

✔ Unrelated fields are ignored

✔ Test passes successfully

---

## 6. Create vs Update: A Common Real-World Use Case

### Example: Product DTO

```java
public class ProductDto {

    @Null(groups = OnCreate.class)
    @NotNull(groups = OnUpdate.class)
    private Long id;

    @NotBlank
    private String name;

    @Positive
    private BigDecimal price;
}
```

### Controller

```java
@PostMapping
public ResponseEntity<?> create(
    @Validated(OnCreate.class) @RequestBody ProductDto dto) {
    ...
}

@PutMapping("/{id}")
public ResponseEntity<?> update(
    @Validated(OnUpdate.class) @RequestBody ProductDto dto) {
    ...
}
```

📌 Same DTO

📌 Different rules

📌 Clean and expressive

---

## 7. Nested Object Validation and `@Valid`

### 7.1 Why `@Valid` Is Still Necessary

`@Validated` **does not replace** `@Valid`.

`@Valid` is essential for:

* Triggering validation on **nested objects**

---

### 7.2 Example: Nested Validation

```java
public class UserAddress {

    @NotBlank
    private String countryCode;
}
```

```java
public class UserAccount {

    @Valid
    @NotNull(groups = AdvanceInfo.class)
    private UserAddress useraddress;
}
```

✔ `@Valid` ensures nested validation

✔ Without it, nested constraints are ignored

---

## 8. Key Differences Summary

| Feature           | `@Valid`          | `@Validated`     |
| ----------------- | ----------------- |------------------|
| Standard          | JSR-303 / JSR-380 | Spring-specific  |
| Supports groups   | ❌ No             |✅ Yes            | 
| Validation scope  | All constraints   | Group-specific   |
| Nested validation | ✅ Yes            |❌ Needs `@Valid` | 
| Best for          | Simple validation | Complex scenarios| 

---

## 9. Pros and Cons

### `@Valid`

**Pros**

* Simple and standard
* Validates entire object graph
* Easy to use

**Cons**

* No partial validation
* Not suitable for multi-use DTOs

---

### `@Validated`

**Pros**

* Supports validation groups
* Ideal for create/update separation
* Essential for complex workflows

**Cons**

* DTOs must know all use cases
* Can mix concerns if overused

---

## 10. Recommendation (Best Practice)

* ✅ Use **`@Valid`** for:

    * Simple DTOs
    * Single-purpose requests
    * Nested object validation

* ✅ Use **`@Validated(group)`** for:

    * Create vs Update
    * Multi-step forms
    * Partial validation scenarios

---

## 11. Conclusion

To conclude:

* **`@Valid`** is the default, standard, and simplest choice
* **`@Validated`** is essential when validation rules vary by context
* They are **not competitors**—they complement each other

> **Use `@Valid` for universality.
> Use `@Validated` for precision.**

---

## 1️⃣ High-Level Concept Diagram (Big Picture)

```
                 ┌─────────────────────────┐
                 │   HTTP Request (JSON)   │
                 └───────────┬─────────────┘
                             │
                             ▼
                   ┌───────────────────┐
                   │ Controller Method │
                   └───────────┬───────┘
                               │
          ┌────────────────────┴────────────────────┐
          │                                         │
          ▼                                         ▼
 ┌───────────────────┐                    ┌────────────────────┐
 │       @Valid      │                    │     @Validated     │
 │ (Standard Java)   │                    │ (Spring-specific)  │
 └─────────┬─────────┘                    └──────────┬─────────┘
           │                                         │
           ▼                                         ▼
 ┌───────────────────┐                    ┌────────────────────┐
 │ All constraints   │                    │ Group-specific     │
 │ are validated     │                    │ constraints only   │
 └─────────┬─────────┘                    └──────────┬─────────┘
           │                                         │
           ▼                                         ▼
 ┌───────────────────┐                    ┌────────────────────┐
 │ Nested objects    │                    │ Create / Update /  │
 │ validated (@Valid)│                    │ Wizard steps       │
 └───────────────────┘                    └────────────────────┘
```

**Teaching takeaway:**

👉 `@Valid` = **validate everything**

👉 `@Validated` = **validate what I choose**

---

## 2️⃣ Side-by-Side Comparison Diagram (Most Effective for Exams)

```
┌──────────────────────────────┐   ┌────────────────────────────────┐
│            @Valid            │   │           @Validated           │
├──────────────────────────────┤   ├────────────────────────────────┤
│ Standard Java (JSR-303/380)  │   │ Spring Framework annotation    │
│                              │   │                                │
│ Validates ALL constraints    │   │ Validates ONLY selected groups │
│                              │   │                                │
│ No group support             │   │ Supports validation groups     │
│                              │   │                                │
│ Simple & universal           │   │ Context-aware                  │
│                              │   │                                │
│ Best for:                    │   │ Best for:                      │
│ - Simple forms               │   │ - Create vs Update             │
│ - Single use DTOs            │   │ - Multi-step forms             │
│ - Nested object validation   │   │ - Partial validation           │
│                              │   │                                │
│ Example:                     │   │ Example:                       │
│ @Valid ProductDto dto        │   │ @Validated(OnCreate.class)     │
│                              │   │ ProductDto dto                 │
└──────────────────────────────┘   └────────────────────────────────┘
```

---

## 3️⃣ Validation Flow Diagram (Create vs Update)

```
                 ProductDto
        ┌─────────────────────────┐
        │ id                      │
        │ name                    │
        │ price                   │
        └───────────┬─────────────┘
                    │
     ┌──────────────┴──────────────┐
     │                             │
     ▼                             ▼
CREATE REQUEST                UPDATE REQUEST
POST /products               PUT /products/{id}

@Validated(OnCreate.class)    @Validated(OnUpdate.class)
        │                             │
        ▼                             ▼
┌────────────────────┐       ┌────────────────────┐
│ id MUST be null    │       │ id MUST NOT be null│
│ name required      │       │ name required      │
│ price > 0          │       │ price > 0          │
└────────────────────┘       └────────────────────┘
```

**Teaching takeaway:**
👉 Same DTO
👉 Different rules
👉 Controlled by `@Validated`

---

## 4️⃣ One-Line Memory Hook for Students 🧠

> **`@Valid` = “Validate everything”**
> **`@Validated` = “Validate by context”**

---

## 5️⃣ Common Exam / Interview Tip (Tell Students!)

> ❗ If you need **different validation rules for the same DTO**,
> **`@Valid` is NOT enough — you must use `@Validated` with groups.**

---

