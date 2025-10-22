
---
# **Lesson Notes: Spring Container Extension Points**
---

## **1. Introduction**

The **Spring Container** is responsible for managing the complete lifecycle of beans — from creation and initialization to destruction. However, Spring also provides **extension points** that let developers **intercept and modify this lifecycle**, allowing custom logic to run before or after beans are instantiated or configured.

These extension points include:

1. **BeanPostProcessor** – Intercepts bean creation (before/after initialization).
2. **BeanFactoryPostProcessor** – Intercepts bean **definitions** before instantiation.
3. **FactoryBean** – Customizes bean creation logic using factory classes.

We’ll cover how each works with **examples**.

---

## **2. The Spring Container Extension Points**

### **2.1 Overview**

Spring provides hooks (interfaces) to extend the context initialization process:

* **BeanPostProcessor** → Customizes beans *after* they are created.
* **BeanFactoryPostProcessor** → Customizes bean *definitions* before beans are created.
* **FactoryBean** → Allows creation of custom factory objects.

You can think of these as “interception points” in the Spring lifecycle:

| Phase                     | Interface                    | Purpose                                       |
| ------------------------- | ---------------------------- | --------------------------------------------- |
| Before Bean Instantiation | **BeanFactoryPostProcessor** | Modify bean metadata before instantiation     |
| After Bean Instantiation  | **BeanPostProcessor**        | Modify or wrap the created bean               |
| Custom Bean Creation      | **FactoryBean**              | Create complex beans through custom factories |

---

## **3. BeanPostProcessor**

### **3.1 Concept**

A **BeanPostProcessor** allows you to run custom code **before and after** the initialization of each bean managed by Spring.

When Spring creates a bean, it goes through several steps:

1. Instantiate the bean.
2. Set dependencies (via DI).
3. Call any initialization methods.
4. **Call post-processors** before and after initialization.

You can use this to modify bean properties or log bean creation.

---

### **3.2 Example: Custom BeanPostProcessor**

Let’s create a simple example that logs every bean being initialized.

#### **Step 1: Define the Processor**

```java
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(MyBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        LOG.info("Before initializing bean: {}", beanName);
        return bean; // Return the same or modified bean
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        LOG.info("After initializing bean: {}", beanName);
        return bean;
    }

    @Override
    public int getOrder() {
        return 1; // Lower number means higher priority
    }
}
```

#### **Step 2: Observe Logs**

When you run your Spring app, you’ll see:

```
Before initializing bean: beanA
After initializing bean: beanA
Before initializing bean: beanB
After initializing bean: beanB
```

---

### **3.3 Multiple BeanPostProcessors**

You can define multiple post-processors and control their execution order using the **Ordered** interface.

```java
@Component
public class CustomBeanPostProcessor implements BeanPostProcessor, Ordered {

    private static final Logger LOG = LoggerFactory.getLogger(CustomBeanPostProcessor.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        LOG.info("Custom Processor BEFORE init: {}", beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        LOG.info("Custom Processor AFTER init: {}", beanName);
        return bean;
    }

    @Override
    public int getOrder() {
        return 2;
    }
}
```

✅ **Execution Order:**
`MyBeanPostProcessor (order 1)` → runs first
`CustomBeanPostProcessor (order 2)` → runs next

---

## **4. BeanFactoryPostProcessor**

### **4.1 Concept**

The **BeanFactoryPostProcessor** works at an earlier phase than the BeanPostProcessor.

It allows you to **modify the configuration metadata** of a bean **before Spring creates it**.
This means you can change property values, scope, or dependencies programmatically before any bean instance exists.

---

### **4.2 Example: Custom BeanFactoryPostProcessor**

#### **Step 1: Create a Simple Bean**

```java
public class BeanA {
    private String foo;

    public String getFoo() { return foo; }
    public void setFoo(String foo) { this.foo = foo; }

    @PostConstruct
    public void init() {
        System.out.println("BeanA initialized with foo = " + foo);
    }
}
```

#### **Step 2: Define Bean in Configuration**

```java
@Configuration
public class AppConfig {
    @Bean
    public BeanA beanA() {
        return new BeanA();
    }
}
```

#### **Step 3: Create the Factory Post Processor**

```java
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition bd = beanFactory.getBeanDefinition("beanA");
        bd.getPropertyValues().add("foo", "bar");
        System.out.println("BeanFactoryPostProcessor: Modified beanA.foo to 'bar'");
    }
}
```

✅ **Output:**

```
BeanFactoryPostProcessor: Modified beanA.foo to 'bar'
BeanA initialized with foo = bar
```

🧠 **Key Idea:**

* `BeanFactoryPostProcessor` changes **bean definitions (metadata)**, not bean instances.
* It runs **before any beans are created**.

---

## **5. FactoryBean**

### **5.1 Concept**

A **FactoryBean** lets you plug in your own custom logic for bean creation.
Instead of defining complex initialization directly in a bean, you can delegate that logic to a factory class.

When you call `getBean("myFactoryBean")`, Spring actually calls the factory’s `getObject()` method and returns its result.

---

### **5.2 Example: Custom FactoryBean**

#### **Step 1: Create Product Bean**

```java
public class Product {
    private String name;
    public Product(String name) { this.name = name; }
    @Override
    public String toString() { return "Product{name='" + name + "'}"; }
}
```

#### **Step 2: Create FactoryBean**

```java
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

@Component("productFactory")
public class ProductFactory implements FactoryBean<Product> {

    @Override
    public Product getObject() {
        System.out.println("FactoryBean: Creating a new Product instance...");
        return new Product("Laptop");
    }

    @Override
    public Class<?> getObjectType() {
        return Product.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
```

#### **Step 3: Access Factory Output**

```java
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class, args);

        // Get the product created by the factory
        Product product = context.getBean("productFactory", Product.class);
        System.out.println(product);

        // Get the factory itself
        ProductFactory factory = (ProductFactory) context.getBean("&productFactory");
        System.out.println("Factory itself: " + factory.getClass().getSimpleName());
    }
}
```

✅ **Output:**

```
FactoryBean: Creating a new Product instance...
Product{name='Laptop'}
Factory itself: ProductFactory
```

🧩 **Note:**

* `getBean("productFactory")` → returns the **Product** created by the factory.
* `getBean("&productFactory")` → returns the **factory itself**.

---

## **6. Summary: Comparing Spring Extension Points**

| Extension Point              | Purpose                              | Runs On         | Common Use Case                                         |
| ---------------------------- | ------------------------------------ | --------------- | ------------------------------------------------------- |
| **BeanPostProcessor**        | Modify beans after creation          | Bean instance   | Logging, proxy wrapping                                 |
| **BeanFactoryPostProcessor** | Modify bean metadata before creation | Bean definition | Change property values, placeholder replacement         |
| **FactoryBean**              | Custom factory for complex beans     | Factory class   | Custom bean creation logic (e.g., database connections) |

---

## **7. Advanced: Registering PostProcessors Programmatically**

You can also register post-processors **manually** (useful in custom contexts):

```java
ConfigurableApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);
ctx.getBeanFactory().addBeanPostProcessor(new MyBeanPostProcessor());
```

This allows conditional registration and full control over ordering.

---

## **8. Conclusion**

Spring’s container extension points provide **powerful control hooks** into the bean lifecycle.
They allow:

* Developers to **modify, wrap, or replace** beans dynamically.
* Applications to remain **flexible** and **maintainable**, even as configuration needs change.

By mastering these extension points, you gain **deeper insight into Spring’s internals** and learn how to build highly customizable frameworks and middleware on top of Spring itself.

---
