
---

# Java Bean Validation Basics

## 1. Overview

In modern Java applications, **validating user input** is a fundamental requirement. Whether data comes from a web form, an API request, or another system, we must ensure it meets specific rules before processing or storing it.

**Java Bean Validation** provides a **standard, declarative, and reusable way** to validate Java objects (beans). Instead of writing repetitive `if` statements, developers define validation rules using annotations placed directly on bean properties.

This tutorial focuses on **JSR 380**, the specification behind **Jakarta Bean Validation 3.0**, which builds on earlier versions introduced in Java EE 7 and is now widely used in both **Jakarta EE** and **Spring-based applications**.

---

## 2. What Is JSR 380?

**JSR 380** (Java Specification Request 380) defines the **Java API for Bean Validation**. It standardizes how validation constraints are declared and how validation is executed.

### Key Characteristics of JSR 380

* Part of **Jakarta EE** and **Java SE**
* Uses **annotations** to declare constraints
* Works across frameworks (Spring, Jakarta EE, Micronaut, etc.)
* Supports **Java 8+ features** such as:

    * Optional
    * Streams
    * New date/time API
    * Modules (Java 9+)

JSR 380 does **not** enforce validation automatically—it defines *how* validation should work. An implementation (such as **Hibernate Validator**) performs the actual validation at runtime.

---

## 3. Dependencies

### Using Spring Boot

If you are using Spring Boot, adding:

```xml
spring-boot-starter-validation
```

automatically includes **Hibernate Validator** as a transitive dependency.

### Adding Validation Manually

If you want validation without Spring Boot, add the following dependency:

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>9.1.0.Final</version>
</dependency>
```

### Important Note

Hibernate Validator:

* Implements **JSR 380**
* Is **independent of Hibernate ORM**
* Does **not** add database persistence features

---

## 4. Using Validation Annotations

Validation constraints are applied directly to **bean properties** using annotations from:

```
jakarta.validation.constraints
```

### Example: User Bean with Validation

```java
public class User {

    @NotNull(message = "Name cannot be null")
    private String name;

    @AssertTrue(message = "Working must be true")
    private boolean working;

    @Size(min = 10, max = 200,
          message = "About Me must be between 10 and 200 characters")
    private String aboutMe;

    @Min(value = 18, message = "Age should not be less than 18")
    @Max(value = 150, message = "Age should not be greater than 150")
    private int age;

    @Email(message = "Email should be valid")
    private String email;

    // standard getters and setters
}
```

---

## 4.1 Common Validation Annotations Explained

### Null & Text Constraints

* **@NotNull**
  Ensures the value is not `null`.

* **@NotEmpty**
  Ensures the value is not `null` *and* not empty (String, Collection, Map, Array).

* **@NotBlank**
  Ensures the value is not `null`, empty, or whitespace (Strings only).

---

### Boolean Constraint

* **@AssertTrue**
  Ensures the value is `true`.

---

### Size Constraint

* **@Size(min, max)**
  Validates length or size of:

    * String
    * Collection
    * Map
    * Array

---

### Numeric Constraints

* **@Min / @Max**
  Enforces numeric range limits.

* **@Positive / @PositiveOrZero**
  Requires positive numbers.

* **@Negative / @NegativeOrZero**
  Requires negative numbers.

---

### Date & Time Constraints

* **@Past / @PastOrPresent**
  Validates past dates.

* **@Future / @FutureOrPresent**
  Validates future dates.

Works with:

* `LocalDate`
* `LocalDateTime`
* `Instant`
* Other Java 8 date types

---

### Email Constraint

* **@Email**
  Ensures the value follows a valid email format.

---

## 4.2 Validation on Collections

Validation can be applied to **elements inside a collection**:

```java
List<@NotBlank String> preferences;
```

Each element added to the list is validated individually.

---

## 4.3 Validation with Optional

JSR 380 supports Java’s `Optional` type:

```java
private LocalDate dateOfBirth;

public Optional<@Past LocalDate> getDateOfBirth() {
    return Optional.of(dateOfBirth);
}
```

The validator:

* Automatically unwraps the `Optional`
* Applies constraints to the contained value

---

## 5. Programmatic Validation

While frameworks like Spring often trigger validation automatically, it’s important to understand **manual (programmatic) validation**.

---

### 5.1 Creating a Validator

```java
ValidatorFactory factory =
        Validation.buildDefaultValidatorFactory();
Validator validator = factory.getValidator();
```

* `ValidatorFactory` creates validators
* `Validator` performs validation

---

### 5.2 Defining an Invalid Bean

```java
User user = new User();
user.setWorking(true);
user.setAboutMe("Its all about me!");
user.setAge(50);
```

Here, `name` is `null`, which violates `@NotNull`.

---

### 5.3 Validating the Bean

```java
Set<ConstraintViolation<User>> violations =
        validator.validate(user);
```

* Returns a set of all validation errors
* Empty set means the object is valid

---

### 5.4 Reading Validation Errors

```java
for (ConstraintViolation<User> violation : violations) {
    log.error(violation.getMessage());
}
```

Each `ConstraintViolation` contains:

* Error message
* Property path
* Invalid value

---

## 6. Testing Validation Annotations

Bean validation is easy to test using **unit tests**.

---

### 6.1 Test Setup

```java
private Validator validator;

@BeforeEach
void setUp() {
    validator =
        Validation.buildDefaultValidatorFactory().getValidator();
}
```

---

### 6.2 Testing a Valid Bean

```java
User user = new User();
user.setName("test-name");
user.setWorking(true);
user.setAboutMe("test-about-me");
user.setAge(24);
user.setEmail("test@baeldung.ut");

Set<ConstraintViolation<User>> violations =
        validator.validate(user);

assertTrue(violations.isEmpty());
```

---

### 6.3 Testing an Invalid Bean

```java
User user = new User();
user.setName(null);
user.setWorking(false);
user.setAboutMe("test");
user.setAge(10);
user.setEmail("test-invalid-email");

Set<ConstraintViolation<User>> violations =
        validator.validate(user);

assertFalse(violations.isEmpty());
assertThat(violations).hasSize(5);
assertThat(violations)
  .extracting(ConstraintViolation::getMessage)
  .containsExactlyInAnyOrder(
    "Name cannot be null",
    "Working must be true",
    "About Me must be between 10 and 200 characters",
    "Age should not be less than 18",
    "Email should be valid"
  );
```

This confirms:

* All constraints are enforced
* Error messages are returned correctly

---

## 7. Conclusion

Java Bean Validation (JSR 380) provides a **clean, standardized, and powerful way** to validate Java objects.

### Key Takeaways

* Validation rules are declared using annotations
* Constraints are reusable and framework-agnostic
* Programmatic validation gives fine-grained control
* Testing validation is simple and reliable
* Widely used in REST APIs and enterprise applications

Bean Validation improves:

* Code readability
* Maintainability
* Data integrity
* Application security

---
