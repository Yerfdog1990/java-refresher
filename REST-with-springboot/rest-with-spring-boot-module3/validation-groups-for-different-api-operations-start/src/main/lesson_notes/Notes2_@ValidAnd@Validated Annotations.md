# Differences in @Valid and @Validated Annotations in Spring

---

## 1. Overview

Validating users’ input is a common functionality in most applications. In the Java ecosystem, we use the **Java Standard Bean Validation API (JSR-303/JSR-380)** to implement validation rules. This API is well integrated with Spring from version 4.0 onward.

Two commonly used annotations in Spring validation are:

* `@Valid`
* `@Validated`

Both stem from the Standard Bean Validation API, but they are not identical. They serve related yet distinct purposes.

This guide provides detailed notes explaining:

* What each annotation does
* Where each should be used
* How they differ
* When to use one over the other

---

## 2. Understanding @Valid and @Validated

### 2.1 The `@Valid` Annotation

`@Valid` comes from **JSR-303 (Bean Validation API)**.

In Spring, it is used for:

* Method-level validation (e.g., validating request bodies or model attributes)
* Marking a member attribute for nested validation

However, `@Valid` does **not support validation groups**.

#### What Does That Mean?

When `@Valid` is used:

* All constraints on the object are validated.
* It validates the entire object graph (including nested objects if marked with `@Valid`).
* It does not allow selecting specific constraint groups.

This becomes a limitation in scenarios where partial validation is required.

---

### 2.2 Why Group Validation Is Needed

Validation groups allow us to:

* Apply only certain constraints
* Control which fields are validated
* Perform step-wise validation

#### Example Use Case: UI Wizard

Imagine a multi-step registration form.

**Step 1**

* name
* password

**Step 2**

* age
* phone

In step 1, we only want to validate name and password.
In step 2, we validate age and phone.

But if we use `@Valid`, it validates all constraints — including those for step 2 — even if the UI hasn’t collected those fields yet.

This is where `@Validated` becomes essential.

---

### 2.3 The `@Validated` Annotation

`@Validated` is a Spring-specific variant of `@Valid`.

It supports:

* Validation groups
* Group sequences
* Method-level validation with group selection

It is primarily used at the **method level**.

For nested properties, we still use `@Valid`.

---

## 3. Practical Example – Single-Step Registration

### 3.1 Initial Domain Model

```java
public class UserAccount {

    @NotNull
    @Size(min = 4, max = 15)
    private String password;

    @NotBlank
    private String name;

    // standard constructors / setters / getters / toString
}
```

### 3.2 Controller Using `@Valid`

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

### 3.3 Test Case

```java
@Test
public void givenSaveBasicInfo_whenCorrectInput_thenSuccess() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.post("/saveBasicInfo")
      .accept(MediaType.TEXT_HTML)
      .param("name", "test123")
      .param("password", "pass"))
      .andExpect(view().name("success"))
      .andExpect(status().isOk())
      .andDo(print());
}
```

This works correctly for a simple form.

---

## 4. Extending to a Multi-Step Form

Now suppose we extend the form.

### 4.1 Updated Domain Model

```java
public class UserAccount {

    @NotNull
    @Size(min = 4, max = 15)
    private String password;

    @NotBlank
    private String name;

    @Min(value = 18, message = "Age should not be less than 18")
    private int age;

    @NotBlank
    private String phone;

    // standard constructors / setters / getters / toString
}
```

Now the previous test fails.

Why?

Because `@Valid` tries to validate:

* password
* name
* age
* phone

But age and phone are not yet provided in step 1.

This is where group validation is required.

---

## 5. Using Groups with @Validated

### 5.1 Create Marker Interfaces

```java
public interface BasicInfo {}
public interface AdvanceInfo {}
```

These are empty interfaces used to group constraints.

---

### 5.2 Update Domain Model with Groups

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

    // standard constructors / setters / getters / toString
}
```

Now constraints are separated by group.

---

### 5.3 Update Controller to Use `@Validated`

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

Now only `BasicInfo` constraints are validated.

---

### 5.4 Test Case

```java
@Test
public void givenSaveBasicInfoStep1_whenCorrectInput_thenSuccess() throws Exception {
    this.mockMvc.perform(MockMvcRequestBuilders.post("/saveBasicInfoStep1")
      .accept(MediaType.TEXT_HTML)
      .param("name", "test123")
      .param("password", "pass"))
      .andExpect(view().name("success"))
      .andExpect(status().isOk())
      .andDo(print());
}
```

This now runs successfully.

---

## 6. Using @Valid for Nested Objects

`@Valid` is essential when validating nested objects.

### 6.1 Nested Object

```java
public class UserAddress {

    @NotBlank
    private String countryCode;

    // standard constructors / setters / getters / toString
}
```

### 6.2 Mark Nested Property with @Valid

```java
public class UserAccount {

    @Valid
    @NotNull(groups = AdvanceInfo.class)
    private UserAddress useraddress;

    // other fields
}
```

Why is `@Valid` necessary here?

Because without it:

* The `UserAddress` object would not be validated.
* Only the parent object would be checked.

`@Validated` does not replace this behavior.
`@Valid` is still required for nested validation.

---

## 7. Pros and Cons

### 7.1 @Valid

**Pros**

* Simple and standard (JSR-303)
* Validates entire object graph
* Automatically validates nested objects (if annotated)

**Cons**

* No support for group validation
* Cannot perform partial validation
* Not suitable for multi-step forms

---

### 7.2 @Validated

**Pros**

* Supports group validation
* Supports partial validation
* Supports group sequences
* Useful for multi-step workflows

**Cons**

* Domain objects must know all validation groups
* Mixes validation rules with business logic use cases
* Can lead to tighter coupling (possible anti-pattern)

---

## 8. Key Differences Summary

| Feature                     | @Valid           | @Validated                      |
| --------------------------- | ---------------- |---------------------------------|
| Origin                      | JSR-303          | Spring                          |
| Supports group validation   | ❌ No            | ✅ Yes                          |
| Supports group sequences    | ❌ No            | ✅ Yes                          |
| Used for nested validation  | ✅ Yes           | ❌ No (must still use @Valid)   |
| Validates full object graph | ✅ Yes           | Yes (depending on group)        |
| Best for                    | Basic validation | Group-based / partial validation|

---

## 9. Conclusion

The key differences between `@Valid` and `@Validated` can be summarized as follows:

* Use **`@Valid`** for basic validation.
* Use **`@Validated`** when working with validation groups or group sequences.
* Use **`@Valid`** to trigger validation of nested properties.
* Use **`@Validated`** at the method level when selective validation is required.

In short:

* For simple validation → use `@Valid`
* For group-based or partial validation → use `@Validated`
* For nested object validation → always use `@Valid`

Understanding when to use each annotation ensures clean, maintainable, and flexible validation logic in Spring applications.

---