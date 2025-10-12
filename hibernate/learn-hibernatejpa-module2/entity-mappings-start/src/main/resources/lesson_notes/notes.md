1. Overview
   In this lesson, we’ll deep-dive into mapping Java classes to database tables using JPA annotations, covering entity definitions, primary key mapping, schema customization, and implementations of equals() and hashCode() methods.

We’ll start by exploring the @Entity annotation, which marks a class for persistence and tells JPA to map it to a database table. Then, we’ll define a primary key with the @Id annotation, which is essential for uniquely identifying each record in the table.

Next, we’ll use the @Table and @Column annotations to customize how our entity maps to the database schema.

Finally, we’ll discuss why and how to implement equals() and hashCode() for entities.

The relevant module we need to import when starting with this lesson is: entity-mappings-start.

For a fully implemented lesson as a reference, we can import: entity-mappings-end.

2. Mapping a Java Class to an Entity
   We’ll begin by revisiting the basics of how JPA maps Java classes to database tables, starting with the requirements a Java class must meet to be mapped as a JPA entity.

2.1. Entity Class Requirements
Before we apply JPA annotations, let’s review the key requirements for a Java class to qualify as a JPA entity:

It must have a public or protected no-argument constructor
It must not be final and cannot have any final methods or persistent fields
Persistent fields must have private, protected, or package-private access
Exposes state through getter/setter or business methods
It must implement the Serializable interface if passed by value (via remote calls)
JPA entities can use fields or properties for persistence, with annotations placed on instance variables or getters. Additionally, they support primitives, wrappers, String, dates, enums, other entities, embeddable classes, and serializable custom types for fields and properties.

By default, all fields are persisted unless marked with @Transient or declared transient.

2.2. @Entity Annotation
Let’s begin with transforming our Campaign class into a JPA entity by adding the @Entity annotation:

@Entity
public class Campaign {
// fields, constructors, getters, and setters
}
Copy
Optionally, @Entity accepts a name attribute, which allows us to override the default entity name (i.e., the class name):

@Entity(name = "AdCampaign")
public class Campaign {
// ...
}
Copy
This will register the entity under the name AdCampaign in the persistence context, useful in JPQL queries.

However, this name doesn’t affect the database table name.

After annotating a class with @Entity, we can either rely on Hibernate’s auto-detection feature or explicitly list the class in the persistence.xml file so that it is recognized and handled by the persistence provider:

<persistence-unit name="LHJ">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    <!-- Define the classes -->
    <class>com.baeldung.lhj.persistence.model.Campaign</class>

    <!-- properties -->
</persistence-unit>
Copy
This configuration tells JPA to map the Campaign class to a table, typically named campaign. However, naming conventions may vary based on the database.

3. Defining Primary Keys
   Now that we have defined an entity and registered it in the persistence unit, the next step is to define a primary key to uniquely identify each record.

3.1. @Id Annotation
As we’ve discussed, every entity needs a unique identifier, which we define using the @Id annotation:

@Entity
public class Campaign {
@Id
private Long id;

    // other fields, constructors, getters, and setters
}
Copy
Here, JPA knows that the id field is the primary key for this entity. JPA supports several data types for the ID field:

Primitives (e.g., int, long) – not ideal because they can’t be null and default to 0 before persistence
Wrappers (Integer, Long) – preferred since they can be null before persistence
String – valid, commonly used with natural keys or UUIDs
Here, we can see that each time we create a new Campaign object, we need to assign a value to its unique identifier before persisting it.

While manual assignment of IDs is possible, it can become tedious and error-prone over time, especially as the application grows. Managing unique identifiers manually can lead to mistakes such as duplicates or missing values.

Instead, JPA lets us automate this process using the @GeneratedValue annotation.

3.2. @GeneratedValue Annotation
Let’s start with the minimal configuration – when we don’t want to worry about how IDs are generated:

@Entity
public class Campaign {
@Id
@GeneratedValue
private Long id;

    // other fields, constructors, getters, and setters
}
Copy
By default, JPA uses the AUTO strategy, which lets the provider decide the most suitable generation strategy based on the database. For example, Hibernate might use a sequence for PostgreSQL and identity columns for MySQL.

When we run the application (with automatic schema generation enabled), we can check the generated DDL to confirm how Hibernate (JPA provider) interprets this configuration:

Hibernate:
create sequence Campaign_SEQ start with 1 increment by 50;
create table Campaign (
id bigint not null,
name varchar(50),
code varchar(255),
description varchar(255),
primary key (id)
)
Copy
This output shows that Hibernate resolved the AUTO strategy to a sequence-based ID generation strategy, creating a Campaign_SEQ sequence for assigning primary key values.

Alternatively, for databases like MySQL or SQL Server that support auto-increment columns, we can use the IDENTITY strategy:

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY) 
private Long id; 
Copy
This approach relies on the database to generate a new value for each inserted row:

Hibernate:
create table Campaign ( 
        id bigint generated by default as identity,
name varchar(50),
code varchar(255),
description varchar(255),
primary key (id)
)
Copy
This confirms that Hibernate mapped the IDENTITY strategy to an identity column, allowing the database to auto-generate the primary key value during inserts.

We’ll go into more detail about all the strategies available in a future lesson.

4. Customizing the Table Name
   By default, JPA uses the entity class name as the database table name. However, we can override this behavior using the @Table annotation:

@Entity
@Table(name = "campaigns")
public class Campaign {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    // other fields, constructors, getters, and setters
}
Copy
In this example, the table will be named campaigns instead of the default campaign.

We can verify this in the generated DDL output:

Hibernate:
create table campaigns (
id bigint generated by default as identity,
name varchar(50),
code varchar(255),
description varchar(255),
primary key (id)
)
Copy
Additionally, the @Table annotation allows us to define other table-related properties, such as the schema or catalog, and constraints like indexes or unique constraints.

5. Mapping Attributes to Columns
   By default, JPA maps each field to a column with the same name. However, the @Column annotation allows us to override these defaults.

For instance, we can define a custom column name and specify constraints like uniqueness, nullability, and whether the value is updatable.

Let’s apply this to the code field and customize its mapping along with a few constraints:

@Entity
@Table(name = "campaigns")
public class Campaign {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    @Column(name = "codes", unique = true, nullable = false, updatable = false)
    private String code;

    // other fields, constructors, getters, and setters
}
Copy
When we run the application, we can verify the changes to the column definition in the generated DDL output:

Hibernate:
create table campaigns (
id bigint generated by default as identity,
name varchar(50),
codes varchar(255) not null unique,
description varchar(255),
primary key (id)
)
Copy
Here, we can see that Hibernate mapped the code field to a codes column and applied the UNIQUE and NOT NULL constraints as instructed.

The updatable = false value means that Hibernate won’t include this field when generating SQL UPDATE statements.

That’s it! We’ve now walked through all the essential mappings needed to transform a plain Java class into a JPA entity, along with ways to customize its schema.

To solidify our understanding, let’s apply these mappings to a few more example classes.

Here’s how the Task entity might look:

@Entity
public class Task {
@Id
private Long id;

    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    private final String uuid = UUID.randomUUID().toString();

    // other fields, constructors, getters, and setters
}
Copy
Here, uuid is used as a system-generated unique identifier for a Task. Since it doesn’t represent a business concept, it’s called a surrogate key – we’ll cover those in more depth in a future lesson.

Similarly, the Worker class will be:

@Entity
public class Worker {
@Id
private Long id;

    @Column(name = "email", unique = true, nullable = false, updatable = false)
    private String email;

    // other fields, constructors, getters, and setters
}
Copy
Here, we’re using email as a natural identifier. The constraints ensure that every worker has a unique, non-null email address that cannot be changed after it’s first set.

Finally, let’s complete the mapping by including all our entity classes in the persistence.xml file:

<persistence-unit name="LHJ">
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

    <!-- Define the classes -->
    <class>com.baeldung.lhj.persistence.model.Campaign</class>
    <class>com.baeldung.lhj.persistence.model.Task</class>
    <class>com.baeldung.lhj.persistence.model.Worker</class> 

   <!-- properties -->
</persistence-unit>
Copy
With that, we’ve covered the foundational mapping concepts in JPA.

6. Implementing equals() and hashCode()
   Now that we’ve defined our entities and their IDs, let’s examine how Java compares entity instances and why this can sometimes lead to unexpected behavior.

By default, Java compares object references for equality, meaning two instances of the Campaign class with the same ID but different references are considered unequal. That can lead to issues when working with collections like Set, Map, or even during unit testing.

JPA and Hibernate manage entity identity within the persistence context, but outside of it, we rely on equals() and hashCode() methods for entity comparisons. 

In JPA/Hibernate, using the ID field for equality checks can be unreliable before entities persist because the ID might still be null. Additionally, two different entity instances with the same ID might be considered equal, even though they represent separate objects.

Therefore, it’s crucial to correctly override equals() and hashCode() methods to avoid entity equality issues in the persistence layer.

Best practice recommends using a stable and unique business key, often referred to as a natural identifier (e.g., code, uuid, or email), for equality checks.

For example, in the Campaign class, we might choose the code field as the natural identifier:

@Override
public boolean equals(Object obj) {
if (this == obj)
return true;
if (!(obj instanceof Campaign other))
return false;

    return Objects.equals(getCode(), other.getCode());
}

@Override
public int hashCode() {
return Objects.hashCode(getCode());
}
Copy
With this implementation, two Campaign objects with the same code are treated as equal, ensuring consistent behavior in JPA, especially in collections like Set or Map and during entity comparisons.

We can take a similar approach in the Task class using the uuid field:

@Override
public boolean equals(Object obj) {
if (this == obj)
return true;
if (!(obj instanceof Task other))
return false;

    return Objects.equals(getUuid(), other.getUuid());
}

@Override
public int hashCode() {
return Objects.hash(getUuid());
}
Copy
Similarly, we can use the email field as a natural identifier in the Worker class to implement equals() and hashCode() methods:

@Override
public boolean equals(Object obj) {
if (this == obj)
return true;
if (!(obj instanceof Worker other))
return false;

    return Objects.equals(getEmail(), other.getEmail());
}

@Override
public int hashCode() {
return Objects.hashCode(getEmail());
}
Copy
Failing to override these methods can lead to hard-to-debug issues, especially when caching or managing persistence context.