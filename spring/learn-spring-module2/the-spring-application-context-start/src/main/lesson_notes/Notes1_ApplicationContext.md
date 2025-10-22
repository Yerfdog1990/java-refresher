
---

# 🌱 Lesson Notes: The Spring Application Context

---

## 2.1. What is the Application Context

The **ApplicationContext** is a *core part* of the Spring Framework.
It represents the **Inversion of Control (IoC)** container responsible for **instantiating**, **configuring**, and **assembling beans**.

The container gets its instructions on *what objects to instantiate, configure, and assemble* by reading configuration metadata — either from **XML**, **Java annotations**, or **Java-based configuration**.

In simpler terms:

* **ApplicationContext** = "Brain of Spring" that manages the lifecycle and dependencies of beans.

Example:

```java
ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
MyService service = context.getBean(MyService.class);
```

Here, Spring loads the configuration, creates beans, injects dependencies, and wires everything automatically.

---

## 2.2. Obtaining a Reference to the Context

You can reference the current running ApplicationContext in **two ways**:

### ✅ 1. Using `@Autowired`

This automatically injects the running context into your bean.

```java
@Autowired
private ApplicationContext context;
```

### ⚙️ 2. Implementing `ApplicationContextAware`

You can also gain access by implementing the `ApplicationContextAware` interface.
It provides the `setApplicationContext()` method, which is called automatically when the bean is initialized.

Example:

```java
@Service
public class ProjectServiceImpl implements IProjectService, ApplicationContextAware {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        LOG.info("CONTEXT WITH ID '{}' SET", applicationContext.getId());
    }
}
```

When the app runs, Spring logs:

```
CONTEXT WITH ID 'application' SET
```

Spring Boot automatically creates this context, usually with the id `'application'`.

---

## 2.3. Creating a New Context

Spring provides several **ApplicationContext implementations**, each used depending on your configuration style:

| Configuration Style    | Class Used                                                          |
| ---------------------- | ------------------------------------------------------------------- |
| XML-based              | `ClassPathXmlApplicationContext`, `FileSystemXmlApplicationContext` |
| Groovy DSL             | `GenericGroovyApplicationContext`                                   |
| Java-based             | `AnnotationConfigApplicationContext`                                |
| Flexible, multi-format | `GenericApplicationContext`                                         |

### Example — Java-based Context

```java
public class LsApp {
    private static final Logger LOG = LoggerFactory.getLogger(LsApp.class);

    public static void main(String[] args) {
        SpringApplication.run(LsApp.class, args);

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        LOG.info("context created with id {}", ctx.getId());
    }
}
```

When run, the log confirms that the context was successfully created.

📝 **Note:**
This new context is **not** the same as the one created automatically by Spring Boot.
It’s an entirely separate container you created programmatically.

---

## 2.4. Adding and Retrieving Beans from the Context

Once you have a context, you can **retrieve beans** using `getBean()`.

But remember — if you create a context manually, you must tell it *where to find beans*.

### ✅ Example: Scanning for Beans

```java
AnnotationConfigApplicationContext ctx =
    new AnnotationConfigApplicationContext("com.example.repository");
```

This tells Spring to scan the specified package for `@Component`, `@Service`, or `@Repository` annotated classes.

Alternatively, you can use:

```java
ctx.scan("com.example.service");
ctx.refresh(); // refresh after scanning
```

Then retrieve a bean:

```java
IProjectService projectService = ctx.getBean("projectServiceImpl", IProjectService.class);
LOG.info("{}", projectService.findById(1L));
```

---

## 2.5. Bean Lifecycle in the Application Context

Spring beans have lifecycle hooks that can be managed using annotations like:

* `@PostConstruct` — runs *after* bean creation.
* `@PreDestroy` — runs *before* the context is destroyed.

### Example:

```java
@Service
public class ProjectServiceImpl {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @PostConstruct
    public void created() {
        LOG.info("POST CONSTRUCT in ProjectServiceImpl");
    }

    @PreDestroy
    public void onDestroy() {
        LOG.info("PRE DESTROY in ProjectServiceImpl");
    }
}
```

When the app runs, Spring executes `@PostConstruct` after initialization,
and `@PreDestroy` when the context shuts down.

---

## 2.6. Closing the Context

To close the context and release resources:

```java
LOG.info("Context active before close: {}", ctx.isActive());
ctx.close();
LOG.info("Context active after close: {}", ctx.isActive());
```

Output:

```
Context active before close: true
Context active after close: false
```

---

# 🧩 2.7. Understanding `AnnotationConfigApplicationContext`

This class is one of the **most common ApplicationContext implementations** when using Java-based configuration.

It can:

* Accept `@Configuration` classes directly.
* Scan packages for `@Component` classes.
* Be configured **automatically or manually**.

---

## 🧭 1️⃣ Two Ways to Initialize It

| Code                                                                                      | Meaning                                          | What It Does                                                                                 |
| ----------------------------------------------------------------------------------------- | ------------------------------------------------ | -------------------------------------------------------------------------------------------- |
| ✅ `ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);` | **Creates and refreshes immediately**            | Loads `AppConfig`, registers beans, refreshes the container, and wires everything instantly. |
| ⚙️ `AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();`   | **Creates an empty context (not yet refreshed)** | You must **manually register** configuration classes and **call `refresh()`** before use.    |

---

## 🥇 Case 1 — One-Liner with `AppConfig.class`

```java
ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
```

### What Happens Internally

1. Context is created.
2. Spring registers `AppConfig`.
3. Beans in `AppConfig` are discovered.
4. The context is refreshed.
5. The container is ready.

### Example

```java
@Configuration
public class AppConfig {
    @Bean
    public MyService myService() {
        return new MyService();
    }
}

public class MainApp {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        MyService service = context.getBean(MyService.class);
        service.greet();
    }
}

class MyService {
    public void greet() {
        System.out.println("Hello from MyService!");
    }
}
```

✅ Output:

```
Hello from MyService!
```

---

## 🥈 Case 2 — Empty Constructor (Manual Setup)

```java
AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
ctx.register(AppConfig.class);
ctx.refresh();
MyService service = ctx.getBean(MyService.class);
service.greet();
ctx.close();
```

✅ Output:

```
Hello from MyService!
```

### When to Use Manual Setup

* You want to register multiple configs dynamically.
* You want full control over when the context refreshes.
* You’re writing a framework that builds contexts programmatically.

---

## 🔍 Key Difference Visually

```
Option 1: Automatic Context
──────────────────────────────
AnnotationConfigApplicationContext(AppConfig.class)
     ↓
[Spring Container Initialized + Beans Loaded + Ready to Use]


Option 2: Manual Context
──────────────────────────────
AnnotationConfigApplicationContext()
     ↓ register(AppConfig.class)
     ↓ scan("com.example")
     ↓ refresh()
[Spring Container Initialized + Beans Loaded + Ready to Use]
```

---

## 🧠 Summary Table

| Feature                              | `new AnnotationConfigApplicationContext(AppConfig.class)` | `new AnnotationConfigApplicationContext()` |
| ------------------------------------ | --------------------------------------------------------- | ------------------------------------------ |
| Registers config class automatically | ✅ Yes                                                     | ❌ No                                       |
| Automatically refreshes the context  | ✅ Yes                                                     | ❌ No — must call `refresh()`               |
| Ideal for                            | Simple apps                                               | Dynamic / programmatic setups              |

---

## 💡 Bonus Tip

This is similar to the XML-based equivalent:

```java
// Automatic
new ClassPathXmlApplicationContext("beans.xml");

// Manual
ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext();
ctx.setConfigLocation("beans.xml");
ctx.refresh();
```

The first is **automatic**, while the second gives you **control**.

---

### ✅ Final Takeaway

* `ApplicationContext` = the **Spring container**.
* `AnnotationConfigApplicationContext` = the **Java-based implementation**.
* Use the **automatic constructor** for quick bootstrapping.
* Use the **manual constructor** for dynamic configuration or advanced setups.

---
