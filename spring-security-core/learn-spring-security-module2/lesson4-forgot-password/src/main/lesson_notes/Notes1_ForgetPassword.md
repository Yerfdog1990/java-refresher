Below are **comprehensive lesson notes** on **“Forgot Password”**, written **strictly using all the information you provided**, carefully organized, explained step by step, and structured for teaching and revision.
Spaces are intentionally left where diagrams can be inserted.

---

# Lesson Notes: Forgot Password

---

## 1. Overview

In this lesson, we continue the **Registration with Spring Security** series by implementing the **“I forgot my password”** feature. This functionality allows a user who no longer remembers their password to safely reset it using their email address.

This is a **critical feature for production systems**, since most real-world applications must provide a secure and user-friendly password recovery mechanism.

The reset process is based on:

* Email as the recovery channel
* Secure, time-limited tokens
* Server-side validation
* Encrypted password storage

---

## 2. Refactoring Registration Email Logic

### 2.1 Moving Verification Email Logic

In a previous lesson, the verification email logic (used during registration) was implemented directly inside the `RegistrationController`.

To improve design and maintainability:

* The email logic was **moved into a `RegistrationListener`**
* The controller remains clean and focused
* Side effects (email sending) are handled separately

This pattern will be reused conceptually for password reset emails.

---

## 3. Password Reset Flow Overview

A typical password reset flow consists of the following steps:

1. User clicks **“I forgot my password”** on the login page
2. User enters their email address
3. System generates a **password reset token**
4. System emails a reset link containing the token
5. User clicks the link
6. User sets a new password
7. System validates the token and updates the password

---

### (Diagram Space – Password Reset Flow)

```
Login Page
   ↓
Forgot Password Page
   ↓
Enter Email
   ↓
Generate Reset Token
   ↓
Send Email
   ↓
User Clicks Link
   ↓
Reset Password Page
   ↓
Save New Password
```

---

## 4. Creating the Forgot Password Page

### 4.1 View Controller Configuration

In `LssWebMvcConfiguration`, the forgot password page is mapped:

```java
registry.addViewController("/forgotPassword")
        .setViewName("forgotPassword");
```

---

### 4.2 Security Configuration

The forgot password page must be publicly accessible.

In `LssSecurityConfig`:

```java
http
    .authorizeRequests()
        .antMatchers(
            "/forgotPassword*",
            ...
        ).permitAll();
```

This allows unauthenticated users to start the reset process.

---

## 5. forgotPassword.html

The first UI page asks the user for their email address.

```html
<html>
<body>
    <h1 th:text="#{message.resetPassword}">reset</h1>

    <label th:text="#{label.user.email}">email</label>
    <input id="email" name="email" type="email" value="" />
    <button type="submit" onclick="resetPass()" 
      th:text="#{message.resetPassword}">reset</button>

    <a th:href="@{/registration.html}"
       th:text="#{label.form.loginSignUp}">registration</a>
    <a th:href="@{/login}"
       th:text="#{label.form.loginLink}">login</a>

<script src="jquery.min.js"></script>
<script th:inline="javascript">
var serverContext = [[@{/}]];
function resetPass(){
    var email = $("#email").val();
    $.post(serverContext + "user/resetPassword",{email: email} ,
      function(data){
          window.location.href = 
           serverContext + "login?message=" + data.message;
    })
    .fail(function(data) {
        if(data.responseJSON.error.indexOf("MailError") > -1) {
            window.location.href = serverContext + "emailError.html";
        } else {
            window.location.href = 
              serverContext + "login?message=" + data.responseJSON.message;
        }
    });
}
</script>
</body>
</html>
```

This page:

* Accepts the user’s email
* Calls the reset endpoint via AJAX
* Redirects the user appropriately

---

### 5.1 Login Page Link

The login page provides an entry point:

```html
<a th:href="@{/forgetPassword.html}" 
   th:text="#{message.resetPassword}">reset</a>
```

---

## 6. Password Reset Token

### 6.1 Purpose of the Token

A **PasswordResetToken**:

* Identifies the password reset request
* Links the request to a specific user
* Expires after a fixed duration (24 hours)
* Prevents long-term reuse of reset links

---

### 6.2 PasswordResetToken Entity

```java
@Entity
public class PasswordResetToken {

    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;
}
```

---

## 7. Triggering the Reset Process

### 7.1 Controller Logic

```java
@PostMapping("/user/resetPassword")
public GenericResponse resetPassword(HttpServletRequest request, 
  @RequestParam("email") String userEmail) {

    User user = userService.findUserByEmail(userEmail);
    if (user == null) {
        throw new UserNotFoundException();
    }

    String token = UUID.randomUUID().toString();
    userService.createPasswordResetTokenForUser(user, token);

    mailSender.send(
      constructResetTokenEmail(
        getAppUrl(request),
        request.getLocale(),
        token,
        user
      )
    );

    return new GenericResponse(
      messages.getMessage("message.resetPasswordEmail", null, 
      request.getLocale())
    );
}
```

---

### 7.2 Token Creation Service Method

```java
public void createPasswordResetTokenForUser(User user, String token) {
    PasswordResetToken myToken = new PasswordResetToken(token, user);
    passwordTokenRepository.save(myToken);
}
```

---

### 7.3 Sending the Reset Email

```java
private SimpleMailMessage constructResetTokenEmail(
  String contextPath, Locale locale, String token, User user) {

    String url = contextPath + "/user/changePassword?token=" + token;
    String message = messages.getMessage("message.resetPassword", null, locale);

    return constructEmail(
      "Reset Password",
      message + " \r\n" + url,
      user
    );
}
```

---

### 7.4 GenericResponse Object

```java
public class GenericResponse {
    private String message;
    private String error;

    public GenericResponse(String message) {
        this.message = message;
    }

    public GenericResponse(String message, String error) {
        this.message = message;
        this.error = error;
    }
}
```

---

## 8. Verifying the Reset Token

When the user clicks the email link, they reach `/user/changePassword`.

```java
@GetMapping("/user/changePassword")
public String showChangePasswordPage(
  Locale locale,
  Model model,
  @RequestParam("token") String token) {

    String result = securityService.validatePasswordResetToken(token);
    if(result != null) {
        String message = messages.getMessage(
          "auth.message." + result, null, locale);
        return "redirect:/login.html?lang=" 
          + locale.getLanguage() + "&message=" + message;
    }

    model.addAttribute("token", token);
    return "redirect:/updatePassword.html?lang=" + locale.getLanguage();
}
```

---

### 8.1 Token Validation Logic

```java
public String validatePasswordResetToken(String token) {
    final PasswordResetToken passToken =
        passwordTokenRepository.findByToken(token);

    return !isTokenFound(passToken) ? "invalidToken"
         : isTokenExpired(passToken) ? "expired"
         : null;
}
```

---

## 9. Updating the Password

### 9.1 updatePassword.html

```html
<html>
<body>
<div sec:authorize="hasAuthority('CHANGE_PASSWORD_PRIVILEGE')">
    <h1 th:text="#{message.resetYourPassword}">reset</h1>
    <form>
        <label th:text="#{label.user.password}">password</label>
        <input id="password" name="newPassword" type="password" />

        <label th:text="#{label.user.confirmPass}">confirm</label>
        <input id="matchPassword" type="password" />

        <input id="token" name="token" value="" />

        <div id="globalError" style="display:none"></div>
        <button type="submit" onclick="savePass()"
          th:text="#{message.updatePassword}">submit</button>
    </form>
</div>
</body>
</html>
```

---

### 9.2 Saving the New Password

```java
@PostMapping("/user/savePassword")
public GenericResponse savePassword(
  final Locale locale,
  @Valid PasswordDto passwordDto) {

    String result =
      securityUserService.validatePasswordResetToken(passwordDto.getToken());

    if(result != null) {
        return new GenericResponse(
          messages.getMessage("auth.message." + result, null, locale));
    }

    Optional<User> user =
      userService.getUserByPasswordResetToken(passwordDto.getToken());

    if(user.isPresent()) {
        userService.changeUserPassword(
          user.get(), passwordDto.getNewPassword());
        return new GenericResponse(
          messages.getMessage("message.resetPasswordSuc", null, locale));
    }

    return new GenericResponse(
      messages.getMessage("auth.message.invalid", null, locale));
}
```

---

### 9.3 Password Update Logic

```java
public void changeUserPassword(User user, String password) {
    user.setPassword(passwordEncoder.encode(password));
    repository.save(user);
}
```

---

### 9.4 PasswordDto

```java
public class PasswordDto {

    private String oldPassword;
    private String token;

    @ValidPassword
    private String newPassword;
}
```

---

## 10. Password Encryption (Upgrade Notes)

Since Spring Boot 2:

* **Passwords must be encrypted**

### PasswordEncoder Bean

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```

### Usage

```java
user.setPassword(passwordEncoder().encode(password));
```

---

## 11. Events in Spring (Supporting Concepts)

Spring provides an event system using:

* `ApplicationEventPublisher`
* Custom events
* Synchronous and asynchronous listeners

This infrastructure is useful for:

* Email sending
* Audit logging
* Notification handling

Password reset emails can also be triggered using events for cleaner design.

---

## 12. Lesson Takeaways

In this lesson, we:

* Created a public password reset entry point
* Generated secure, expiring reset tokens
* Sent password reset emails
* Validated reset tokens
* Encrypted and updated user passwords
* Completed a full end-to-end password reset flow

This functionality is **essential for any real-world authentication system** and significantly improves usability and security.

---

