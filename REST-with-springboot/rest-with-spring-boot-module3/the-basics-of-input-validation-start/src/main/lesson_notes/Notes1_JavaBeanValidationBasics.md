# Java Bean Validation Basics

---

## 1. Overview

Validating user input is one of the most common requirements in modern applications. Whether we are building REST APIs, web applications, or backend services, we must ensure that incoming data satisfies certain rules before processing it.

**Java Bean Validation** provides a standardized way to declare and enforce validation rules directly on Java objects (beans) using annotations.

The current standard is defined by:

* **JSR 380** – The Java Specification Request for Bean Validation
* **Jakarta Bean Validation 3.0** – The latest specification
* Built upon the Bean Validation API introduced in Java EE 7

Bean Validation has become the **de facto standard** for handling validation logic in Java applications.

---

## 2. What is JSR 380?

**JSR 380** defines the Java API for Bean Validation. It is part of:

* Jakarta EE
* Java SE

It ensures that the properties of a bean satisfy specific constraints using annotations such as:

* `@NotNull`
* `@Min`
* `@Max`

### Key Characteristics

* Annotation-based validation
* Works with Java SE and Jakarta EE
* Supports modern Java features (Java 8+), including:

    * `Optional`
    * Java 8 date/time API
    * Streams
    * Modules
    * Private interface methods

This makes it fully compatible with modern Java development practices.

---

## 3. Dependencies

In Spring Boot projects, validation support is usually included automatically through:

```
spring-boot-starter-validation
```

This starter includes a **transitive dependency** on:

```
hibernate-validator
```

If we want to use validation independently of Spring Boot, we can add:

```xml
<dependency>
    <groupId>org.hibernate.validator</groupId>
    <artifactId>hibernate-validator</artifactId>
    <version>9.1.0.Final</version>
</dependency>
```

### Important Note

`hibernate-validator`:

* Is the **reference implementation** of JSR 380
* Is **completely separate** from Hibernate ORM
* Does NOT introduce any persistence functionality

Adding it does **not** mean we are adding Hibernate persistence.

---

## 4. Using Validation Annotations

Let’s define a `User` bean and apply validation constraints:

```java
public class User {

    @NotNull(message = "Name cannot be null")
    private String name;

    @AssertTrue(message = "Working must be true")
    private boolean working;

    @Size(min = 10, max = 200, message 
      = "About Me must be between 10 and 200 characters")
    private String aboutMe;

    @Min(value = 18, message = "Age should not be less than 18")
    @Max(value = 150, message = "Age should not be greater than 150")
    private int age;

    @Email(message = "Email should be valid")
    private String email;

    // getters and setters
}
```

All annotations above are part of:

```
jakarta.validation.constraints
```

---

### 4.1 Core Validation Annotations

#### `@NotNull`

* Ensures value is not `null`

#### `@AssertTrue`

* Ensures boolean value is `true`

#### `@Size(min, max)`

* Validates size range
* Applies to:

    * String
    * Collection
    * Map
    * Array

#### `@Min(value)`

* Value must be ≥ specified number

#### `@Max(value)`

* Value must be ≤ specified number

#### `@Email`

* Validates email format

---

### 4.2 Common Additional Annotations

#### `@NotEmpty`

* Not null AND not empty
* Applies to String, Collection, Map, Array

#### `@NotBlank`

* Not null AND not whitespace
* Applies only to text

#### Numeric Constraints

* `@Positive`
* `@PositiveOrZero`
* `@Negative`
* `@NegativeOrZero`

#### Date Constraints

* `@Past`
* `@PastOrPresent`
* `@Future`
* `@FutureOrPresent`

These work with:

* `java.util.Date`
* Java 8+ date/time classes (e.g., `LocalDate`)

---

### 4.3 The `message` Attribute

All constraint annotations support the `message` attribute:

```java
@NotNull(message = "Name cannot be null")
```

This message is returned when validation fails.

---

## 5. Validating Collection Elements

Validation can also be applied to elements inside collections:

```java
List<@NotBlank String> preferences;
```

Every element added to the list will be validated.

This is called **container element validation**.

---

## 6. Optional Support (Java 8+)

Bean Validation supports `Optional`.

Example:

```java
private LocalDate dateOfBirth;

public Optional<@Past LocalDate> getDateOfBirth() {
    return Optional.of(dateOfBirth);
}
```

The framework automatically:

* Unwraps the `Optional`
* Applies `@Past` to the contained value

This works seamlessly with modern Java types.

---

## 7. Programmatic Validation

Although frameworks like Spring trigger validation automatically, we can also validate beans manually.

### 7.1 Create a Validator

```java
ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
Validator validator = factory.getValidator();
```

Steps:

1. Build a `ValidatorFactory`
2. Obtain a `Validator` instance

---

### 7.2 Define an Invalid Bean

```java
User user = new User();
user.setWorking(true);
user.setAboutMe("Its all about me!");
user.setAge(50);
```

Notice:

* `name` is not set (null)
* This should trigger validation failure

---

### 7.3 Validate the Bean

```java
Set<ConstraintViolation<User>> violations = validator.validate(user);
```

The `validate()` method returns:

```
Set<ConstraintViolation<T>>
```

If invalid, it contains one or more violations.

---

### 7.4 Inspect Violations

```java
for (ConstraintViolation<User> violation : violations) {
    log.error(violation.getMessage());
}
```

If `name` is null:

```
"Name cannot be null"
```

will be returned as a violation message.

---

## 8. Testing Validation Annotations

Bean validation can be easily tested using unit tests.

---

### 8.1 Setup Validator in Test

```java
private Validator validator;

@BeforeEach
void setUp() {
    validator = Validation.buildDefaultValidatorFactory().getValidator();
}
```

---

### 8.2 Test Valid Object

```java
User user = new User();
user.setName("test-name");
user.setWorking(true);
user.setAboutMe("test-about-me");
user.setAge(24);
user.setEmail("test@baeldung.ut");

Set<ConstraintViolation<User>> violations = validator.validate(user);

assertTrue(violations.isEmpty());
```

If valid:

* `violations` is empty

---

### 8.3 Test Invalid Object

```java
User user = new User();
user.setName(null);
user.setWorking(false);
user.setAboutMe("test");
user.setAge(10);
user.setEmail("test-invalid-email");

Set<ConstraintViolation<User>> violations = validator.validate(user);

assertFalse(violations.isEmpty());
assertThat(violations).hasSize(5);
assertThat(violations).extracting(ConstraintViolation::getMessage)
  .containsExactlyInAnyOrder(
    "Name cannot be null",
    "Working must be true", 
    "About Me must be between 10 and 200 characters",
    "Age should not be less than 18", 
    "Email should be valid");
```

This verifies:

* All constraints are applied
* Each violation returns the correct message
* The validation configuration works correctly

---

## 9. Key Concepts Summary

| Concept             | Explanation                           |
| ------------------- | ------------------------------------- |
| Bean Validation     | Standard way to validate Java objects |
| JSR 380             | Specification defining validation API |
| Annotations         | Declarative constraints on fields     |
| Validator           | Performs programmatic validation      |
| ConstraintViolation | Represents a validation failure       |
| message             | Custom error message for constraint   |

---

## 10. Conclusion

Java Bean Validation provides:

* A standardized validation API
* Annotation-based constraint definitions
* Integration with modern Java features
* Easy testing support
* Framework independence

By using Jakarta Validation annotations and the programmatic API, we can:

* Keep validation logic declarative
* Keep domain models clean
* Ensure reliable data integrity
* Write easily testable validation rules

Bean Validation is a foundational tool in modern Java and Spring-based REST APIs, especially when validating incoming DTOs in RESTful services.

---