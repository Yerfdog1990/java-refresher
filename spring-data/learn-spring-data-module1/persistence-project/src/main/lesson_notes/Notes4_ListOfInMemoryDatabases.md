Here are your **lesson notes** on the topic **“List of In-Memory Databases”** — written in a clean, structured format suitable for study or teaching material.

---

## **Module: Spring Persistence**

### **Lesson: List of In-Memory Databases**

---

### **1. Overview**

In-memory databases rely on **system memory (RAM)** instead of disk space for data storage.
Because memory access is significantly faster than disk access, these databases offer **high performance** and **low latency**.

However, since their data resides in volatile memory, in-memory databases are ideal for:

* Applications where data **does not need to persist** after the process ends.
* **Testing scenarios**, where the database is created at startup and discarded when the process finishes.

In-memory databases are often used as **embedded databases** — lightweight, self-contained, and easy to configure — making them perfect for development and testing.

In this lesson, we will review the most commonly used in-memory databases in the Java ecosystem and their configurations.

---

### **2. H2 Database**

**H2** is an open-source, lightweight Java database that supports **standard SQL** and can run in **embedded** or **standalone** mode.
It’s known for being **fast** and **easy to integrate**, with a JAR size of about **1.5 MB**.

#### **2.1. Maven Dependency**

```xml
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.1.214</version>
</dependency>
```

#### **2.2. Configuration**

```properties
driverClassName=org.h2.Driver
url=jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1
username=sa
password=sa
```

* `mem:` indicates an **in-memory** database.
* `DB_CLOSE_DELAY=-1` keeps the database alive until the JVM stops.

For Hibernate integration:

```properties
hibernate.dialect=org.hibernate.dialect.H2Dialect
```

> 📘 **Reference:** [h2database.com](https://www.h2database.com)

---

### **3. HSQLDB (HyperSQL Database)**

**HSQLDB** is another open-source Java-based relational database.
It supports **SQL**, **JDBC**, **stored procedures**, and **triggers**, and can run both **in-memory** or **with disk storage**.

#### **3.1. Maven Dependency**

```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.7.1</version>
</dependency>
```

#### **3.2. Configuration**

```properties
driverClassName=org.hsqldb.jdbc.JDBCDriver
url=jdbc:hsqldb:mem:myDb
username=sa
password=sa
```

For Hibernate:

```properties
hibernate.dialect=org.hibernate.dialect.HSQLDialect
```

> 📘 **Reference:** [hsqldb.org](https://hsqldb.org)

---

### **4. Apache Derby Database**

**Apache Derby** is a relational database developed by the **Apache Software Foundation**.
It’s Java-based and supports both **embedded** and **client-server** modes (via Derby Network Server).

#### **4.1. Maven Dependency**

```xml
<dependency>
    <groupId>org.apache.derby</groupId>
    <artifactId>derby</artifactId>
    <version>10.13.1.1</version>
</dependency>
```

#### **4.2. Configuration**

```properties
driverClassName=org.apache.derby.jdbc.EmbeddedDriver
url=jdbc:derby:memory:myDb;create=true
username=sa
password=sa
```

For Hibernate:

```properties
hibernate.dialect=org.hibernate.dialect.DerbyDialect
```

> 📘 **Reference:** [db.apache.org/derby](https://db.apache.org/derby)

---

### **5. SQLite Database**

**SQLite** is a lightweight **C-based SQL database** that runs only in **embedded mode** — either in-memory or saved to a file.
It’s widely used for small-scale applications and can be accessed via **JDBC** in Java.

#### **5.1. Maven Dependency**

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.16.1</version>
</dependency>
```

#### **5.2. Configuration**

```properties
driverClassName=org.sqlite.JDBC
url=jdbc:sqlite:memory:myDb
username=sa
password=sa
```

* Automatically creates the database if it doesn’t exist.
* No official Hibernate dialect yet — a custom dialect must be implemented if required.

> 📘 **Reference:** [sqlite.org](https://www.sqlite.org)

---

### **6. In-Memory Databases in Spring Boot**

Spring Boot provides **auto-configuration** for in-memory databases like **H2**, **HSQLDB**, and **Derby**.

To use one:

1. Add the database dependency to your `pom.xml`.
2. Spring Boot detects it automatically and sets up a **DataSource** and **JPA** configuration.

This feature eliminates the need for manual database setup, making integration seamless and ideal for testing.

---

### **7. Conclusion**

In-memory databases provide a **fast and convenient** solution for testing and lightweight data operations in Java applications.
They enable rapid prototyping and continuous integration testing without the overhead of managing external database servers.

However, remember that:

* They **do not persist data** after the application stops.
* Their behavior may differ slightly from real production databases.

For **testing**, **development**, and **education**, in-memory databases like **H2**, **HSQLDB**, **Derby**, and **SQLite** remain invaluable tools in the Java ecosystem.

---
Database Configuration Examples

Below is a practical reference for using each database as in-memory in Java or Spring Boot applications.

| Feature               | **H2**                                                                                                                                               | **HSQLDB**                                                                                                                                         | **Apache Derby**                                                                                                                                            | **SQLite**                                                                                                                                               |
| --------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Maven Dependency**  | `xml<br><dependency><br>    <groupId>com.h2database</groupId><br>    <artifactId>h2</artifactId><br>    <version>2.1.214</version><br></dependency>` | `xml<br><dependency><br>    <groupId>org.hsqldb</groupId><br>    <artifactId>hsqldb</artifactId><br>    <version>2.7.1</version><br></dependency>` | `xml<br><dependency><br>    <groupId>org.apache.derby</groupId><br>    <artifactId>derby</artifactId><br>    <version>10.13.1.1</version><br></dependency>` | `xml<br><dependency><br>    <groupId>org.xerial</groupId><br>    <artifactId>sqlite-jdbc</artifactId><br>    <version>3.16.1</version><br></dependency>` |
| **Driver Class Name** | `org.h2.Driver`                                                                                                                                      | `org.hsqldb.jdbc.JDBCDriver`                                                                                                                       | `org.apache.derby.jdbc.EmbeddedDriver`                                                                                                                      | `org.sqlite.JDBC`                                                                                                                                        |
| **URL Format**        | `jdbc:h2:mem:myDb;DB_CLOSE_DELAY=-1`                                                                                                                 | `jdbc:hsqldb:mem:myDb`                                                                                                                             | `jdbc:derby:memory:myDb;create=true`                                                                                                                        | `jdbc:sqlite:memory:myDb`                                                                                                                                |
| **Username**          | `sa`                                                                                                                                                 | `sa`                                                                                                                                               | `sa`                                                                                                                                                        | `sa`                                                                                                                                                     |
| **Password**          | `sa`                                                                                                                                                 | `sa`                                                                                                                                               | `sa`                                                                                                                                                        | `sa`                                                                                                                                                     |
| **Hibernate Dialect** | `org.hibernate.dialect.H2Dialect`                                                                                                                    | `org.hibernate.dialect.HSQLDialect`                                                                                                                | `org.hibernate.dialect.DerbyDialect`                                                                                                                        | *(Custom dialect needed)*                                                                                                                                |
| **Persistence**       | Until JVM stops (`DB_CLOSE_DELAY=-1`)                                                                                                                | Until process ends                                                                                                                                 | Until JVM stops                                                                                                                                             | Until process ends                                                                                                                                       |
| **JAR Size**          | ~1.5 MB                                                                                                                                              | ~2 MB                                                                                                                                              | ~2.6 MB                                                                                                                                                     | ~1 MB                                                                                                                                                    |
| **Documentation**     | [h2database.com](https://www.h2database.com)                                                                                                         | [hsqldb.org](https://hsqldb.org)                                                                                                                   | [db.apache.org/derby](https://db.apache.org/derby)                                                                                                          | [sqlite.org](https://sqlite.org)                                                                                                                         |

