1. Overview
   In this lesson, we’ll add and configure Hibernate, our JPA provider. We’ll connect it to an in-memory database, map the Worker domain entity, and explore how Hibernate translates object-oriented concepts into DDL statements for automated schema generation.

As a result, we’ll see Hibernate create the Worker database table for us automatically at startup.

Along the way, we’ll see how Hibernate can be one of the tools we use to maintain and evolve an application’s schema over time.

The relevant module you need to import when starting with this lesson is: setting-up-hibernate-and-jpa-start

If you want to have a look at the fully implemented lesson as a reference, feel free to import: setting-up-hibernate-and-jpa-end

2. Adding Dependencies
   To begin, we need to add Hibernate to our project’s dependencies.

Hibernate ships with a Bill of Materials (BOM) called hibernate-platform, ensuring compatibility between the modules it includes. Let’s add this to our pom.xml file:

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.hibernate.orm</groupId>
            <artifactId>hibernate-platform</artifactId>
            <version>${hibernate.version}</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
Copy
This is not mandatory, but certainly can be of use since this BOM manages the versions of Hibernate dependencies for us, so we no longer need to specify them individually.

Next, we’ll add the actual Hibernate’s core dependency to the main <dependencies> section of our pom.xml:

<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
</dependency>
Copy
This is the only Hibernate dependency we need for now.

Since Hibernate needs to connect to a database, let’s use H2 – an in-memory database often used for testing and quick setups:

<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <version>${h2.version}</version>
</dependency>
Copy
Similarly, we can connect to other databases like PostgreSQL and MySQL through their corresponding Maven dependencies.

Unlike the H2 dependency, which also installs an in-memory database, these dependencies provide drivers for connecting to production-grade relational databases like PostgreSQL and MySQL.

3. Configuring the Persistence Layer
   JPA and Hibernate need a few things from us to function correctly. Hibernate requires database connection details and a list of domain entities to map. This collection of information is called a persistence unit.

In this section, we’ll configure a few of its key settings.

3.1. Persistence XML
We define our persistence unit in a file called persistence.xml in the src/main/resources/META-INF directory. This file supports multiple persistence units, but we’ll define only one for this course (and for most production use cases).

We’ll start by declaring three of the main elements:

name – the name of the persistence unit
provider – an element that instructs JPA to use Hibernate as its provider
properties – a set of properties that includes the database connection, the schema generation strategy, and any other properties that Hibernate exposes
Let’s understand each configuration in detail.

3.2. Naming the Persistence Unit
Every persistence unit consisting of entity classes and database configurations should have a unique name. Let’s begin by naming our persistence unit LHJ:

<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.1">
  <persistence-unit name="LHJ">
    <!-- we'll add JPA/Hibernate configuration here -->
    <provider></provider>
    <properties>
    </properties>
  </persistence-unit>
</persistence>
Copy
We’ll use this name later to construct an EntityManager from JPA’s EntityManagerFactory.

3.3. Specifying the JPA Provider
As discussed in a previous lesson, Hibernate is the de facto provider of the JPA specification.

However, we must still tell JPA we’re using Hibernate as our provider. We do this by defining the <provider> element under <persistence-unit> in our persistence.xml:

<provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
Copy
Also, declaring Hibernate as the JPA provider allows us to specify Hibernate-specific configurations for our persistence unit.

3.4. Connecting to the Database
Now that JPA knows about Hibernate, we’ll be required to provide driver details and connection parameters in the <properties> element under <persistence-unit>:

<properties>
  <property name="hibernate.connection.driver_class" value="org.h2.Driver"/>
  <property name="hibernate.connection.url" value="jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE"/>
  <property name="hibernate.connection.username" value="sa"/>
  <property name="hibernate.connection.pool_size" value="5"/>
</properties>
Copy
Let’s take a moment to understand these Hibernate properties in detail:

hibernate.connection.driver_class is similar in concept to the JPA provider. That is, JPA requires a provider, and so does JDBC. This property specifies the JDBC driver Hibernate will use to connect to the database. Here, the org.h2.Driver is the driver for the H2 database.

hibernate.connection.url is the location of the database. jdbc:h2:mem:testdb is the syntax to connect to an in-memory instance of an H2 database called testdb.

hibernate.connection.username sets the username for database access. In this case, we use sa, the default for H2.

hibernate.connection.pool_size determines the number of database connections Hibernate can maintain in its connection pool. Here, we set it to 5, meaning Hibernate can hold up to five active connections simultaneously.

Alternatively to hibernate.connection.driver_class, url, and username, we can also use standard JPA properties:

<property name="jakarta.persistence.jdbc.driver" value="org.h2.Driver"/>
<property name="jakarta.persistence.jdbc.url" value="jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE"/>
<property name="jakarta.persistence.jdbc.user" value="sa"/>
Copy
These standard JPA properties offer vendor-agnostic configuration and make it easier to switch to a JPA provider other than Hibernate if needed.

However, Hibernate-specific properties allow additional configurations like connection pooling with finer control, which is missing in the standard JPA properties.

4. Mapping the Worker Entity
   Now that we’ve configured the persistence layer using JPA and Hibernate, let’s move on to another critical part of the setup – mapping a domain entity to a database table.

In this section, we’ll map the Worker entity so that JPA knows how to interact with it.

4.1. POJO to Entity
First, we must inform JPA that our Worker POJO class in the com.baeldung.lhj.persistence.model package is an entity. We’ll use an @Entity annotation to do that:

@Entity
public class Worker {
// getters and setters ...
}
Copy
The @Entity annotation marks the Worker class as a persistent entity. This means JPA now recognizes it as a Java representation of the Worker table in the database.

JPA also needs to know how to identify each record in this table uniquely. In relational databases, we typically use a primary key for it:

@Entity
public class Worker {

    @Id
    private Long id;
    
    // getters and setters ...
}
Copy
Here, the @Id annotation tells JPA that the id field acts as the primary key for this entity. That’s how JPA differentiates one Worker record from another in the database.

We’ll explore more annotations later, but this gives us a solid foundation for now.

4.2. Mapping Entity in Persistence XML
Now that we have our entity, we need to ensure JPA is aware of it when it starts.

Hibernate provides an auto-detection feature that automatically scans for classes annotated with @Entity, so you typically don’t need to list them manually. This is a convenient feature and works well in many cases. However, it’s important to note that this auto-detection is not part of the official JPA specification, which expects all entity classes to be explicitly declared in the persistence.xml file.

To align with the JPA standard, and for the sake of learning, this course will follow the explicit declaration approach. That means we will manually list our Worker entity class inside a <class> element within the <persistence-unit> section of persistence.xml:

<?xml version="1.0" encoding="UTF-8"?>
<persistence xmlns="https://jakarta.ee/xml/ns/persistence" version="3.1">
    <persistence-unit name="LHJ">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
        <!-- Define the classes -->
        <class>com.baeldung.lhj.persistence.model.Worker</class>
        <properties>
            <property name="hibernate.connection.driver_class" value="org.h2.Driver"/>
            <property name="hibernate.connection.url" value="jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE"/>
            <property name="hibernate.connection.username" value="sa"/>
            <property name="hibernate.connection.pool_size" value="5"/>
        </properties>
    </persistence-unit>
</persistence>
Copy
So, this is the complete persistence.xml file, which brings everything together.

To sum up what we have learned so far, our persistence unit declares Hibernate as the JPA provider, specifies the database connection details, and maps the Worker entity to the corresponding table in the database.

We should now understand how these configurations work together to enable JPA to manage our persistence layer effectively and interact with the Worker table using Hibernate.

5. Setting up the EntityManagerFactory
   So, our Worker entity is set up and declared in the configuration. However, how do we interact with the database?

5.1. Create EntityManagerFactory
For that, we need an EntityManagerFactory. This factory is responsible for creating EntityManager instances, which we’ll use to perform database operations:

EntityManagerFactory emf = Persistence.createEntityManagerFactory("LHJ");
Copy
This line of code lets JPA use the configuration from the persistence.xml file, referring to the LHJ persistence unit, and prepare everything we need to manage entities.

Now, let’s integrate this into our main class called LhjApp in the com.baeldung.lhj package:

public class LhjApp {

    public static void main(final String... args) {
        EntityManagerFactory emf = null;
        try {
            Logger logger = LoggerFactory.getLogger(LhjApp.class);
            logger.info("Running Learn Hibernate and JPA App");

            emf = Persistence.createEntityManagerFactory("LHJ");
        } finally {
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }
    }

}
Copy
Here, we created a logger to keep track of what’s going on, and then we initialized the persistence configuration by calling the createManageFactory() method of the Persistence class. We also wrapped the code in a try-finally block to make sure that the EntityManagerFactory is closed at the end by calling the close() method.

That’s it! Now, our app is ready to work with JPA and Hibernate. We can start it using the Maven command:

mvn clean package exec:java -Dexec.mainClass=com.baeldung.lhj.LhjApp
Copy
Then, we see the following log in the console:

16:12:56.393 [com.baeldung.lhj.LhjApp.main()] INFO  com.baeldung.lhj.LhjApp -- Running Learn Hibernate and JPA App
Copy
At startup, Hibernate logs lots of valuable information, which we can use here to understand more about what it’s doing.

5.2. Hibernate Logs
First, JPA finds and loads the persistence unit named LHJ using Hibernate:

16:12:56.456 [com.baeldung.lhj.LhjApp.main()] INFO o.h.jpa.internal.util.LogHelper -- HHH000204: Processing PersistenceUnitInfo [name: LHJ]
Copy
Next, it tells us which version of the Hibernate ORM we’re using:

23:10:45.979 [com.baeldung.lhj.LhjApp.main()] INFO org.hibernate.Version -- HHH000412: Hibernate ORM core version 6.6.0.Final
Copy
Then, Hibernate initializes a connection pool:

23:10:46.096 [com.baeldung.lhj.LhjApp.main()] WARN org.hibernate.orm.connections.pooling -- HHH10001002: Using built-in connection pool (not intended for production use)
Copy
Note that it emits a warning because the built-in pool lacks essential features and is meant only for testing and development. We won’t go into details here, but in a production environment, a dedicated connection pool like HikariCP, Apache DBCP, or C3P0 would be necessary to efficiently manage database connections.

After that, it confirms our database connection details:

23:10:46.210 [com.baeldung.lhj.LhjApp.main()] INFO org.hibernate.orm.connections.pooling -- HHH10001005: Database info:
Database JDBC URL [jdbc:h2:mem:testdb;DB_CLOSE_ON_EXIT=FALSE]
Database driver: org.h2.Driver
Database version: 2.3.232
Autocommit mode: false
Isolation level: undefined/unknown
Minimum pool size: 1
Maximum pool size: 5
Copy
So, we observed that Hibernate initializes by loading the LHJ persistence unit, connecting to an H2 in-memory database, and using a connection pool with a minimum of one active connection.

6. The ORM Schema Generation Feature
   Let’s explore an interesting and useful ORM feature that can make our persistence layer more powerful and insightful.

6.1. Managing Schema Generation
At this stage, we can rely on Hibernate to create and update our database schema automatically. This provides the convenience of ensuring our tables match the entity definitions.

Hibernate supports this through a property that we can add to the <properties> section of our persistence unit:

<property name="hibernate.hbm2ddl.auto" value="create-drop"/>
Copy
This property controls how Hibernate updates the database schema:

create-drop: At application startup, Hibernate drops the existing database schema (if any) and then recreates it. When the application shuts down, Hibernate drops the schema again.
create: Drops the existing schema (if any) and creates a new one at application startup, but it doesn’t drop the schema on shutdown.
update: Modifies the schema to match entity changes without dropping existing data
validate: Ensures that the existing schema matches entity definitions, but doesn’t modify it
none: Default setting to keep automatic schema management disabled
During development and testing, we often use create-drop to ensure a clean database for every run. However, we’d typically use validate or none in production environments to prevent unintentional data loss or schema modification.

Let’s wait for the next section to see this automatic schema creation in action by again inspecting the logs.

6.2. Enabling SQL Logging
As we work with Hibernate, we’ll likely want to know which SQL queries Hibernate runs behind the scenes. To make this transparent, we can enable SQL logging.

It’s also helpful since we’ve enabled the automatic schema generation, and we might be interested in seeing the actual queries run by Hibernate.

We can configure Hibernate to print SQL statements and format them for better readability by adding additional properties to our persistence.xml file:

<property name="hibernate.show_sql" value="true"/>
<property name="hibernate.format_sql" value="true"/>
Copy
With these settings in place:

hibernate.show_sql=true ensures that Hibernate prints every executed SQL statement in the logs
hibernate.format_sql=true makes the SQL output more readable by formatting it properly
Now, let’s rerun our application with the Maven command:

mvn clean package exec:java -Dexec.mainClass=com.baeldung.lhj.LhjApp
Copy
We can observe the following logs that show Hibernate tries to drop the existing Worker table (if it exists) and creates a fresh table for Worker with id, email, firstName, and lastName columns:

Hibernate:
drop table if exists Worker cascade   
Hibernate:
create table Worker (
id bigint not null,
email varchar(255),
firstName varchar(255),
lastName varchar(255),
primary key (id)
)
Copy
Here, our persistence layer guides Hibernate to automatically drop and recreate the Worker table using hbm2ddl.auto=create-drop, with SQL execution logged for debugging.

This feature of SQL logging helps debug and gain insights into query execution, but in production environments, this can negatively impact performance and expose sensitive data.

Therefore, it should be used only when necessary and disabled in production configurations.