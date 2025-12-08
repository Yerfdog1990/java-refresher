
---

# **🔥 Redirecting to Different Pages + Custom Logout **

## **1. Why Redirecting Matters**

In multi-role systems (USER, ADMIN, ANALYST, etc.), authentication isn’t enough — we must route each authenticated user to the correct **post-login landing page**, based on:

* their **role**
* the **page they originally requested**
* or a **custom rule** (MFA state, session freshness, login time, etc.)

Spring Security lets us do this with a **custom AuthenticationSuccessHandler**.

---

## **2. The Architecture of Login Redirection**

When your login form posts:

```html
<form th:action="@{/login}" method="post">
```

The POST **does NOT go to your controller**.
It goes to:

```
UsernamePasswordAuthenticationFilter → AuthenticationManager
```

If successful, the filter calls:

```
AuthenticationSuccessHandler.onAuthenticationSuccess()
```

### This means **ALL redirect logic belongs in the Success Handler**, not in controllers.

---

## **3. Writing a Custom Success Handler (Conceptual)**

A custom handler allows rules like:

```
ROLE_USER  → /user/home
ROLE_ADMIN → /admin/home
```

That is exactly where your `UserHome.html` and `AdminHome.html` pages fit into the architecture.

This gives you full control to:

* Redirect based on **roles**
* Redirect based on **custom attributes**
* Log login events
* Track login IP/device
* Add MFA states

---

## **4. How Spring Security Decides Redirects (Internals)**

Order of decision making:

1. If the user originally requested a protected resource, Spring redirects them back there
   Example: user tries `/admin/home` → gets redirected to `/login` → after login → back to `/admin/home`.

2. If **no original URL**, the SuccessHandler decides.

3. If you use your own SuccessHandler, it **overrides** default behavior.

For advanced behavior (e.g., preventing privilege escalation), we typically **always enforce role-based redirect**, even overriding saved requests.

---

## **5. Integrating Your Pages Into The Redirect Flow**

Your pages:

### **/user/home**

```java
@GetMapping("/user/home")
public String userHome() {
    return "UserHome";
}
```

### **/admin/home**

```html
<h1>Welcome Admin!</h1>
<form th:action="@{/custom-logout}" method="post">
```

With a custom handler, Spring sends authenticated users to **one of these two pages** depending on their authorities.

---

# **6. Custom Logout Flow**

```html
<form th:action="@{/custom-logout}" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}"/>
    <button type="submit">Logout</button>
</form>
```

This is the correct advanced pattern.

### Why?

Because:

* Spring Security **requires POST logout** when CSRF is enabled.
* The logout request must include a **valid CSRF token**.
* Your form uses `${_csrf.parameterName}` and `${_csrf.token}` — correct.

### Behind the scenes, logout triggers:

```
LogoutFilter
  → clears SecurityContext
  → invalidates HttpSession
  → deletes cookies
  → calls logoutSuccessHandler or redirects to logoutSuccessUrl
```

This ensures:

* No session fixation
* No cached credentials
* No ability to “back-button” into authenticated pages

---

## **7. How Spring Decides the Logout Redirect**

After logout, you will normally want:

* redirect to `/login?logout`
* OR a dedicated logout-complete page

Your HTML is already wired correctly.
You just need logout config in the security chain (conceptually):

```
.logoutUrl("/custom-logout")
.logoutSuccessUrl("/login?logout")
```

This matches your form exactly.

---

### **Quick Check:**

Do you want logout to always redirect to `/login?logout`, or to a custom message page?
(choose one)

---

# **8. COMPLETE LESSON SUMMARY**

Here is a condensed “senior-level” memory sheet:

---

### **🔹 Redirecting After Login**

* Done through a **custom AuthenticationSuccessHandler**
* Should use **role-based routing** for multi-role systems
* Overrides default saved-request behavior
* Cleaner and safer than controller-based redirects
* Works directly with your login POST `/login` form

---

### **🔹 How Your Pages Fit**

* `/user/home` and `/admin/home` serve as post-login landing pages
* The handler inspects `authentication.getAuthorities()` to choose the target page
* Controllers remain “dumb”: no redirect logic inside them

---

### **🔹 Custom Logout**

* Your HTML is correct: POST + CSRF token
* Triggers Spring’s `LogoutFilter`
* Clears session + security context
* Redirection is controlled by `logoutSuccessUrl` or a custom handler
* Ensures secure and complete logout

---

# **Next Step (Your Choice):**

Which section would you like me to guide you through next?

1. **Writing the custom success handler (full advanced explanation).**
2. **Wiring the SecurityFilterChain for login + logout + role redirects.**
3. **Deep dive into filter chain behavior during authentication/logout.**

Pick one, and we’ll build it step by step.
