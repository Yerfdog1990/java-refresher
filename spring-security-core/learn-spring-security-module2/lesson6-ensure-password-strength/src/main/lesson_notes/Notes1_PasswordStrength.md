
---

# Lesson Notes: Password Strength during Registration

---

## 1. Introduction

Password strength during user registration is a **critical security concern** in modern applications.
Significant data breaches are more common than we might think, and in many cases, the **strength of user passwords determines the impact** of such breaches.

Simply put, **helping users select strong passwords has a huge positive effect on the overall security of the system**.

The goal of this lesson is to:

* Define **constraints** for how strong a password must be
* Provide **immediate feedback** to users during registration
* Enforce **the same rules consistently** on both the front end and the back end

---

## 2. Why “Strong” Passwords?

Significant data breaches occur frequently, often due to weak or reused passwords.

The **outcome of a breach** is directly influenced by:

* Password length
* Predictability
* Complexity
* Resistance to common patterns

By enforcing password strength:

* Attackers have a much harder time cracking credentials
* Users are guided toward secure behavior
* The system’s overall security posture improves

**Our objective** is to:

* Define password strength rules
* Enforce them during registration
* Give users instant feedback when those rules are violated

---

## 3. Password Strength on the Front End

We begin on the **client side**, where we can help users select strong passwords **before the form is submitted**.

### Why Front-End Validation?

* Provides **real-time feedback**
* Improves user experience
* Prevents unnecessary server requests
* Helps users immediately understand password requirements

⚠️ However, **front-end validation alone is not sufficient**.
All rules enforced on the client must also be enforced on the server.

---

### (Diagram Space – Front-End Password Validation Flow)

```
[ User Types Password ]
            ↓
[ jQuery Password Strength Plugin ]
            ↓
[ Real-Time Feedback Displayed ]
```

---

## 4. Using jQuery Password Strength Meter (Client Side)

We use a simple jQuery plugin called **jQuery Password Strength Meter for Twitter Bootstrap**.

### 4.1 Required JavaScript Files

```html
<script src="/js/jquery-1.7.2.js"></script>
<script src="/js/pwstrength.js"></script>
```

These files already exist in the project, but they must be made accessible via Spring’s static resource configuration.

---

### 4.2 Configuring Static Resource Resolution in Spring MVC

To allow direct access to static resources, configure a resource handler:

```java
@Override
public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/**")
            .addResourceLocations(new String[] { "classpath:/static/" });
}
```

This maps the **static folder** on the classpath so that JavaScript resources can be loaded by the browser.

---

### (Diagram Space – Static Resource Mapping)

```
classpath:/static/
        ↓
     /js/
        ↓
jquery.js
pwstrength.js
```

---

### 4.3 Enabling Password Strength Validation on the Registration Page

Now we attach the password strength meter to the password input field.

```html
<script type="text/javascript">
    $(document).ready(function () {
        options = {
            common: { minChar: 8 },
            ui: {
                showVerdictsInsideProgressBar: true,
                showErrors: true,
                errorMessages: {
                    wordLength: 'Your password is too short',
                }
            }
        };
        $('#password').pwstrength(options);
    });
</script>
```

#### What This Does:

* Enforces a **minimum length of 8 characters**
* Displays feedback **inside a progress bar**
* Shows real-time error messages
* Validates as the user types

---

### 4.4 Front-End Behavior in Action

* Short passwords immediately trigger the message:
  **“Your password is too short”**
* Sequential values like `123` are flagged
* When the password meets all rules:

    * Errors disappear
    * Strength indicator shows **Strong**

This completes the **front-end implementation**.

---

## 5. Why Front-End Validation Is Not Enough

Although front-end validation is extremely helpful:

* It can be bypassed
* Client-side rules can be disabled or manipulated
* The server must remain the **source of truth**

Therefore, the **exact same password rules must be enforced on the back end**.

---

## 6. Password Strength in the Back End

Just like the front end, we use a **library** to define password rules instead of writing them manually.

Manual validation is:

* Error-prone
* Hard to maintain
* Difficult to extend

---

### 6.1 Introducing Passay

**Passay** is a Java password policy enforcement library that provides:

* Password validation
* Password generation
* Command-line tools
* Extensible rule definitions
* Internationalization support

---

### 6.2 Adding Passay Dependency

```xml
<dependency>
    <groupId>org.passay</groupId>
    <artifactId>passay</artifactId>
    <version>1.2.0</version>
</dependency>
```

(Alternative newer version example:)

```xml
<dependency>
    <groupId>org.passay</groupId>
    <artifactId>passay</artifactId>
    <version>1.6.6</version>
</dependency>
```

---

## 7. Creating a Custom Password Validation Annotation

Instead of validating passwords manually everywhere, we create a **custom validation annotation**.

### 7.1 Defining `@ValidPassword`

```java
package com.baeldung.lss.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = PasswordConstraintValidator.class)
@Target({ TYPE, FIELD, ANNOTATION_TYPE })
@Retention(RUNTIME)
public @interface ValidPassword {

    String message() default "Invalid Password";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
```

This annotation delegates validation logic to a custom validator.

---

## 8. Implementing the Password Constraint Validator

### 8.1 Validator Skeleton

```java
public class PasswordConstraintValidator
        implements ConstraintValidator<ValidPassword, String> {
}
```

---

### 8.2 Implementing Password Strength Validation Logic

```java
PasswordValidator validator =
    new PasswordValidator(Arrays.asList(
        new LengthRule(8, 30)
    ));

RuleResult result =
    validator.validate(new PasswordData(password));

if (result.isValid()) {
    return true;
}

context.buildConstraintViolationWithTemplate(
    Joiner.on("\n").join(validator.getMessages(result)))
    .addConstraintViolation();

return false;
```

#### What This Does:

* Enforces password length between **8 and 30 characters**
* Validates using Passay rules
* Returns `true` if valid
* Adds detailed error messages to the validation context if invalid

---

### (Diagram Space – Back-End Validation Flow)

```
[ Password Submitted ]
          ↓
[ @ValidPassword Annotation ]
          ↓
[ PasswordConstraintValidator ]
          ↓
[ Passay Rule Validation ]
          ↓
[ Accept or Reject Password ]
```

---

## 9. Applying the Validation Annotation

Finally, annotate the password field in the User or DTO class:

```java
@ValidPassword
private String password;
```

Now:

* Invalid passwords are **rejected by the server**
* Validation messages are returned to the client
* The system does not rely on front-end checks

---

## 10. Summary and Key Takeaways

* Strong passwords significantly improve system security
* Users benefit from **real-time feedback** on the front end
* **Front-end validation alone is never enough**
* The back end must enforce **identical rules**
* Passay provides a clean, extensible solution for password policy enforcement
* Custom annotations integrate seamlessly with Java Bean Validation

With this full infrastructure in place, you can easily add:

* Character complexity rules
* Dictionary checks
* Sequence prevention
* Custom password policies

---

