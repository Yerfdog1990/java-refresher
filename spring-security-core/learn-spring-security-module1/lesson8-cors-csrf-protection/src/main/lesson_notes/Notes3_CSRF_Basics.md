
---

# Cross-Site Request Forgery (CSRF) in Spring Security

## Introduction

Cross-Site Request Forgery (CSRF) is a security vulnerability that occurs when an attacker tricks a user into performing unwanted actions on a web application in which they are authenticated. Protecting against CSRF is crucial in applications where users log in and perform state-changing actions.

Spring Security provides **built-in CSRF protection** for unsafe HTTP methods like `POST`, `PUT`, and `DELETE`. By default, this protection is enabled, so no additional code is strictly necessary. You can, however, configure it explicitly.

### Basic CSRF Configuration

**Java Example:**

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // other security configurations
            .csrf(Customizer.withDefaults());
        return http.build();
    }
}
```

**Kotlin Example:**

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() } // Or customize as needed
        return http.build()
    }
}
```

**XML Example:**

```xml
<http>
    <csrf/>
</http>
```

---

## Key CSRF Use Cases

When working with CSRF protection, you might encounter several scenarios:

* Understanding the components of CSRF protection.
* Migrating an application from Spring Security 5 to 6.
* Storing the CSRF token in a cookie instead of the session.
* Storing the CSRF token in a custom location.
* Opting out of deferred token loading.
* Opting out of BREACH protection.
* Integrating with front-end technologies like Thymeleaf, JSPs, Angular, or other JavaScript frameworks.
* Integrating with mobile applications or other clients.
* Handling errors related to CSRF.
* Testing CSRF protection.
* Disabling CSRF protection for specific endpoints or entirely.

---

## Components of CSRF Protection

CSRF protection in Spring Security relies on several components within the `CsrfFilter`.

---
![img.png](img.png)
---
**Two main parts of CSRF protection:**

1. **Token availability** – The `CsrfTokenRequestHandler` makes the token accessible to the application.
2. **Token validation** – Checks whether a request requires CSRF protection, validates the token, and handles exceptions (`AccessDeniedException`) if validation fails.

### CSRF Processing Flow

---
![img_1.png](img_1.png)
---

1. **Deferred token loading:** `DeferredCsrfToken` holds a reference to `CsrfTokenRepository` to load the persisted token later in 4.
2. **Token handler setup:** A `Supplier<CsrfToken>` is passed to `CsrfTokenRequestHandler` to populate the request attribute.
3. **CSRF check:** The filter determines if the current request needs CSRF protection.
4. **Token retrieval:** The persisted CSRF token is loaded.
5. **Client token resolution:** The token provided by the client is resolved.
6. **Validation:** The client token is compared against the persisted token.
7. **Access handling:** If invalid, an `AccessDeniedException` is triggered.

---

## Migrating to Spring Security 6

Changes in CSRF handling from Spring Security 5 to 6:

* **Deferred token loading by default:** Improves performance by avoiding session access on every request.
* **Randomized tokens:** Added randomness on every request to protect against BREACH attacks.
* **Single-page applications (SPA):** Require additional configuration for proper integration.

---

## Persisting the CSRF Token

Spring Security uses a `CsrfTokenRepository` to store tokens.

### HttpSessionCsrfTokenRepository

* Default repository stores the token in the `HttpSession`.
* To customize the session attribute name:

```java
HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
repository.setSessionAttributeName("MY_CSRF_TOKEN");
```

* Configuration example:

```java
http.csrf(csrf -> csrf.csrfTokenRepository(new HttpSessionCsrfTokenRepository()));
```

### CookieCsrfTokenRepository

* Stores the CSRF token in a cookie, useful for JavaScript-based clients.
* Default behavior aligns with Angular conventions (`XSRF-TOKEN` cookie, `X-XSRF-TOKEN` header).
* Example configuration:

```java
http.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));
```

* Set `HttpOnly=false` to allow JavaScript access.

---

## Custom CSRF Token Repository

You can implement your own `CsrfTokenRepository` if you want to store the CSRF token in a custom location, such as a database or a different type of storage.

**Example Configuration:**

```java
http.csrf(csrf -> csrf.csrfTokenRepository(new CustomCsrfTokenRepository()));
```

---

## Handling the CSRF Token

The CSRF token is made available to your application using a `CsrfTokenRequestHandler`. This component also resolves the token from HTTP headers or request parameters.

### XorCsrfTokenRequestAttributeHandler (with BREACH Protection)

* Exposes the CSRF token as an HTTP request attribute `_csrf`.
* Adds randomness to the token to protect against BREACH attacks.
* Resolves the token from either request headers (`X-CSRF-TOKEN`, `X-XSRF-TOKEN`) or a request parameter (`_csrf`).
* Configuration example:

```java
http.csrf(csrf -> csrf.csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler()));
```

### CsrfTokenRequestAttributeHandler (without BREACH Protection)

* Similar to the Xor handler but does not provide BREACH protection.
* Useful if you want to disable BREACH protection for performance or compatibility reasons.
* Configuration example:

```java
http.csrf(csrf -> csrf.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()));
```

### Custom CSRF Token Request Handler

You can implement the `CsrfTokenRequestHandler` interface to fully customize how tokens are resolved from requests:

```java
http.csrf(csrf -> csrf.csrfTokenRequestHandler(new CustomCsrfTokenRequestHandler()));
```

---

## Deferred Loading of CSRF Tokens

By default, Spring Security **defers loading** of CSRF tokens until they are needed (e.g., on POST requests or when rendering a form). This improves performance by avoiding unnecessary session access.

* To **opt out of deferred loading** and load the token on every request:

```java
XorCsrfTokenRequestAttributeHandler requestHandler = new XorCsrfTokenRequestAttributeHandler();
requestHandler.setCsrfRequestAttributeName(null);
http.csrf(csrf -> csrf.csrfTokenRequestHandler(requestHandler));
```

---

## Integrating CSRF Protection

### HTML Forms

* Include the CSRF token in a hidden input field for unsafe HTTP methods like POST:

```html
<input type="hidden" name="_csrf" value="CSRF_TOKEN_HERE"/>
```

* Supported automatically in:

    * Spring’s form tag library
    * Thymeleaf
    * Any view technology integrating with `RequestDataValueProcessor`

* Example using JSP request attributes:

```jsp
<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
```

### JavaScript Applications

* Typically use JSON; include the CSRF token in HTTP request headers instead of request parameters.
* Store the CSRF token in a cookie using `CookieCsrfTokenRepository`.
* Angular and similar frameworks can automatically read the token from the cookie.

#### Single-Page Applications (SPA)

* Special considerations for BREACH protection and deferred tokens.
* Cookies storing CSRF tokens are cleared on authentication/logout, so you may need to refresh the token.
* Configuration example for SPA:

```java
http.csrf(csrf -> csrf.spa());
```

#### Multi-Page Applications

* Include CSRF tokens in `<meta>` tags for JavaScript to read:

```html
<meta name="_csrf" content="${_csrf.token}"/>
<meta name="_csrf_header" content="${_csrf.headerName}"/>
```

* JavaScript can then add the token to AJAX requests:

```javascript
var token = $("meta[name='_csrf']").attr("content");
var header = $("meta[name='_csrf_header']").attr("content");
$(document).ajaxSend(function(e, xhr) {
    xhr.setRequestHeader(header, token);
});
```

### Other JavaScript Approaches

* Expose the CSRF token in HTTP response headers using `@ControllerAdvice`:

```java
@ControllerAdvice
public class CsrfControllerAdvice {
    @ModelAttribute
    public void getCsrfToken(HttpServletResponse response, CsrfToken csrfToken) {
        response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
    }
}
```

* Useful for obtaining the token on-demand for single-page or custom JavaScript applications.

---

### Mobile Applications

* Mobile apps typically use JSON requests.
* If the backend also serves browser traffic, continue storing the CSRF token in the session.
* A common pattern: provide a `/csrf` endpoint to request a fresh CSRF token:

```java
@RestController
public class CsrfController {

    @GetMapping("/csrf")
    public CsrfToken csrf(CsrfToken csrfToken) {
        return csrfToken;
    }
}
```

* Consider `.requestMatchers("/csrf").permitAll()` if token retrieval must occur before login.

---

## Handling `AccessDeniedException`

When a CSRF token is invalid or missing, Spring Security throws an `AccessDeniedException` (e.g., `InvalidCsrfTokenException`). You can configure a custom handler:

```java
http.exceptionHandling(exceptionHandling ->
    exceptionHandling.accessDeniedPage("/access-denied")
);
```

This allows you to redirect users to a specific page or handle errors in a custom way.

---

## CSRF Testing

Spring Security provides testing support with `CsrfRequestPostProcessor`:

```java
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SecurityConfig.class)
@WebAppConfiguration
public class CsrfTests {

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp(WebApplicationContext applicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    public void loginWhenValidCsrfTokenThenSuccess() throws Exception {
        this.mockMvc.perform(post("/login").with(csrf())
                        .accept(MediaType.TEXT_HTML)
                        .param("username", "user")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, "/"));
    }

    @Test
    public void loginWhenInvalidCsrfTokenThenForbidden() throws Exception {
        this.mockMvc.perform(post("/login").with(csrf().useInvalidToken())
                        .accept(MediaType.TEXT_HTML)
                        .param("username", "user")
                        .param("password", "password"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void loginWhenMissingCsrfTokenThenForbidden() throws Exception {
        this.mockMvc.perform(post("/login")
                        .accept(MediaType.TEXT_HTML)
                        .param("username", "user")
                        .param("password", "password"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser
    public void logoutWhenValidCsrfTokenThenSuccess() throws Exception {
        this.mockMvc.perform(post("/logout").with(csrf())
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string(HttpHeaders.LOCATION, "/login?logout"));
    }
}
```

* You can test login, logout, valid, invalid, or missing CSRF tokens.
* Use `@WithMockUser` for testing authenticated scenarios.

---

## Disabling CSRF Protection

While CSRF protection is enabled by default, there are scenarios where you may want to disable it:

### Disable entirely:

```java
http.csrf(csrf -> csrf.disable());
```

### Ignore certain endpoints:

```java
http.csrf(csrf -> csrf.ignoringRequestMatchers("/api/*"));
```

This is useful for stateless REST APIs or endpoints that don’t require CSRF protection.

---

## CSRF Considerations

### Logging In

* Always require CSRF for login requests to prevent forged login attempts.
* Spring Security enforces this by default.

### Logging Out

* Require CSRF for logout requests to prevent forced logouts.
* Use POST for logout forms.
* If a GET is needed, it can be configured but is **not recommended**:

```java
http.logout(logout -> logout.logoutRequestMatcher(
    PathPatternRequestMatcher.withDefaults().matcher("/logout")
));
```

### Session Timeouts

* Tokens stored in `HttpSessionCsrfTokenRepository` may become invalid if the session expires.
* Options:

    * Use `CookieCsrfTokenRepository` instead.
    * Handle expired tokens with a custom `AccessDeniedHandler`.

---

## Multipart (File Upload) Requests

Protecting file uploads requires special handling because of the "chicken and egg" problem with CSRF:

### 1. Include CSRF in the Body

* Configure `MultipartFilter` **before** the Spring Security filter:

```java
public class SecurityApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
    @Override
    protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
        insertFilters(servletContext, new MultipartFilter());
    }
}
```

### 2. Include CSRF in the URL

* If letting unauthenticated users upload temporary files is not acceptable, place CSRF as a query parameter in the form’s `action`:

```jsp
<form method="post" action="./upload?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
```

---

## Hidden HTTP Method Filter

* In Spring Servlet support, `HiddenHttpMethodFilter` allows overriding HTTP methods (e.g., POST to PUT or DELETE).
* Ensure CSRF token handling aligns with the overridden method.

---

## Summary

* **CSRF protection** defends against cross-site request forgery attacks.
* Spring Security enables CSRF protection by default for unsafe HTTP methods.
* **Tokens** can be stored in sessions, cookies, or custom repositories.
* **Request handlers** manage token resolution and optional BREACH protection.
* Frontend integration differs based on HTML forms, JavaScript apps, SPAs, multi-page apps, or mobile apps.
* Testing, exception handling, and optional disabling are supported for flexibility.
* Special considerations include login/logout protection, session timeouts, multipart uploads, and method overrides.

---

