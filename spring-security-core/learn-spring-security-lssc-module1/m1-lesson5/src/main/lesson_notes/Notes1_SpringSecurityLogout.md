
---

# **Lesson Notes: Implementing Logout in Spring Security**

Logout is a core part of any secure application. Once users sign in, they must have a safe and predictable way to end their session. Spring Security provides a powerful and flexible logout mechanism with sensible defaults, while also offering configuration options for more advanced scenarios.

---

# **1. Enabling Logout Support**

Spring Security enables logout automatically when you include:

```java
spring-boot-starter-security
```

or use:

```java
@EnableWebSecurity
```

This means a default **`/logout`** endpoint is already available.
To explicitly enable logout in the HTTP configuration:

```java
http.logout().permitAll();
```

### **Default Behavior**

* Logout URL: **`/logout`**
* Logout method: **POST**
* After logout → redirect to **`/login?logout`**
* CSRF required for POST logout
* Session invalidated
* Authentication cleared

---

# **2. Customizing the Logout URL**

You can change the default logout endpoint:

```java
http.logout().logoutUrl("/doLogout");
```

### Client-side link using Thymeleaf:

```html
<a th:href="@{/doLogout}" class="menu-right">Logout</a>
```

This link calls the backend endpoint you defined.

---

# **3. Restricting the HTTP Method (Using RequestMatcher)**

Sometimes you want more control over how logout is triggered.
Spring Security allows specifying both URL **and HTTP method**:

```java
.logout()
.logoutRequestMatcher(
        new AntPathRequestMatcher("/doLogout", "GET")
)
```

### ⚠ Important Note on CSRF

* With **CSRF enabled** → **GET logout does NOT work**
* Recommended method: **POST**, because logout changes application state

For real systems:

* Use **POST**
* Trigger logout using a form with a CSRF token

Example logout form:

```html
<form th:action="@{/doLogout}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}">
    <button type="submit">Logout</button>
</form>
```

---

# **4. Adding a Logout Confirmation Message**

When logout succeeds, Spring Security automatically appends `?logout` to the redirect URL.

To display a message on the login page:

```html
<div th:if="${param.logout}">
    You have been logged out.
</div>
```

---

# **5. How Spring Security Handles Logout Internally**

When the user requests **POST /logout**, Spring Security executes several **LogoutHandler** components:

### **Default Logout Handlers**

1. **Invalidate HTTP session**
   `SecurityContextLogoutHandler`

2. **Clear SecurityContext**
   `SecurityContextLogoutHandler`

3. **Clear CSRF token**
   `CsrfLogoutHandler`

4. **Clear Remember-Me tokens**
   `TokenRememberMeServices`

5. **Publish logout event**
   `LogoutSuccessEventPublishingLogoutHandler`

After all handlers complete, the **LogoutSuccessHandler** executes, redirecting to:

```
/login?logout
```

---

# **6. Customizing Logout Success Behavior**

### **6.1 Redirect to a Custom Page**

```java
.logout()
    .logoutSuccessUrl("/goodbye")
    .permitAll();
```

### **6.2 Return HTTP Status Code Instead of Redirect**

```java
.logout()
    .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
```

### **6.3 Create a Custom LogoutSuccessHandler**

```java
.logout()
    .logoutSuccessHandler((request, response, authentication) -> {
        // custom logic here
        response.sendRedirect("/custom-logout");
    });
```

---

# **7. Cleanup Options During Logout**

Spring Security enables customization of what gets cleared on logout.

### **7.1 Clear Authentication**

```java
.logout().clearAuthentication(true);
```

(Default is true)

### **7.2 Delete Cookies on Logout**

```java
.logout().deleteCookies("custom-cookie");
```

### **7.3 Invalidate Session**

```java
.logout().invalidateHttpSession(true);
```

(Default is true)

### **7.4 Using Clear-Site-Data Header**

Modern browsers support clearing site data via headers:

```java
HeaderWriterLogoutHandler clearSiteData =
    new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(Directives.ALL));

http.logout().addLogoutHandler(clearSiteData);
```

To only clear cookies:

```java
new ClearSiteDataHeaderWriter(Directive.COOKIES)
```

This ensures cookies, storage, and cache are wiped securely.

---

# **8. Custom Logout Endpoints (Advanced Use)**

If you must implement a logout endpoint manually:

```java
@PostMapping("/my/logout")
public String performLogout(Authentication authentication,
        HttpServletRequest request,
        HttpServletResponse response) {

    new SecurityContextLogoutHandler().logout(request, response, authentication);
    return "redirect:/home";
}
```

### ⚠ Important:

* You **must** call `SecurityContextLogoutHandler.logout()`
  Otherwise the user will not actually be logged out.
* You must configure Spring Security to allow your custom endpoint:

```java
.authorizeHttpRequests()
    .requestMatchers("/my/logout").permitAll();
```

---

# **9. When You Must Permit Logout Explicitly**

By default:
**You do NOT need to permit `/logout`** because `LogoutFilter` runs earlier in the filter chain.

But if you configure:

* A *custom logout success URL*, or
* A *custom logout endpoint implemented in a controller*

Then you must explicitly permit it:

```java
.logout()
    .logoutSuccessUrl("/my/success")
    .permitAll();
```

---

# **10. Summary of What You Learned**

✔ Enabled logout in Spring Security
✔ Changed logout URL
✔ Added logout link using Thymeleaf
✔ Custom logout HTTP method via `AntPathRequestMatcher`
✔ Displayed logout confirmation message
✔ Understood Spring Security’s logout architecture
✔ Customized:

* logout success behavior
* cookie clearing
* session invalidation
* Clear-Site-Data header
  ✔ Implemented custom logout endpoints properly

Logout in Spring Security is both simple and powerful.
You can rely on defaults or deeply customize the logic to match your application’s needs.

---

