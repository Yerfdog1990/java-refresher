
---

# 🧩 Lesson Notes: Dependency Injection (DI) in Spring

## 1. Introduction

**Dependency Injection (DI)** is a core concept in the **Spring Framework’s Inversion of Control (IoC)** container. It allows objects to **receive their dependencies from an external source** (the Spring container) rather than creating them internally.

In other words, instead of a class controlling its dependencies, the **container manages and injects them** when creating the bean.
This improves **flexibility**, **testability**, and **maintainability** of code.

---

## 2. What Is a Dependency?

A **dependency** is any object that another object requires to function.
For example:

```java
public class ProjectService {
    private ProjectRepository projectRepository = new ProjectRepository();
}
```

Here, `ProjectService` is tightly coupled to `ProjectRepository` — it creates its own dependency.

With **Dependency Injection**, we delegate this responsibility to Spring:

```java
public class ProjectService {
    private ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }
}
```

Now, `ProjectService` no longer creates its dependency — it *receives* it from the container.

---

## 3. Types of Dependency Injection

Spring provides **three main approaches** to inject dependencies:

| Type                      | Description                                         | How It Works                   |
| ------------------------- | --------------------------------------------------- | ------------------------------ |
| **Constructor Injection** | Dependencies are provided via constructor arguments | Best for required dependencies |
| **Setter Injection**      | Dependencies are provided via public setter methods | Best for optional dependencies |
| **Field Injection**       | Dependencies are injected directly into fields      | Convenient, but less testable  |

---

## 4. Constructor-Based Dependency Injection

In **constructor injection**, dependencies are passed through the constructor.

### Example

```java
@Service
public class ProjectServiceImpl implements IProjectService {

    private final IProjectRepository projectRepository;

    // Constructor-based injection
    @Autowired
    public ProjectServiceImpl(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void displayProjects() {
        projectRepository.listProjects();
    }
}
```

**Spring Configuration**

```java
@Configuration
@ComponentScan("com.example.project")
public class AppConfig {}
```

**Repository Layer**

```java
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {
    public void listProjects() {
        System.out.println("Listing all projects...");
    }
}
```

**Main Class**

```java
@SpringBootApplication
public class ProjectApp {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(ProjectApp.class, args);
        ProjectServiceImpl service = context.getBean(ProjectServiceImpl.class);
        service.displayProjects();
    }
}
```

✅ **Key Points**

* Spring automatically injects the `ProjectRepositoryImpl` bean.
* If there’s only **one constructor**, `@Autowired` is optional.
* Use constructor injection for **mandatory** dependencies.

---

## 5. Setter-Based Dependency Injection

In **setter injection**, dependencies are injected through **public setter methods**.

### Example

```java
@Service
public class ProjectServiceImplSetterInjection implements IProjectService {

    private IProjectRepository projectRepository;

    @Autowired
    public void setProjectRepository(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void showProjects() {
        projectRepository.listProjects();
    }
}
```

### Configuration

```java
@Configuration
@ComponentScan("com.example.project")
public class AppConfig {}
```

✅ **Key Points**

* Spring calls the setter method after bean creation.
* Use setter injection for **optional** dependencies.
* Easy to change dependencies later if needed.

---

## 6. Field-Based Dependency Injection

In **field injection**, dependencies are injected directly into fields using `@Autowired`.

### Example

```java
@Service
public class ProjectServiceImplFieldInjection implements IProjectService {

    @Autowired
    private IProjectRepository projectRepository;

    public void displayAllProjects() {
        projectRepository.listProjects();
    }
}
```

✅ **Key Points**

* Simplest syntax but **less recommended**.
* Makes the class **harder to test**, since the dependency is hidden from the constructor.
* Tight coupling with Spring’s container — not suitable for manual instantiation.

---

## 7. Using @Qualifier with Multiple Beans

If multiple beans implement the same interface, Spring cannot decide which one to inject — this causes a **NoUniqueBeanDefinitionException**.

### Example

```java
@Repository("projectRepositoryImpl1")
public class ProjectRepositoryImpl1 implements IProjectRepository {
    public void listProjects() {
        System.out.println("Project Repository 1");
    }
}

@Repository("projectRepositoryImpl2")
public class ProjectRepositoryImpl2 implements IProjectRepository {
    public void listProjects() {
        System.out.println("Project Repository 2");
    }
}
```

### Use @Qualifier to Specify Which Bean to Inject

```java
@Service
public class ProjectServiceImpl implements IProjectService {

    private final IProjectRepository projectRepository;

    @Autowired
    public ProjectServiceImpl(@Qualifier("projectRepositoryImpl2") IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void displayProjects() {
        projectRepository.listProjects();
    }
}
```

✅ **Key Points**

* Use `@Qualifier("beanName")` to specify which bean to inject.
* The default bean name is the **class name starting with a lowercase letter**.

---

## 8. Using @Primary for Default Bean

Instead of using `@Qualifier`, we can mark one bean as **primary**.

```java
@Repository
@Primary
public class ProjectRepositoryImpl2 implements IProjectRepository {
    public void listProjects() {
        System.out.println("Primary Project Repository");
    }
}
```

Now, even if there are multiple implementations, Spring will inject the **primary** one by default.

---

## 9. Advantages and Disadvantages

| Injection Type            | Advantages                                           | Disadvantages                           |
| ------------------------- | ---------------------------------------------------- | --------------------------------------- |
| **Constructor Injection** | Immutable dependencies, easy testing, cleaner design | More verbose                            |
| **Setter Injection**      | Optional dependencies supported, flexible            | Object may be partially constructed     |
| **Field Injection**       | Simple, less boilerplate                             | Hard to test, tightly coupled to Spring |

✅ **Best Practice**

* Use **Constructor Injection** for required dependencies.
* Use **Setter Injection** for optional dependencies.
* Avoid **Field Injection** unless absolutely necessary.

---

## 10. Example: Testing Constructor Injection

### Service Class

```java
@Service
public class ProjectServiceImpl implements IProjectService {
    private final IProjectRepository projectRepository;

    public ProjectServiceImpl(IProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public void saveProject(String name) {
        projectRepository.save(name);
    }
}
```

### Unit Test Without Spring Context

```java
public class ProjectServiceImplTest {

    private ProjectServiceImpl projectService =
        new ProjectServiceImpl(new ProjectRepositoryImpl());

    @Test
    public void givenProject_whenSaved_thenSuccess() {
        projectService.saveProject("Spring Boot Project");
    }
}
```

✅ Constructor injection allows testing without Spring — **decoupled and testable**.

---

## 11. Summary

| Concept                 | Description                                                   |
| ----------------------- | ------------------------------------------------------------- |
| **DI Purpose**          | Externalizes object creation and wiring to the IoC container  |
| **Types**               | Constructor, Setter, Field                                    |
| **Key Annotations**     | `@Autowired`, `@Qualifier`, `@Primary`                        |
| **Best Practice**       | Prefer constructor-based injection for mandatory dependencies |
| **Spring Boot Default** | Uses component scanning and autowiring automatically          |

---
