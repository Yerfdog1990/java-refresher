
---

# **Lesson Notes: Spring AspectJ**

## **1. Introduction**

Aspect-Oriented Programming (AOP) is a powerful programming paradigm that complements Object-Oriented Programming (OOP). While OOP focuses on encapsulating data and behavior into objects, AOP focuses on separating **cross-cutting concerns** — functionalities like logging, security, and transaction management that affect multiple modules but do not belong to the core business logic.

**AspectJ** is a robust and complete implementation of AOP for Java. It extends the Java language and compiler to support aspect-oriented features, allowing developers to **weave** aspects into code at different stages: **compile-time**, **post-compile**, or **load-time**.

---

## **2. Overview of AspectJ**

AspectJ increases modularity by isolating cross-cutting concerns and weaving them into existing code **without modifying the code itself**.

* **Aspect**: A module that encapsulates cross-cutting concerns.
* **Join Point**: A point in program execution, such as method call, field access, or constructor execution.
* **Pointcut**: A set of join points where advice should be executed.
* **Advice**: The action to be taken at a join point (e.g., before, after, around).
* **Weaving**: The process of linking aspects with target code.

AspectJ provides weaving at:

* **Compile-time**
* **Post-compile (binary)**
* **Load-time**

---

## **3. Maven Dependencies**

To use AspectJ, include the following dependencies in your `pom.xml`:

### **3.1 AspectJ Runtime**

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjrt</artifactId>
    <version>1.9.20.1</version>
</dependency>
```

### **3.2 AspectJ Weaver**

```xml
<dependency>
    <groupId>org.aspectj</groupId>
    <artifactId>aspectjweaver</artifactId>
    <version>1.9.20.1</version>
</dependency>
```

---

## **4. Creating an Aspect**

Let’s demonstrate AspectJ’s basic functionality using a simple `Account` example.

### **Account Class**

```java
public class Account {
    int balance = 20;

    public boolean withdraw(int amount) {
        if (balance < amount) {
            return false;
        }
        balance -= amount;
        return true;
    }
}
```

### **Aspect File – AccountAspect.aj**

AspectJ uses `.aj` files for aspect definitions.

```java
public aspect AccountAspect {
    final int MIN_BALANCE = 10;

    pointcut callWithdraw(int amount, Account account):
        call(boolean Account.withdraw(int)) && args(amount) && target(account);

    before(int amount, Account account): callWithdraw(amount, account) {
        System.out.println("Balance before withdrawal: " + account.balance);
        System.out.println("Withdraw amount: " + amount);
    }

    boolean around(int amount, Account account): callWithdraw(amount, account) {
        if (account.balance < amount) {
            System.out.println("Withdrawal Rejected!");
            return false;
        }
        return proceed(amount, account);
    }

    after(int amount, Account account): callWithdraw(amount, account) {
        System.out.println("Balance after withdrawal: " + account.balance);
    }
}
```

### **Concept Breakdown**

| Concept        | Description                                                     |
| -------------- | --------------------------------------------------------------- |
| **Aspect**     | `AccountAspect` encapsulates cross-cutting logic.               |
| **Join Point** | Execution of `withdraw()` method.                               |
| **Pointcut**   | Expression selecting join points.                               |
| **Advice**     | Actions defined `before`, `after`, and `around` the join point. |

---

## **5. Types of Weaving**

### **5.1 Compile-Time Weaving**

AspectJ compiler (`ajc`) weaves aspects during compilation.

Add AspectJ Maven plugin:

```xml
<plugin>
    <groupId>org.codehaus.mojo</groupId>
    <artifactId>aspectj-maven-plugin</artifactId>
    <version>1.14.0</version>
    <configuration>
        <source>1.8</source>
        <target>1.8</target>
        <showWeaveInfo>true</showWeaveInfo>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>compile</goal>
                <goal>test-compile</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### **Unit Test**

```java
public class AccountTest {
    private Account account;

    @Before
    public void setUp() {
        account = new Account();
    }

    @Test
    public void whenWithdrawValid_thenSuccess() {
        assertTrue(account.withdraw(5));
    }

    @Test
    public void whenWithdrawTooMuch_thenFail() {
        assertFalse(account.withdraw(100));
    }
}
```

---

### **5.2 Post-Compile Weaving**

Used to weave **already compiled class files** or JARs.

```xml
<configuration>
    <weaveDependencies>
        <weaveDependency>
            <groupId>org.sample</groupId>
            <artifactId>example-lib</artifactId>
        </weaveDependency>
    </weaveDependencies>
</configuration>
```

---

### **5.3 Load-Time Weaving**

Weaving happens when classes are loaded into the JVM.

#### **Enable Weaving via Agent**

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <configuration>
        <argLine>
            -javaagent:"${settings.localRepository}"/org/aspectj/aspectjweaver/1.9.20.1/aspectjweaver-1.9.20.1.jar
        </argLine>
    </configuration>
</plugin>
```

#### **aop.xml Configuration**

Located under `META-INF/aop.xml`:

```xml
<aspectj>
    <aspects>
        <aspect name="com.example.aspect.AccountAspect"/>
    </aspects>
    <weaver options="-verbose -showWeaveInfo">
        <include within="com.example.*"/>
    </weaver>
</aspectj>
```

---

## **6. Annotation-Based Aspect (Spring Integration)**

AspectJ supports annotation-style aspects, often used in Spring.

### **Custom Annotation**

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Secured {
    boolean isLocked() default false;
}
```

### **Target Class**

```java
public class SecuredMethod {

    @Secured(isLocked = true)
    public void lockedMethod() {
        System.out.println("Locked method execution.");
    }

    @Secured(isLocked = false)
    public void unlockedMethod() {
        System.out.println("Unlocked method execution.");
    }
}
```

### **Aspect Using Annotations**

```java
@Aspect
public class SecuredMethodAspect {

    @Pointcut("@annotation(secured)")
    public void callAt(Secured secured) {}

    @Around("callAt(secured)")
    public Object around(ProceedingJoinPoint pjp, Secured secured) throws Throwable {
        if (secured.isLocked()) {
            System.out.println(pjp.getSignature() + " is locked");
            return null;
        }
        return pjp.proceed();
    }
}
```

---

## **7. Comparison: Spring AOP vs AspectJ**

| Feature                  | **Spring AOP**                     | **AspectJ**                                          |
| ------------------------ | ---------------------------------- | ---------------------------------------------------- |
| **Implementation**       | Pure Java (runtime proxies)        | Java extensions                                      |
| **Weaving Type**         | Runtime (proxy-based)              | Compile-time, post-compile, load-time                |
| **Scope**                | Only Spring-managed beans          | All Java objects                                     |
| **Joinpoints Supported** | Method execution only              | All joinpoints (methods, constructors, fields, etc.) |
| **Performance**          | Slower (runtime weaving)           | Faster (compile-time weaving)                        |
| **Ease of Use**          | Simpler and integrated with Spring | Complex setup, more powerful                         |
| **Dependencies**         | Spring AOP libraries               | AspectJ runtime + weaver                             |
| **Proxy Mechanism**      | JDK Dynamic or CGLIB proxies       | No proxying (direct weaving)                         |

---

## **8. Choosing Between Spring AOP and AspectJ**

| **Scenario**                                     | **Recommended Tool**                     |
| ------------------------------------------------ | ---------------------------------------- |
| Application built entirely with Spring           | **Spring AOP**                           |
| Need for fine-grained control and all joinpoints | **AspectJ**                              |
| High-performance applications with many aspects  | **AspectJ**                              |
| Simplicity and quick setup                       | **Spring AOP**                           |
| Combination of both worlds                       | Use **Spring AOP + AspectJ** integration |

---

## **9. Conclusion**

AspectJ provides a **complete and powerful AOP framework** capable of weaving aspects at any stage of the application lifecycle. It supports both traditional `.aj` syntax and annotation-based styles, making it flexible for integration with frameworks like Spring.

While **Spring AOP** offers simplicity and ease of use within the Spring ecosystem, **AspectJ** is the choice for advanced, high-performance, and non-Spring-based applications requiring complete AOP support.

---
