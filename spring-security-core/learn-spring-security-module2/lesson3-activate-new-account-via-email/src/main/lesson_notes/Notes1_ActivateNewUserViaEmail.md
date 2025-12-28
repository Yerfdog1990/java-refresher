
---

# Activate a New Account via Email

## 1. Introduction

Activating a new account via email is a critical security enhancement in modern applications. Instead of allowing newly registered users to log in immediately, the system requires them to verify ownership of their email address. This ensures that:

* Only valid email owners can activate accounts
* Fake or mistyped email registrations are reduced
* The overall security of the authentication system is strengthened

The core idea is simple:

1. Register the user in a **disabled** state
2. Generate a **verification token**
3. Send a verification email containing a unique link
4. Enable the user only after successful verification

---

## 2. Verification Token

### 2.1. Purpose of the Verification Token

The verification token is the key artifact that drives the entire email verification process. It is used to uniquely identify a registration attempt and confirm the user’s identity.

The verification token must satisfy the following requirements:

* It links back to a `User` (unidirectional relationship)
* It is created immediately after registration
* It expires within 24 hours of creation
* It has a unique, randomly generated value

---

### 2.2. The VerificationToken Entity

The token is implemented as a JPA entity:

```java
@Entity
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    // standard constructors, getters, and setters
}
```

**Key points:**

* `nullable = false` ensures data integrity between token and user
* Expiration logic ensures tokens are time-bound
* The token value is generated using `UUID`

---

### 2.3. VerificationToken Repository

A Spring Data JPA repository provides persistence operations:

```java
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);
    VerificationToken findByUser(User user);
}
```

---

## 3. Adding the Enabled Field to the User

Initially, newly registered users must not be allowed to authenticate. This is achieved by adding an `enabled` field to the `User` entity.

```java
public class User {
    ...
    @Column(name = "enabled")
    private boolean enabled;

    public User() {
        super();
        this.enabled = false;
    }
    ...
}
```

**Important behavior:**

* Users are created with `enabled = false`
* Only verified users are allowed to authenticate

---

## 4. Registration Logic Enhancements

During registration, two additional operations are introduced:

1. Generate and persist a verification token
2. Send an email containing the verification link

---

### 4.1. Publishing a Registration Event

Instead of performing email logic directly in the controller, a Spring `ApplicationEvent` is published.

```java
@Autowired
ApplicationEventPublisher eventPublisher;

@PostMapping("/user/registration")
public ModelAndView registerUserAccount(
  @ModelAttribute("user") @Valid UserDto userDto,
  HttpServletRequest request, Errors errors) {

    try {
        User registered = userService.registerNewUserAccount(userDto);

        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(
          new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));
    } catch (UserAlreadyExistException ex) {
        return new ModelAndView("registration", "user", userDto)
          .addObject("message", "An account for that email already exists.");
    } catch (RuntimeException ex) {
        return new ModelAndView("emailError", "user", userDto);
    }

    return new ModelAndView("successRegister", "user", userDto);
}
```

This approach keeps the controller clean and delegates collateral logic to listeners.

---

### 4.2. Registration Completion Event

```java
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private Locale locale;
    private User user;

    public OnRegistrationCompleteEvent(User user, Locale locale, String appUrl) {
        super(user);
        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    // getters and setters
}
```

---

### 4.3. Registration Listener

The listener handles token creation and email sending.

```java
@Component
public class RegistrationListener
  implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private IUserService service;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl =
          event.getAppUrl() + "/regitrationConfirm?token=" + token;
        String message =
          messages.getMessage("message.regSucc", null, event.getLocale());

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + confirmationUrl);
        mailSender.send(email);
    }
}
```

---

## 5. Verifying Registration

When the user clicks the verification link, the token is processed by the controller.

```java
@GetMapping("/regitrationConfirm")
public String confirmRegistration(
  WebRequest request, Model model, @RequestParam("token") String token) {

    Locale locale = request.getLocale();
    VerificationToken verificationToken = service.getVerificationToken(token);

    if (verificationToken == null) {
        model.addAttribute("message",
          messages.getMessage("auth.message.invalidToken", null, locale));
        return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }

    User user = verificationToken.getUser();
    Calendar cal = Calendar.getInstance();
    if ((verificationToken.getExpiryDate().getTime()
        - cal.getTime().getTime()) <= 0) {
        model.addAttribute("message",
          messages.getMessage("auth.message.expired", null, locale));
        return "redirect:/badUser.html?lang=" + locale.getLanguage();
    }

    user.setEnabled(true);
    service.saveRegisteredUser(user);
    return "redirect:/login.html?lang=" + locale.getLanguage();
}
```

### Error Conditions

The user is redirected to an error page if:

* The token does not exist
* The token has expired

---

### Error Page (`badUser.html`)

```html
<html>
<body>
    <h1 th:text="${param.message[0]}">Error Message</h1>
    <a th:href="@{/registration.html}">signup</a>
</body>
</html>
```

---

## 6. Enforcing Activation During Login

### 6.1. UserDetailsService Update

```java
public UserDetails loadUserByUsername(String email)
  throws UsernameNotFoundException {

    User user = userRepository.findByEmail(email);
    if (user == null) {
        throw new UsernameNotFoundException("No user found with email: " + email);
    }

    return new org.springframework.security.core.userdetails.User(
      user.getEmail(),
      user.getPassword().toLowerCase(),
      user.isEnabled(),
      true,
      true,
      true,
      getAuthorities(user.getRole()));
}
```

Only **enabled users** can authenticate.

---

### 6.2. Custom Authentication Failure Handler

```java
@Component
public class CustomAuthenticationFailureHandler
  extends SimpleUrlAuthenticationFailureHandler {

    @Autowired
    private MessageSource messages;

    @Autowired
    private LocaleResolver localeResolver;

    @Override
    public void onAuthenticationFailure(
      HttpServletRequest request,
      HttpServletResponse response,
      AuthenticationException exception)
      throws IOException, ServletException {

        setDefaultFailureUrl("/login.html?error=true");
        super.onAuthenticationFailure(request, response, exception);

        Locale locale = localeResolver.resolveLocale(request);
        String errorMessage =
          messages.getMessage("message.badCredentials", null, locale);

        if (exception.getMessage().equalsIgnoreCase("User is disabled")) {
            errorMessage =
              messages.getMessage("auth.message.disabled", null, locale);
        }

        request.getSession().setAttribute(
          WebAttributes.AUTHENTICATION_EXCEPTION, errorMessage);
    }
}
```

---

### 6.3. Displaying Errors on Login Page

```html
<div th:if="${param.error != null}"
     th:text="${session[SPRING_SECURITY_LAST_EXCEPTION]}">
    error
</div>
```

---

## 7. Email Infrastructure

### 7.1. Maven Dependencies

**Spring Boot**

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-mail</artifactId>
    <version>3.5.7</version>
</dependency>
```

---

### 7.2. SMTP Configuration (Gmail Example)

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=<smtp-username>
spring.mail.password=<app-password>
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

---

