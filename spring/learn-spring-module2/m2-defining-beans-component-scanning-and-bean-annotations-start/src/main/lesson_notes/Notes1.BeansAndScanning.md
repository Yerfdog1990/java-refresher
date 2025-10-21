
---

# **Lesson Notes: Defining Beans, Component Scanning, and Bean Annotations**

---

## **1. Introduction to Spring Beans**

In the **Spring Framework**, a **bean** is an object that is managed by the **Spring IoC (Inversion of Control) container**. These beans form the backbone of a Spring application — they are created, configured, and assembled by the container.

Beans can be defined in several ways:

* Explicitly using the `@Bean` annotation inside a `@Configuration` class
* Implicitly through **component scanning**, where Spring automatically detects and registers components using annotations such as `@Component`, `@Service`, or `@Repository`.

---

## **2. Defining Beans Explicitly Using `@Configuration` and `@Bean`**

The traditional way to define a bean is through a **Java configuration class** using the `@Configuration` and `@Bean` annotations.

### **Example: Defining Beans Explicitly**

```java
package com.example.springconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ExampleBean exampleBean() {
        return new ExampleBean();
    }
}

class ExampleBean {
    public void sayHello() {
        System.out.println("Hello from ExampleBean!");
    }
}
```

### **How It Works**

* The `@Configuration` annotation tells Spring that this class defines one or more bean methods.
* The `@Bean` annotation marks a method as a bean producer. Spring calls this method, registers the returned object as a bean, and manages its lifecycle.

### **Running the Example**

```java
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class BeanExampleMain {
    public static void main(String[] args) {
        ApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        ExampleBean bean = context.getBean(ExampleBean.class);
        bean.sayHello();
    }
}
```

**Output:**

```
Hello from ExampleBean!
```

This method is **explicit**, giving you full control over bean creation and configuration.

---

## **3. Component Scanning in Spring**

Instead of defining every bean manually, Spring provides a mechanism called **component scanning**, which **automatically detects and registers beans** based on annotations.

This is done using the `@ComponentScan` annotation.

---

### **3.1 What is Component Scanning?**

**Component Scanning** tells Spring **where to look** for classes annotated with special **stereotype annotations** like:

* `@Component`
* `@Service`
* `@Repository`
* `@Controller`

Spring automatically registers these classes as beans in the application context.

---

### **3.2 Example: Basic Component Scanning**

Let’s say we have the following package structure:

```
com.example.componentscan
│
├── SpringComponentScanApp.java
├── animals/
│     ├── Cat.java
│     └── Dog.java
└── flowers/
      └── Rose.java
```

#### **Configuration Class**

```java
package com.example.componentscan;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Configuration
@ComponentScan
public class SpringComponentScanApp {

    public static void main(String[] args) {
        ApplicationContext context =
                new AnnotationConfigApplicationContext(SpringComponentScanApp.class);

        for (String beanName : context.getBeanDefinitionNames()) {
            System.out.println(beanName);
        }
    }
}
```

#### **Component Classes**

```java
package com.example.componentscan.animals;

import org.springframework.stereotype.Component;

@Component
public class Cat {
    public Cat() {
        System.out.println("Cat bean created!");
    }
}
```

```java
package com.example.componentscan.animals;

import org.springframework.stereotype.Component;

@Component
public class Dog {
    public Dog() {
        System.out.println("Dog bean created!");
    }
}
```

```java
package com.example.componentscan.flowers;

import org.springframework.stereotype.Component;

@Component
public class Rose {
    public Rose() {
        System.out.println("Rose bean created!");
    }
}
```

#### **Expected Output**

```
Cat bean created!
Dog bean created!
Rose bean created!
```

Spring automatically scans the **current package and its sub-packages**, finds `@Component` annotations, and creates bean instances.

---

### **3.3 Customizing Scanning Paths**

We can specify which packages Spring should scan using the `basePackages` attribute.

#### **Example:**

```java
@Configuration
@ComponentScan(basePackages = {"com.example.componentscan.animals"})
public class SpringComponentScanApp {
    // Only Cat and Dog will be scanned; Rose will be ignored.
}
```

**Output:**

```
Cat bean created!
Dog bean created!
```

You can specify multiple packages like this:

```java
@ComponentScan(basePackages = {"com.example.animals", "com.example.flowers"})
```

or even separate them with commas or semicolons.

---

### **3.4 Excluding Components from Scanning**

You can also **exclude certain classes** from being registered as beans.

#### **Example:**

```java
@ComponentScan(
  basePackages = "com.example.componentscan",
  excludeFilters = @ComponentScan.Filter(
      type = org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE,
      value = com.example.componentscan.flowers.Rose.class))
@Configuration
public class AppConfig {
}
```

This configuration will exclude the `Rose` component.

---

## **4. Component Scanning in Spring Boot**

In a **Spring Boot application**, you don’t need to explicitly use `@ComponentScan`, because `@SpringBootApplication` already includes it internally.

```java
@SpringBootApplication
public class SpringBootComponentScanApp {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootComponentScanApp.class, args);

        System.out.println("Is cat in ApplicationContext: " + context.containsBean("cat"));
        System.out.println("Is dog in ApplicationContext: " + context.containsBean("dog"));
        System.out.println("Is rose in ApplicationContext: " + context.containsBean("rose"));
    }
}
```

**Output:**

```
Is cat in ApplicationContext: true
Is dog in ApplicationContext: true
Is rose in ApplicationContext: true
```

By default, Spring Boot scans the package of the **main application class** and all its sub-packages.

---

## **5. Stereotype Annotations**

Spring provides **specialized stereotype annotations** built on top of `@Component`.
They help clarify the **role** of each bean in an application.

| Annotation    | Used For          | Description                                            |
| ------------- | ----------------- | ------------------------------------------------------ |
| `@Component`  | Generic           | Marks a simple bean or component                       |
| `@Repository` | Persistence layer | Used for DAO/repository classes; translates exceptions |
| `@Service`    | Business layer    | Marks service classes containing business logic        |
| `@Controller` | Web layer         | Used in MVC controllers                                |

---

### **5.1 Example: Using Different Stereotype Annotations**

```java
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {
    // Data access code
}

@Service
public class ProjectServiceImpl implements IProjectService {
    // Business logic
}
```

**Note:**
Technically, all these annotations are meta-annotations of `@Component`.
For example, the definition of `@Repository` is:

```java
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Repository {
}
```

This means `@Repository` behaves exactly like `@Component`, but with added semantics and exception translation.

---

## **6. Lazy Initialization of Beans**

By default, Spring eagerly initializes **singleton beans** at startup.
To delay the creation until the bean is first requested, use `@Lazy`.

### **Example:**

```java
@Service
@Lazy
public class ProjectServiceImpl implements IProjectService {
    public ProjectServiceImpl() {
        System.out.println("ProjectServiceImpl bean initialized lazily!");
    }
}
```

**Effect:**
This bean will only be created when it’s first injected or requested, not at application startup.

---

## **7. Importing Configuration Classes with `@Import`**

When your project has multiple configuration classes, you can use `@Import` to group them together.

### **Example:**

```java
@Configuration
@ComponentScan(basePackages = {"com.example.persistence"})
public class PersistenceConfig { }

@Configuration
@Import(PersistenceConfig.class)
public class AppConfig { }
```

This approach helps modularize configurations, making them easier to maintain and reuse.

---

## **8. Advantages and Disadvantages of Annotation-Based Configuration**

### **Advantages**

- ✅ Simplifies configuration — less boilerplate.
- ✅ Encourages convention over configuration.
- ✅ Easier to maintain and understand.

### **Disadvantages**

- ⚠️ Configuration becomes **implicit** — harder to see dependencies.
- ⚠️ Mixes **configuration logic with business logic** since annotations appear in class definitions.

---

## **9. Summary**

| Concept            | Description                            | Example                                             |
| ------------------ | -------------------------------------- | --------------------------------------------------- |
| **Bean**           | Managed object in the Spring container | `@Bean`, `@Component`                               |
| **@ComponentScan** | Automatically detects beans            | `@ComponentScan(basePackages="com.example")`        |
| **@Component**     | Generic component marker               | `@Component public class Cat {}`                    |
| **@Service**       | Business logic layer                   | `@Service public class ProjectServiceImpl {}`       |
| **@Repository**    | Persistence layer                      | `@Repository public class ProjectRepositoryImpl {}` |
| **@Import**        | Combine multiple configs               | `@Import(PersistenceConfig.class)`                  |
| **@Lazy**          | Delay bean creation                    | `@Lazy public class MyBean {}`                      |

---

## **10. Key Takeaway**

* **Explicit configuration** (`@Bean`) = More control
* **Component scanning** (`@ComponentScan`) = More automation
* **Use stereotypes** (`@Service`, `@Repository`, `@Controller`) = Clear layer semantics
* **Use @Import** to modularize configurations
* **Use @Lazy** when beans should not load at startup

---
