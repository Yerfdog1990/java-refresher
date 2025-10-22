
---

# **Lesson 10: Debugging and Solving Wiring Exceptions**

---

## 🧭 **1. Lesson Overview**

In previous lessons, we learned how Spring manages beans and dependency injection using annotations such as `@Component`, `@Service`, `@Repository`, and `@Bean`. However, when dependencies are not properly configured or beans are mismanaged, **wiring exceptions** occur.

In this lesson, we’ll:

* Understand **three major wiring exceptions**.
* Learn how to **identify** and **resolve** them.
* Explore **real-world examples** of debugging.
* Discuss **best practices** to prevent these issues.

---

## 🧩 **2. Learning Goals**

By the end of this lesson, you should be able to:

* Identify the cause of common bean wiring exceptions in Spring.
* Understand error messages in the console/logs.
* Fix wiring issues related to missing beans, duplicate beans, and circular dependencies.
* Apply debugging strategies to resolve these errors.

---

## ⚙️ **3. Common Wiring Exceptions**

Spring’s IoC container manages object creation and injection. However, during context initialization, the following exceptions commonly occur:

| Exception Type                     | Meaning                                                                        |
| ---------------------------------- | ------------------------------------------------------------------------------ |
| `NoSuchBeanDefinitionException`    | The requested bean could not be found in the Spring context.                   |
| `NoUniqueBeanDefinitionException`  | Multiple beans match a dependency and Spring can’t decide which one to inject. |
| `BeanCurrentlyInCreationException` | A circular dependency exists between two or more beans.                        |

---

## 🔍 **4. Problem 1: Spring Cannot Find the Requested Bean**

### **Scenario**

You attempt to inject a bean that Spring cannot locate in the context.

This often occurs when:

* The bean class lacks `@Component`, `@Service`, or `@Repository`.
* The bean’s package is not scanned by `@ComponentScan`.
* Conditional loading or profiles prevent the bean from loading.

### **Example**

```java
public class ProjectRepositoryImpl implements IProjectRepository {
    // No @Repository annotation
}
```

### **Error Message**

```
Parameter 0 of constructor in com.example.service.ProjectServiceImpl
required a bean of type 'com.example.repository.IProjectRepository' that could not be found.
```

Spring suggests:

> “Consider defining a bean of type 'IProjectRepository' in your configuration.”

### ✅ **Fix #1 – Add a Stereotype Annotation**

```java
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {
    // Now managed by Spring
}
```

### ✅ **Fix #2 – Include the Package in Component Scan**

```java
@SpringBootApplication
@ComponentScan({"com.example.service", "com.example.repository"})
public class AppConfig { }
```

---

## ⚖️ **5. Problem 2: Conflicting Bean Definitions**

### **Scenario**

Spring finds **multiple beans of the same type** and doesn’t know which one to inject.

### **Example**

```java
@Repository
public class ProjectRepositoryImpl implements IProjectRepository { }

@Repository
public class ProjectRepositoryBImpl implements IProjectRepository { }
```

### **Error Message**

```
Parameter 0 of constructor in com.example.service.ProjectServiceImpl
required a single bean, but 2 were found: projectRepositoryImpl, projectRepositoryBImpl
```

### ✅ **Fix #1 – Use @Primary**

Mark one bean as preferred:

```java
@Repository
@Primary
public class ProjectRepositoryImpl implements IProjectRepository { }
```

### ✅ **Fix #2 – Use @Qualifier**

Specify which bean to inject:

```java
@Service
public class ProjectServiceImpl implements IProjectService {

    private final IProjectRepository projectRepository;

    public ProjectServiceImpl(@Qualifier("projectRepositoryBImpl") IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
}
```

---

## 🔁 **6. Problem 3: Circular Dependency**

### **Scenario**

Two beans depend on each other directly or indirectly, causing a **cycle**.

```java
@Service
public class ProjectServiceA {
    private final ProjectServiceB projectServiceB;

    public ProjectServiceA(ProjectServiceB projectServiceB) {
        this.projectServiceB = projectServiceB;
    }
}

@Service
public class ProjectServiceB {
    private final ProjectServiceA projectServiceA;

    public ProjectServiceB(ProjectServiceA projectServiceA) {
        this.projectServiceA = projectServiceA;
    }
}
```

### **Error Message**

```
The dependencies of some beans in the application context form a cycle:
| projectServiceA -> projectServiceB -> projectServiceA
```

---

### ✅ **Fix #1 – Use @Lazy to Break the Cycle**

```java
@Service
public class ProjectServiceA {

    private final ProjectServiceB projectServiceB;

    public ProjectServiceA(@Lazy ProjectServiceB projectServiceB) {
        this.projectServiceB = projectServiceB;
    }
}
```

**Explanation:**
`@Lazy` tells Spring to inject a proxy instead of creating the actual bean immediately.
The dependent bean will be created **only when needed**, breaking the circular reference.

---

### ✅ **Fix #2 – Use Setter Injection**

```java
@Service
public class ProjectServiceA {
    private ProjectServiceB projectServiceB;

    @Autowired
    public void setProjectServiceB(@Lazy ProjectServiceB projectServiceB) {
        this.projectServiceB = projectServiceB;
    }
}
```

Setter injection allows Spring to first create the beans, then inject dependencies afterward, avoiding creation-time conflicts.

---

### ✅ **Fix #3 – Use @PostConstruct**

```java
@Component
public class CircularDependencyA {

    @Autowired
    private CircularDependencyB circB;

    @PostConstruct
    public void init() {
        circB.setCircA(this);
    }
}
```

```java
@Component
public class CircularDependencyB {
    private CircularDependencyA circA;

    public void setCircA(CircularDependencyA circA) {
        this.circA = circA;
    }
}
```

This approach delays full wiring until after the bean has been initialized.

---

### ✅ **Fix #4 – Use ApplicationContextAware**

```java
@Component
public class CircularDependencyA implements ApplicationContextAware, InitializingBean {

    private CircularDependencyB circB;
    private ApplicationContext context;

    @Override
    public void afterPropertiesSet() {
        circB = context.getBean(CircularDependencyB.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        this.context = ctx;
    }
}
```

This manually retrieves the other bean after all properties are set, breaking the creation cycle.

---

## 🧰 **7. Debugging Tips**

1. **Read the full stack trace.**
   Spring provides actionable hints like “Consider defining a bean of type…” or “required a single bean, but found 2.”

2. **Enable debug logging:**

   ```properties
   logging.level.org.springframework=DEBUG
   ```

3. **Check your package scanning.**
   Make sure all necessary packages are included in `@ComponentScan`.

4. **Simplify your architecture.**
   Circular dependencies often indicate **poor design**—try to decouple services logically.

5. **Use Bean Validation Tools.**
   Spring Boot’s **Actuator** endpoint `/beans` can display all registered beans in the context.

---

## 🧪 **8. Summary Table**

| Exception                          | Cause                       | Common Fix                                                              |
| ---------------------------------- | --------------------------- | ----------------------------------------------------------------------- |
| `NoSuchBeanDefinitionException`    | Missing or unscanned bean   | Add `@Component`, `@Service`, or `@Repository`; expand `@ComponentScan` |
| `NoUniqueBeanDefinitionException`  | Multiple beans of same type | Use `@Primary` or `@Qualifier`                                          |
| `BeanCurrentlyInCreationException` | Circular dependency         | Use `@Lazy`, setter injection, or redesign classes                      |

---

## 🧩 **9. Best Practices to Prevent Wiring Errors**

* Always **follow naming conventions** (lowercase first letter for bean names).
* Keep **clear package structures** — services, repositories, and configs separated.
* Avoid **bidirectional dependencies** where possible.
* Use **unit tests** to validate your context:

  ```java
  @SpringBootTest
  class ContextLoadTest {
      @Test
      void contextLoads() { }
  }
  ```
* Rely on **constructor injection** for required dependencies and **setter injection** for optional ones.

---

## 🎯 **10. Conclusion**

Wiring exceptions are common but entirely solvable once you understand Spring’s dependency injection process.

Key takeaways:

* Always ensure your beans are **discovered** and **uniquely identifiable**.
* When in doubt, use `@Qualifier` or `@Primary`.
* Use `@Lazy` or refactor design to fix circular dependencies.
* Debugging wiring issues helps deepen your understanding of **Spring’s IoC container**.

---
