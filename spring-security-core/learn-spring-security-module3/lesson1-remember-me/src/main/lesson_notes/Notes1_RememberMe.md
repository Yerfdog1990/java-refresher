
---

# Remember-Me Authentication

---

## Lesson Objective

In this lesson, we explore **Remember-Me authentication** in Spring Security. Remember-Me allows a web application to **automatically re-authenticate a user across different sessions**, even after the original HTTP session has expired.

By the end of this lesson, you will:

* Understand why Remember-Me is needed
* Configure basic Remember-Me support
* Understand how cookies drive Remember-Me
* Compare hash-based and persistent token strategies
* Understand all core Remember-Me interfaces, filters, and providers
* Configure Remember-Me using both Java and XML
* Observe Remember-Me behavior in the browser

---

## Why Remember-Me Is Needed

Without Remember-Me, users must log in every time they revisit an application because authentication is tied to the **HTTP session**.

Remember-Me addresses this problem by:

* Remembering the authenticated principal across sessions
* Automatically authenticating users when they return

Technically:

> Remember-Me is a mechanism that remembers the authenticated principal between different sessions.

---

## Spring Security Remember-Me Options

Spring Security provides **two Remember-Me strategies**:

1. **Simple hash-based token stored in a cookie**
2. **Persistent token stored in a database**

Both approaches rely on detecting a cookie in future requests and performing **automated login**.

⚠️ **Important Requirement**
Both implementations **require a `UserDetailsService`**.

If an authentication provider does not use `UserDetailsService` (for example, LDAP), Remember-Me **will not work** unless a `UserDetailsService` bean is also present.

---

## 2.1 Basic Remember-Me Configuration

### Enabling Remember-Me in the Security Configuration

The simplest way to enable Remember-Me is by adding the following to the security configuration:

```java
.rememberMe().key("securityAppKey")
```

Key points:

* This enables Remember-Me support
* The `key` is a private value used to protect the Remember-Me token from tampering
* This configuration alone enables a **simple Remember-Me flow**

---

### Adding Remember-Me to the Login Form

To allow users to opt in, a checkbox must be added to the login form:

```html
<div class="form-group">
  <label class="control-label col-xs-2" for="remember">Remember me?</label>
  <input id="remember" type="checkbox" name="remember-me" value="true" />
</div>
```

Important details:

* The input type is `checkbox`
* The `name` **must be `remember-me`**
* Spring Security automatically detects this parameter

---

## Understanding the Simple Remember-Me Flow

### Page 1 – Conceptual Overview

Remember-Me exists because forcing users to re-log in on every visit is not ideal.

Spring Security supports:

* A simple cookie-based approach
* A more secure persistent database-backed approach

In this lesson, we start with the **simple approach**.

---

### Page 2 – Observing Cookies in the Browser

#### Before Login

* No cookies are present

---

#### Login Without Remember-Me

* Only one cookie is created:

  * `JSESSIONID`
* This cookie expires when:

  * The session expires
  * The user logs out

---

#### Login With Remember-Me

Two cookies are created:

* `JSESSIONID`
* `remember-me`

Key differences:

* `JSESSIONID` expires at session end
* `remember-me` persists beyond session expiration
* Default expiration: **two weeks** (configurable)

---

### Page 3 – Removing Cookies Experiment

#### Scenario 1: Without Remember-Me

Steps:

1. Log in without Remember-Me
2. Remove the `JSESSIONID` cookie
3. Refresh the page

Result:

* User is redirected to the login page

---

#### Scenario 2: With Remember-Me

Steps:

1. Log in with Remember-Me
2. Remove the `JSESSIONID` cookie
3. Refresh the page

Result:

* User remains logged in
* Spring Security recreates a new `JSESSIONID`
* Authentication is restored automatically

---

## Simple Hash-Based Token Approach

### How the Token Is Built

The Remember-Me cookie contains a Base64-encoded token:

```
base64(
  username + ":" +
  expirationTime + ":" +
  algorithmName + ":" +
  algorithmHex(
    username + ":" +
    expirationTime + ":" +
    password + ":" +
    key
  )
)
```

---

### Token Components Explained

* **username**
  Identifies the user in `UserDetailsService`

* **password**
  Must match the password in `UserDetails`

* **expirationTime**
  Time when the token expires (milliseconds)

* **key**
  Private key used to prevent token modification

* **algorithmName**
  Algorithm used to sign and verify the token

---

### Security Characteristics

* Token is valid only until expiration
* Token becomes invalid if:

  * Password changes
  * Username changes
  * Key changes

⚠️ **Security Limitation**
A stolen token can be reused from any user agent until it expires.

Mitigation:

* Changing the password immediately invalidates all tokens

For stronger security, use persistent tokens—or avoid Remember-Me entirely.

---

## Enabling Remember-Me with XML

```xml
<http>
    ...
    <remember-me key="myAppKey"/>
</http>
```

If multiple `UserDetailsService` beans exist, specify one using `user-service-ref`.

---

## Remember-Me Interfaces and Filter Integration

### RememberMeServices Interface

```java
Authentication autoLogin(HttpServletRequest request, HttpServletResponse response);

void loginFail(HttpServletRequest request, HttpServletResponse response);

void loginSuccess(HttpServletRequest request,
                  HttpServletResponse response,
                  Authentication successfulAuthentication);
```

How it works:

* `loginSuccess()` and `loginFail()` are invoked by authentication filters
* `autoLogin()` is called by `RememberMeAuthenticationFilter`
* This occurs when the `SecurityContextHolder` has no authentication

This interface allows **pluggable Remember-Me strategies**.

---

## TokenBasedRememberMeServices

### Role and Responsibilities

* Implements the simple hash-based approach
* Generates `RememberMeAuthenticationToken`
* Works with `RememberMeAuthenticationProvider`
* Requires a `UserDetailsService`
* Implements `LogoutHandler` to clear cookies on logout

---

### Algorithm Support

* Default encoding algorithm: **SHA-256**
* Algorithm is embedded in the token
* If no algorithm is present, SHA-256 is used
* Different encoding and matching algorithms can be configured

---

### Custom RememberMeServices Bean (Java)

```java
@Bean
RememberMeServices rememberMeServices(UserDetailsService userDetailsService) {
    RememberMeTokenAlgorithm encodingAlgorithm = RememberMeTokenAlgorithm.SHA256;
    TokenBasedRememberMeServices rememberMe =
        new TokenBasedRememberMeServices(myKey, userDetailsService, encodingAlgorithm);

    rememberMe.setMatchingAlgorithm(RememberMeTokenAlgorithm.MD5);
    return rememberMe;
}
```

---

### Using RememberMeServices in Security Configuration

```java
@Bean
SecurityFilterChain securityFilterChain(HttpSecurity http,
        RememberMeServices rememberMeServices) throws Exception {

    http
        .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
        .rememberMe(remember -> remember
            .rememberMeServices(rememberMeServices)
        );

    return http.build();
}
```

---

## Required Beans for Manual Remember-Me Wiring

To enable Remember-Me fully, the following beans are required:

### RememberMeAuthenticationFilter

```java
@Bean
RememberMeAuthenticationFilter rememberMeFilter() {
    RememberMeAuthenticationFilter filter =
        new RememberMeAuthenticationFilter();

    filter.setRememberMeServices(rememberMeServices());
    filter.setAuthenticationManager(theAuthenticationManager);
    return filter;
}
```

---

### TokenBasedRememberMeServices

```java
@Bean
TokenBasedRememberMeServices rememberMeServices() {
    TokenBasedRememberMeServices services =
        new TokenBasedRememberMeServices();

    services.setUserDetailsService(myUserDetailsService);
    services.setKey("springRocks");
    return services;
}
```

---

### RememberMeAuthenticationProvider

```java
@Bean
RememberMeAuthenticationProvider rememberMeAuthenticationProvider() {
    RememberMeAuthenticationProvider provider =
        new RememberMeAuthenticationProvider();

    provider.setKey("springRocks");
    return provider;
}
```

---

### Manual Integration Requirements

You must:

* Set `RememberMeServices` on `UsernamePasswordAuthenticationFilter`
* Add `RememberMeAuthenticationProvider` to the `AuthenticationManager`
* Add `RememberMeAuthenticationFilter` to the filter chain
* Place it immediately after `UsernamePasswordAuthenticationFilter`

---

## PersistentTokenBasedRememberMeServices

This implementation:

* Extends `TokenBasedRememberMeServices`
* Stores tokens in persistent storage
* Requires a `PersistentTokenRepository`

Available repositories:

* `InMemoryTokenRepositoryImpl` (testing only)
* `JdbcTokenRepositoryImpl` (production)

Database schema is defined by the `persistent_logins` table.

---

## Summary

* Remember-Me improves usability by persisting authentication
* Spring Security supports two strategies:

  * Hash-based cookie tokens
  * Persistent database tokens
* Remember-Me depends on cookies, filters, providers, and services
* Proper wiring is essential for correct behavior
* Persistent tokens provide stronger security guarantees

---

## Diagram: Simple Remember-Me Authentication Flow (Conceptual)

```
+--------+        1. Login Request        +---------------------------+
|        | ---------------------------->  |                           |
| Browser|                                | Spring Security           |
|        | <----------------------------  | UsernamePasswordAuthFilter|
+--------+        2. Login Success        |                           |
                                          +---------------------------+
                                            |
                                            | 3. loginSuccess()
                                            |    (RememberMeServices)
                                            v
                                   +---------------------------+
                                   | RememberMeServices        |
                                   | (TokenBasedRememberMe...) |
                                   +---------------------------+
                                            |
                                            | 4. Create Remember-Me Token
                                            v
                                   +---------------------------+
                                   | remember-me Cookie        |
                                   | (Persistent, 2 weeks)     |
                                   +---------------------------+
                                            |
                                            v
                                   Stored in Browser
```

---

## Diagram: Remember-Me Authentication on Subsequent Request

```
+--------+        Request without Session        +---------------------------+
|        | ------------------------------------> |                           |
| Browser|   remember-me cookie present          | Spring Security           |
|        |                                       | Filter Chain              |
+--------+                                       +---------------------------+                           |
                                                   |
                                                   | 1. No Authentication
                                                   |    in SecurityContext
                                                   v
                                         +---------------------------+
                                         | RememberMeAuthentication  |
                                         | Filter                    |
                                         +---------------------------+
                                                   |
                                                   | 2. autoLogin()
                                                   |    (RememberMeServices)
                                                   v
                                         +---------------------------+
                                         | RememberMeServices        |
                                         | (validate token)          |
                                         +---------------------------+
                                                   |
                                                   | 3. Load UserDetails
                                                   v
                                         +---------------------------+
                                         | UserDetailsService        |
                                         +---------------------------+
                                                   |
                                                   | 4. Create Authentication
                                                   v
                                         +---------------------------+
                                         | SecurityContextHolder     |
                                         | Authentication restored   |
                                         +---------------------------+
                                                   |
                                                   v
                                         New JSESSIONID issued
```

---

## Diagram: Full Filter-Level Flow (Detailed)

```
Request
  |
  v
+-----------------------------------+
| SecurityContextPersistenceFilter  |
+-----------------------------------+
  |
  v
+-----------------------------------+
| UsernamePasswordAuthentication    |
| Filter                            |
|                                   |
| - loginSuccess()                  |
| - loginFail()                     |
| -> RememberMeServices             |
+-----------------------------------+
  |
  v
+-----------------------------------+
| RememberMeAuthenticationFilter    |
|                                   |
| - autoLogin()                     |
| - Only if no Authentication       |
+-----------------------------------+
  |
  v
+-----------------------------------+
| RememberMeAuthenticationProvider  |
|                                   |
| - Validates token                 |
| - Checks key                      |
+-----------------------------------+
  |
  v
+-----------------------------------+
| SecurityContextHolder             |
| Authentication restored           |
+-----------------------------------+
```

---

## Key Diagram Notes (for Explanation)

* **JSESSIONID**

  * Session-based
  * Expires on logout or inactivity

* **remember-me cookie**

  * Persistent (default: 2 weeks)
  * Survives session expiration
  * Used to recreate authentication

* **RememberMeServices**

  * Called on:

    * `loginSuccess()`
    * `loginFail()`
    * `autoLogin()`

* **TokenBasedRememberMeServices**

  * Uses hashing (SHA-256 by default)
  * Requires `UserDetailsService`
  * Clears cookie on logout

---

## Optional Label for Slides or Exams

> **Remember-Me Flow Summary:**
> When the session expires, Spring Security uses the remember-me cookie, validates it through `RememberMeServices`, reloads the user via `UserDetailsService`, recreates authentication, and issues a new session transparently.

---

