
---

# 🧩 **Lesson Notes: Circular Dependencies in Spring**

## 1. What Is a Circular Dependency?

A **circular dependency** occurs when **two or more beans depend on each other** directly or indirectly.

For example:

```
Bean A → Bean B → Bean A
```

This means:

* Bean A needs Bean B to be created.
* Bean B also needs Bean A to be created.

Spring cannot decide **which bean to instantiate first**, resulting in a **circular reference error** (`BeanCurrentlyInCreationException`).

It can also occur in longer dependency chains:

```
Bean A → Bean B → Bean C → Bean D → Bean A
```

---

## 2. How Spring Handles Bean Creation

When the Spring container starts:

1. It scans for components or beans defined via annotations (`@Component`, `@Service`, etc.) or configuration.
2. It tries to **resolve dependencies** by injecting beans where required.

Normally, if dependencies are **not circular**, Spring can determine a proper creation order:

```
Bean A → Bean B → Bean C
```

Spring will create **Bean C first**, then **Bean B** (injecting C), then **Bean A** (injecting B).

But with circular dependencies (like A ↔ B), **Spring cannot resolve the creation order** if both are constructor-injected, leading to an exception.

---

## 3. Demonstrating a Circular Dependency Error

### Example: Constructor Injection Problem

```java
package com.example.circulardependency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CircularDependencyA {

    private CircularDependencyB circB;

    @Autowired
    public CircularDependencyA(CircularDependencyB circB) {
        this.circB = circB;
    }
}
```

```java
package com.example.circulardependency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CircularDependencyB {

    private CircularDependencyA circA;

    @Autowired
    public CircularDependencyB(CircularDependencyA circA) {
        this.circA = circA;
    }
}
```

### Configuration Class

```java
package com.example.circulardependency;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example.circulardependency")
public class TestConfig { }
```

### Test

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CircularDependencyIntegrationTest {

    @Test
    public void givenCircularDependency_whenConstructorInjection_thenItFails() {
        // The error occurs at context startup
    }
}
```

**Result:**

```
BeanCurrentlyInCreationException: Requested bean is currently in creation: Is there an unresolvable circular reference?
```

---

## 4. How to Solve Circular Dependencies

If redesigning is not possible, Spring provides **workarounds**.

---

### 🧠 4.1 Using `@Lazy` Annotation

The simplest fix is to **lazy-load one of the beans**.
This tells Spring to inject a **proxy** that delays bean creation until it is first accessed.

```java
@Component
public class CircularDependencyA {

    private CircularDependencyB circB;

    @Autowired
    public CircularDependencyA(@Lazy CircularDependencyB circB) {
        this.circB = circB;
    }
}
```

```java
@Component
public class CircularDependencyB {

    private CircularDependencyA circA;

    @Autowired
    public CircularDependencyB(CircularDependencyA circA) {
        this.circA = circA;
    }
}
```

✅ **Explanation:**

* Spring injects a **proxy** instead of the actual bean.
* The real `CircularDependencyB` bean is created **only when needed**.
* The cycle is effectively broken.

---

### 🧩 4.2 Using Setter Injection (Preferred Method)

Using **setter injection** allows Spring to create beans first and then inject dependencies **after instantiation**.

```java
@Component
public class CircularDependencyA {

    private CircularDependencyB circB;

    @Autowired
    public void setCircB(@Lazy CircularDependencyB circB) {
        this.circB = circB;
    }

    public CircularDependencyB getCircB() {
        return circB;
    }
}
```

```java
@Component
public class CircularDependencyB {

    private CircularDependencyA circA;
    private String message = "Hi!";

    @Autowired
    public void setCircA(@Lazy CircularDependencyA circA) {
        this.circA = circA;
    }

    public String getMessage() {
        return message;
    }
}
```

### Test

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CircularDependencyIntegrationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void givenCircularDependency_whenSetterInjection_thenItWorks() {
        CircularDependencyA circA = context.getBean(CircularDependencyA.class);
        Assert.assertEquals("Hi!", circA.getCircB().getMessage());
    }
}
```

✅ **Explanation:**

* Spring creates both beans first.
* Then injects dependencies afterward using setters.
* The `@Lazy` annotation ensures dependencies are initialized when required.

---

### ⚙️ 4.3 Using `@PostConstruct` Annotation

Another solution is to use **field injection** and manually set the circular reference in a `@PostConstruct` method (executed after bean initialization).

```java
@Component
public class CircularDependencyA {

    @Autowired
    private CircularDependencyB circB;

    @PostConstruct
    public void init() {
        circB.setCircA(this);
    }

    public CircularDependencyB getCircB() {
        return circB;
    }
}
```

```java
@Component
public class CircularDependencyB {

    private CircularDependencyA circA;
    private String message = "Hi!";

    public void setCircA(CircularDependencyA circA) {
        this.circA = circA;
    }

    public String getMessage() {
        return message;
    }
}
```

✅ **Explanation:**

* Spring injects `CircularDependencyB` first.
* After all injections, `@PostConstruct` runs to complete the wiring manually.
* This approach **delays** one part of the circular reference until after bean creation.

---

### 🧰 4.4 Using `ApplicationContextAware` and `InitializingBean`

This is a **manual approach** that leverages the **Spring ApplicationContext** to fetch the other bean after initialization.

```java
@Component
public class CircularDependencyA implements ApplicationContextAware, InitializingBean {

    private CircularDependencyB circB;
    private ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }

    @Override
    public void afterPropertiesSet() {
        this.circB = context.getBean(CircularDependencyB.class);
    }

    public CircularDependencyB getCircB() {
        return circB;
    }
}
```

```java
@Component
public class CircularDependencyB {

    private CircularDependencyA circA;
    private String message = "Hi!";

    @Autowired
    public void setCircA(CircularDependencyA circA) {
        this.circA = circA;
    }

    public String getMessage() {
        return message;
    }
}
```

✅ **Explanation:**

* `ApplicationContextAware` gives direct access to the Spring context.
* `afterPropertiesSet()` (from `InitializingBean`) runs **after dependency injection**, so we can safely fetch the other bean manually.
* This avoids the need for constructor injection.

---

## 5. Summary: Solutions Comparison

| Method                                         | Description                              | Pros                      | Cons                                              |
| ---------------------------------------------- | ---------------------------------------- | ------------------------- | ------------------------------------------------- |
| **@Lazy**                                      | Injects proxy and delays instantiation   | Easy, minimal code change | Lazy initialization can hide errors until runtime |
| **Setter Injection**                           | Injects dependencies after instantiation | Clean, Spring-recommended | Requires setters, not final fields                |
| **@PostConstruct**                             | Manually completes wiring post-init      | Flexible                  | Slightly manual and less declarative              |
| **ApplicationContextAware + InitializingBean** | Manually retrieves dependencies          | Works when others fail    | Invasive and less clean                           |

---

## 6. Best Practice

- 🔹 **First choice:** Redesign your components to avoid circular dependencies.
- 🔹 **If not possible:** Use **setter injection** (preferred Spring approach).
- 🔹 Use **@Lazy** for quick fixes or legacy code.
- 🔹 Use **@PostConstruct** or **ApplicationContextAware** only when absolutely necessary.

---

## 7. Conclusion

Circular dependencies often indicate **tight coupling** or **poor design**.
However, when unavoidable, Spring provides multiple mechanisms to resolve them.

* `@Lazy` → Lazy proxy injection
* Setter injection → Defers dependency wiring
* `@PostConstruct` → Manual post-initialization wiring
* `ApplicationContextAware` + `InitializingBean` → Programmatic retrieval

Each approach helps maintain flexibility and control over bean initialization order.

---

**A flow chart of how each fix changes the dependency cycle**
---