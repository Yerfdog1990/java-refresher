
---

# 🧠 Lesson Notes: Spring Bean Scopes

---

## 1. Introduction

In the Spring Framework, **bean scope** determines **how long a bean lives**, **how many instances** are created, and **where** it is visible or accessible within an application context.

When we define a bean, Spring treats it as a **blueprint** for creating actual objects. The **scope** of the bean tells Spring **how to manage** those object instances.

---

### 🧩 Why Scopes Matter

* Controls **lifecycle** of beans.
* Helps manage **memory and performance**.
* Determines **how dependencies are shared** among components.
* Enables **different behavior in web vs non-web applications**.

---

### 🌍 Spring’s Built-in Bean Scopes

| Scope           | Description                                        | Available In     |
| --------------- | -------------------------------------------------- | ---------------- |
| **singleton**   | One shared instance per Spring container (default) | All applications |
| **prototype**   | A new instance for each request                    | All applications |
| **request**     | One instance per HTTP request                      | Web apps only    |
| **session**     | One instance per HTTP session                      | Web apps only    |
| **application** | One instance per `ServletContext`                  | Web apps only    |
| **websocket**   | One instance per WebSocket session                 | Web apps only    |

---

## 2. Singleton Scope (Default)

### 📘 Concept

A **singleton bean** means **only one instance** of that bean is created by the Spring container.
Any request for that bean will return the same instance.

---

### ✅ Example Using Annotations

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

@Configuration
public class AppConfig {

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Person personSingleton() {
        return new Person();
    }
}

public class Person {
    private String name;
    // Getters and setters
}
```

### 🧪 Demonstration

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

Person personA = context.getBean(Person.class);
Person personB = context.getBean(Person.class);

personA.setName("John Smith");
System.out.println(personB.getName()); // Output: John Smith
```

✅ Both references point to the **same object**, meaning any change is reflected everywhere.

---

### 🧾 Equivalent XML Configuration

```xml
<bean id="personSingleton" class="com.example.Person" scope="singleton" />
```

---

## 3. Prototype Scope

### 📘 Concept

A **prototype bean** means **Spring creates a new instance** of the bean **every time** it’s requested.
Each caller gets its own independent copy.

---

### ✅ Example Using Annotations

```java
@Bean
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public Person personPrototype() {
    return new Person();
}
```

### 🧪 Demonstration

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

Person person1 = context.getBean(Person.class);
Person person2 = context.getBean(Person.class);

person1.setName("Alice");
person2.setName("Bob");

System.out.println(person1.getName()); // Output: Alice
System.out.println(person2.getName()); // Output: Bob
```

✅ Each object is **unique**, independent, and **not shared**.

---

### 🧾 Equivalent XML Configuration

```xml
<bean id="personPrototype" class="com.example.Person" scope="prototype" />
```

---

## 4. Web-Aware Scopes

These scopes are available **only in web applications** using Spring MVC or WebFlux.

### 🧱 Example Bean

```java
public class HelloMessageGenerator {
    private String message;
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
```

---

### 4.1. Request Scope

#### 📘 Concept

Creates a **new bean instance for every HTTP request**.
When the request completes, the bean is discarded.

#### ✅ Example

```java
@Bean
@RequestScope
public HelloMessageGenerator requestScopedBean() {
    return new HelloMessageGenerator();
}
```

#### 🧪 Controller Example

```java
@Controller
public class ScopeController {

    @Autowired
    private HelloMessageGenerator requestScopedBean;

    @GetMapping("/scopes/request")
    public String showMessage(Model model) {
        model.addAttribute("previous", requestScopedBean.getMessage());
        requestScopedBean.setMessage("Hello from request scope!");
        model.addAttribute("current", requestScopedBean.getMessage());
        return "scopesExample";
    }
}
```

✅ A new `HelloMessageGenerator` is created **each time** `/scopes/request` is accessed.

---

### 4.2. Session Scope

#### 📘 Concept

Creates **one bean instance per HTTP session**.
The same user (session) reuses the same bean instance.

#### ✅ Example

```java
@Bean
@SessionScope
public HelloMessageGenerator sessionScopedBean() {
    return new HelloMessageGenerator();
}
```

#### 🧪 Controller Example

```java
@Controller
public class ScopeController {

    @Autowired
    private HelloMessageGenerator sessionScopedBean;

    @GetMapping("/scopes/session")
    public String showMessage(Model model) {
        model.addAttribute("previous", sessionScopedBean.getMessage());
        sessionScopedBean.setMessage("Session active: " + LocalTime.now());
        model.addAttribute("current", sessionScopedBean.getMessage());
        return "scopesExample";
    }
}
```

✅ The message persists across requests in the same session,
but resets once the session ends.

---

### 4.3. Application Scope

#### 📘 Concept

Creates **one bean for the entire web application**, shared across all users and sessions.

#### ✅ Example

```java
@Bean
@ApplicationScope
public HelloMessageGenerator applicationScopedBean() {
    return new HelloMessageGenerator();
}
```

#### 🧪 Controller Example

```java
@Controller
public class ScopeController {

    @Autowired
    private HelloMessageGenerator applicationScopedBean;

    @GetMapping("/scopes/application")
    public String showMessage(Model model) {
        model.addAttribute("previous", applicationScopedBean.getMessage());
        applicationScopedBean.setMessage("Shared for all users!");
        model.addAttribute("current", applicationScopedBean.getMessage());
        return "scopesExample";
    }
}
```

✅ This message is visible to **all users and sessions** within the web application.

---

### 4.4. WebSocket Scope

#### 📘 Concept

Creates one bean per **WebSocket session**.
Each client connected via WebSocket has its own bean instance.

#### ✅ Example

```java
@Bean
@Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)
public HelloMessageGenerator websocketScopedBean() {
    return new HelloMessageGenerator();
}
```

✅ This scope is mainly used in **real-time communication** apps (e.g., chat apps).

---

## 5. Custom Bean Scopes

Spring also allows **custom scopes** (e.g., per-thread, per-tenant).

To create one, implement the `Scope` interface and register it:

```java
Scope threadScope = new SimpleThreadScope();
beanFactory.registerScope("thread", threadScope);
```

Then use it:

```xml
<bean id="taskBean" class="com.example.Task" scope="thread" />
```

✅ This creates **one instance per thread**.

---

## 6. Quick Comparison Table

| Scope           | Lifecycle             | Created When          | Shared Between  | Typical Use             |
| --------------- | --------------------- | --------------------- | --------------- | ----------------------- |
| **singleton**   | App-wide              | Container startup     | All requests    | Stateless services      |
| **prototype**   | Per request           | Bean request          | None            | Stateful beans          |
| **request**     | Per HTTP request      | Incoming HTTP request | None            | Request data            |
| **session**     | Per HTTP session      | New session           | Same user       | User data               |
| **application** | App lifetime          | App startup           | All users       | Global resources        |
| **websocket**   | Per WebSocket session | New WebSocket         | Same connection | Real-time communication |

---

## 7. 🧩 Summary

* Spring provides **six built-in scopes** for managing bean lifecycle and visibility.
* **Singleton** is the default and most common scope.
* **Prototype** provides a new instance for every injection.
* **Request, Session, Application, and WebSocket** apply to **web applications** only.
* Custom scopes can be created for **specialized use cases**.
* Using the right scope ensures **efficient memory use**, **correct dependency behavior**, and **thread safety**.

---
