
---

# **Lesson Notes: Spring with JPA and Hibernate**

---

## **1. Overview**

**Spring Framework**, **JPA (Java Persistence API)**, and **Hibernate** together form a powerful combination for managing data persistence in Java applications.

* **Spring** provides simplified configuration, dependency injection, and transaction management.
* **JPA** is a Java specification for object-relational mapping (ORM).
* **Hibernate** is the most popular **JPA implementation**, handling the mapping between Java objects and database tables.

In this lesson, we’ll explore how to integrate **Spring**, **JPA**, and **Hibernate** step-by-step — from project setup, configuration, repository implementation, and connecting to a MySQL database.

---

## **2. Spring Integration**

Bootstrapping Hibernate manually using its native API is lengthy and complex.
Fortunately, **Spring ORM** simplifies this integration. With only a few configurations, Spring can:

* Manage the **SessionFactory** (Hibernate’s core component)
* Handle **transactions**
* Provide declarative data access using **repositories**

---

## **3. Project Setup**

### **3.1 Maven Dependencies**

To set up Hibernate with Spring and JPA, add the following dependencies to your `pom.xml` file.

```xml
<!-- Hibernate Core -->
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.5.2.Final</version>
</dependency>

<!-- Spring ORM Integration -->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-orm</artifactId>
    <version>6.0.11</version>
</dependency>

<!-- In-Memory Database for Testing -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>2.1.214</version>
</dependency>

<!-- Tomcat JDBC Connection Pool -->
<dependency>
    <groupId>org.apache.tomcat</groupId>
    <artifactId>tomcat-dbcp</artifactId>
    <version>9.0.80</version>
</dependency>
```

**Output in Console (when the application starts):**

```
Starting embedded database:
url='jdbc:h2:mem:testdb;...'
```

This confirms that Spring Boot has automatically loaded the in-memory **H2 database**.

---

## **4. Custom Configuration**

### **4.1 Setting Up an Embedded Database**

We can explicitly configure our **DataSource** in a configuration class.

```java
@Configuration
public class PersistenceConfig {
  
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .setName("learn-spring-db")
            .build();
    }
}
```

**Console Output:**

```
Starting embedded database:
url='jdbc:h2:mem:learn-spring-db...'
```

This configuration defines a **DataSource bean** for an in-memory H2 database named `learn-spring-db`.

---

### **4.2 Configuring DataSource Manually**

Instead of using an embedded builder, we can manually define the **DriverManagerDataSource**:

```java
@Bean
public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
    dataSource.setUsername("username");
    dataSource.setPassword("password");
    return dataSource;
}
```

This approach provides flexibility for future migration to production databases like MySQL or PostgreSQL.

---

## **5. Configuring Hibernate with Spring**

Spring supports bootstrapping **Hibernate’s SessionFactory** automatically.
We can configure it in **Java-based** or **XML-based** formats.

---

### **5.1 Java Configuration**

```java
@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Bean
    public LocalSessionFactoryBean sessionFactory() {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.example.persistence.model");
        sessionFactory.setHibernateProperties(hibernateProperties());
        return sessionFactory;
    }

    @Bean
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
        dataSource.setUsername("sa");
        dataSource.setPassword("sa");
        return dataSource;
    }

    @Bean
    public PlatformTransactionManager hibernateTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory().getObject());
        return transactionManager;
    }

    private Properties hibernateProperties() {
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        return properties;
    }
}
```

---

### **5.2 XML Configuration**

If preferred, Hibernate can also be configured using XML:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">

    <bean id="sessionFactory" class="org.springframework.orm.hibernate5.LocalSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <property name="packagesToScan" value="com.example.persistence.model"/>
        <property name="hibernateProperties">
            <props>
                <prop key="hibernate.hbm2ddl.auto">create-drop</prop>
                <prop key="hibernate.dialect">org.hibernate.dialect.H2Dialect</prop>
            </props>
        </property>
    </bean>

    <bean id="dataSource" class="org.apache.tomcat.dbcp.dbcp2.BasicDataSource">
        <property name="driverClassName" value="org.h2.Driver"/>
        <property name="url" value="jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"/>
        <property name="username" value="sa"/>
        <property name="password" value="sa"/>
    </bean>

    <bean id="txManager" class="org.springframework.orm.hibernate5.HibernateTransactionManager">
        <property name="sessionFactory" ref="sessionFactory"/>
    </bean>
</beans>
```

To load this XML file, use:

```java
@Configuration
@EnableTransactionManagement
@ImportResource({"classpath:hibernate6Configuration.xml"})
public class HibernateXMLConfig {
}
```

---

## **6. Creating the Repository Layer**

### **6.1 JPA Repository Implementation**

We’ll now create a repository that uses **JPA’s EntityManager** directly.

```java
@Repository
public class ProjectRepositoryImpl implements IProjectRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    @Override
    public Optional<Project> findById(Long id) {
        Project item = em.find(Project.class, id);
        return item != null ? Optional.of(item) : Optional.empty();
    }

    @Transactional
    @Override
    public Project save(Project project) {
        em.persist(project);
        return project;
    }
}
```

Here:

* `@PersistenceContext` injects the **EntityManager**.
* `@Transactional` ensures the operations are executed within a transaction.

---

## **7. Connecting to a MySQL Database (Optional Extension)**

To use **MySQL** instead of the in-memory H2 database:

### **7.1 Add MySQL Driver Dependency**

```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>
```

---

### **7.2 Configure MySQL DataSource**

```java
@Bean
public DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
    dataSource.setUrl("jdbc:mysql://localhost:3306/learn-spring-db");
    dataSource.setUsername("username");
    dataSource.setPassword("password");
    return dataSource;
}
```

---

### **7.3 Configure EntityManagerFactory with Hibernate Properties**

```java
@Bean
public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
    em.setDataSource(dataSource);
    em.setPackagesToScan("com.example.persistence.model");

    JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
    em.setJpaVendorAdapter(vendorAdapter);

    Properties properties = new Properties();
    properties.setProperty("hibernate.hbm2ddl.auto", "update");
    em.setJpaProperties(properties);

    return em;
}
```

---

### **7.4 Optional: Running MySQL via Docker**

```bash
docker run --name spring-mysql -p 3306:3306 \
  -e MYSQL_ROOT_PASSWORD=pass \
  -e MYSQL_USER=username \
  -e MYSQL_PASSWORD=password \
  -e MYSQL_DATABASE=learn-spring-db \
  -d mysql:8
```

---

## **8. Using the SessionFactory in DAO Classes**

At this point, Hibernate and JPA are fully integrated with Spring.
You can inject the **SessionFactory** directly where needed:

```java
@Repository
public class ProjectDAO {

    @Autowired
    private SessionFactory sessionFactory;

    public void addProject(Project project) {
        Session session = sessionFactory.getCurrentSession();
        session.save(project);
    }
}
```

---

## **9. Key Points to Remember**

* **Spring ORM** simplifies Hibernate configuration and transaction management.
* **EntityManager** is used for persistence operations in JPA.
* **@PersistenceContext** automatically injects the persistence context.
* Use **LocalSessionFactoryBean** or **LocalContainerEntityManagerFactoryBean** for integration.
* **Transactional boundaries** are defined using `@Transactional`.
* H2 is best for **testing**; MySQL is ideal for **production**.

---

## **10. Summary**

In this lesson, we learned how to:

1. Set up a **Spring project** integrated with **JPA** and **Hibernate**.
2. Configure **DataSource**, **EntityManagerFactory**, and **SessionFactory**.
3. Implement a **repository layer** using **EntityManager**.
4. Connect the project to both **H2** and **MySQL** databases.

This integration lays the foundation for building **robust, scalable, and maintainable enterprise applications** using Spring’s ORM support.

---

