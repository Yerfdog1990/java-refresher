# Grouping Jakarta (Javax) Validation Constraints

---

# 1. Introduction

In the Java Bean Validation Basics tutorial, we saw how to use various built-in `jakarta.validation` constraints such as:

* `@NotBlank`
* `@Email`
* `@NotNull`
* etc.

In this tutorial, we focus on **grouping jakarta.validation constraints**.

Constraint grouping allows us to:

* Apply one set of validations in one scenario
* Apply another set of validations in a different scenario
* Control validation execution more precisely

This is especially useful in real-world workflows.

---

# 2. Use Case

There are many scenarios where:

* We need to apply constraints on one set of fields
* Later, apply constraints on another set of fields
* All within the same bean

### Example: Two-Step Signup Form

**Step 1 — Basic Information**

* First name
* Last name
* Email
* Phone number
* Captcha

At this stage, we validate only these fields.

**Step 2 — Advanced Information**

* Address
* Street
* City
* Zip code
* Country
* Captcha (again)

Now we validate another set of fields.

Constraint groups allow us to validate specific parts of the object depending on context.

---

# 3. Grouping Validation Constraints

All `jakarta.validation` constraints have an attribute named:

```java
groups
```

When adding a constraint, we can specify which group it belongs to.

This is done by assigning a **group interface class** to the `groups` attribute.

---

## 3.1. Declaring Constraint Groups

Constraint groups are just **empty interfaces**.

In our use case, we define two groups.

### BasicInfo Group

```java
public interface BasicInfo {
}
```

### AdvanceInfo Group

```java
public interface AdvanceInfo {
}
```

These interfaces act as identifiers for grouping constraints.

---

## 3.2. Using Constraint Groups

Now we use the groups in our bean:

```java
public class RegistrationForm {
    @NotBlank(groups = BasicInfo.class)
    private String firstName;
    @NotBlank(groups = BasicInfo.class)
    private String lastName;
    @Email(groups = BasicInfo.class)
    private String email;
    @NotBlank(groups = BasicInfo.class)
    private String phone;

    @NotBlank(groups = {BasicInfo.class, AdvanceInfo.class})
    private String captcha;

    @NotBlank(groups = AdvanceInfo.class)
    private String street;
    
    @NotBlank(groups = AdvanceInfo.class)
    private String houseNumber;
    
    @NotBlank(groups = AdvanceInfo.class)
    private String zipCode;
    
    @NotBlank(groups = AdvanceInfo.class)
    private String city;
    
    @NotBlank(groups = AdvanceInfo.class)
    private String contry;
}
```

### Important Notes

* Fields are divided into two logical groups.
* `captcha` belongs to both groups.
* By default, constraints belong to the `Default` group if no group is specified.

---

# 3.3. Testing Constraints Having One Group

Now let’s validate using a specific group.

---

## Test Setup

```java
public class RegistrationFormUnitTest {
    private static Validator validator;

    @BeforeClass
    public static void setupValidatorInstance() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }
```

---

## Test BasicInfo Validation

```java
@Test
public void whenBasicInfoIsNotComplete_thenShouldGiveConstraintViolationsOnlyForBasicInfo() {
    RegistrationForm form = buildRegistrationFormWithBasicInfo();
    form.setFirstName("");
 
    Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, BasicInfo.class);
 
    assertThat(violations.size()).isEqualTo(1);
    violations.forEach(action -> {
        assertThat(action.getMessage()).isEqualTo("must not be blank");
        assertThat(action.getPropertyPath().toString()).isEqualTo("firstName");
    });
}
```

Helper method:

```java
private RegistrationForm buildRegistrationFormWithBasicInfo() {
    RegistrationForm form = new RegistrationForm();
    form.setFirstName("devender");
    form.setLastName("kumar");
    form.setEmail("anyemail@yopmail.com");
    form.setPhone("12345");
    form.setCaptcha("Y2HAhU5T");
    return form;
}
```

### What Happens?

* Only `BasicInfo` constraints are evaluated.
* Only `firstName` fails.
* No `AdvanceInfo` constraints are checked.

---

## Test AdvanceInfo Validation

```java
@Test
public void whenAdvanceInfoIsNotComplete_thenShouldGiveConstraintViolationsOnlyForAdvanceInfo() {
    RegistrationForm form = buildRegistrationFormWithAdvanceInfo();
    form.setZipCode("");
 
    Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, AdvanceInfo.class);
 
    assertThat(violations.size()).isEqualTo(1);
    violations.forEach(action -> {
        assertThat(action.getMessage()).isEqualTo("must not be blank");
        assertThat(action.getPropertyPath().toString()).isEqualTo("zipCode");
    });
}
```

Helper methods:

```java
private RegistrationForm buildRegistrationFormWithAdvanceInfo() {
    RegistrationForm form = new RegistrationForm();
    return populateAdvanceInfo(form);
}

private RegistrationForm populateAdvanceInfo(RegistrationForm form) {
    form.setCity("Berlin");
    form.setContry("DE");
    form.setStreet("alexa str.");
    form.setZipCode("19923");
    form.setHouseNumber("2a");
    form.setCaptcha("Y2HAhU5T");
    return form;
}
```

### Result

Only `AdvanceInfo` constraints are evaluated.

---

# 3.4. Testing Constraints Having Multiple Groups

Some constraints can belong to multiple groups.

In our case:

```java
@NotBlank(groups = {BasicInfo.class, AdvanceInfo.class})
private String captcha;
```

---

## Captcha Validation with BasicInfo

```java
@Test
public void whenCaptchaIsBlank_thenShouldGiveConstraintViolationsForBasicInfo() {
    RegistrationForm form = buildRegistrationFormWithBasicInfo();
    form.setCaptcha("");
 
    Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, BasicInfo.class);
 
    assertThat(violations.size()).isEqualTo(1);
    violations.forEach(action -> {
        assertThat(action.getMessage()).isEqualTo("must not be blank");
        assertThat(action.getPropertyPath().toString()).isEqualTo("captcha");
    });
}
```

---

## Captcha Validation with AdvanceInfo

```java
@Test
public void whenCaptchaIsBlank_thenShouldGiveConstraintViolationsForAdvanceInfo() {
    RegistrationForm form = buildRegistrationFormWithAdvanceInfo();
    form.setCaptcha("");
 
    Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, AdvanceInfo.class);
 
    assertThat(violations.size()).isEqualTo(1);
    violations.forEach(action -> {
        assertThat(action.getMessage()).isEqualTo("must not be blank");
        assertThat(action.getPropertyPath().toString()).isEqualTo("captcha");
    });
}
```

### Key Insight

A constraint can belong to multiple groups and will be evaluated whenever any of those groups is validated.

---

# 4. Specifying Constraint Group Validation Order with GroupSequence

By default:

* Constraint groups are **not evaluated in any specific order**

But sometimes we want:

* Validate BasicInfo first
* Only if valid → validate AdvanceInfo

For this, we use:

```java
@GroupSequence
```

There are two ways to use it:

1. On the entity
2. On an interface

---

## 4.1. Using GroupSequence on the Entity

```java
@GroupSequence({BasicInfo.class, AdvanceInfo.class})
public class RegistrationForm {
    @NotBlank(groups = BasicInfo.class)
    private String firstName;
    @NotBlank(groups = AdvanceInfo.class)
    private String street;
}
```

Now:

* BasicInfo is validated first.
* If it fails → AdvanceInfo is NOT evaluated.

---

## 4.2. Using GroupSequence on an Interface

This is reusable and cleaner.

```java
@GroupSequence({BasicInfo.class, AdvanceInfo.class})
public interface CompleteInfo {
}
```

Now we validate using:

```java
validator.validate(form, CompleteInfo.class);
```

This sequence can be reused for multiple entities.

---

## 4.3. Testing GroupSequence

---

### Test 1: BasicInfo Fails → AdvanceInfo Not Evaluated

```java
@Test
public void whenBasicInfoIsNotComplete_thenShouldGiveConstraintViolationsForBasicInfoOnly() {
    RegistrationForm form = buildRegistrationFormWithBasicInfo();
    form.setFirstName("");
 
    Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, CompleteInfo.class);
 
    assertThat(violations.size()).isEqualTo(1);
    violations.forEach(action -> {
        assertThat(action.getMessage()).isEqualTo("must not be blank");
        assertThat(action.getPropertyPath().toString()).isEqualTo("firstName");
    });
}
```

Only BasicInfo is validated.

---

### Test 2: All Valid → No Violations

```java
@Test
public void whenBasicAndAdvanceInfoIsComplete_thenShouldNotGiveConstraintViolationsWithCompleteInfoValidationGroup() {
    RegistrationForm form = buildRegistrationFormWithBasicAndAdvanceInfo();
 
    Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(form, CompleteInfo.class);
 
    assertThat(violations.size()).isEqualTo(0);
}
```

Both groups pass successfully.

---

# 5. Conclusion

In this tutorial, we learned how to:

* Group `jakarta.validation` constraints
* Assign constraints to specific validation groups
* Validate specific groups programmatically
* Use multiple groups for a single constraint
* Control validation order using `@GroupSequence`

Constraint grouping is powerful for:

* Multi-step forms
* Context-specific validations
* Partial updates
* Workflow-based validation logic

It provides fine-grained control over when and how validation rules are applied.

As usual, all code snippets are available over on GitHub.

---