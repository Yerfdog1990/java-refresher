# Spring Security Storage Mechanism

---
The following are the supported mechanisms for reading a username and password, which can be used with any of the supported storage mechanisms, as commonly defined in frameworks like Spring Security: 

---

## 1. In-Memory Authentication
Spring Security’s InMemoryUserDetailsManager implements UserDetailsService to provide support for username/password based authentication that is stored in memory. InMemoryUserDetailsManager provides management of UserDetails by implementing the UserDetailsManager interface. UserDetails-based authentication is used by Spring Security when it is configured to accept a username and password for authentication.

In the following sample, we use Spring Boot CLI to encode a password value of password and get the encoded password of {bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW:

**InMemoryUserDetailsManager Java Configuration**

```java
@Bean
public UserDetailsService users() {
	UserDetails user = User.builder()
		.username("user")
		.password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
		.roles("USER")
		.build();
	UserDetails admin = User.builder()
		.username("admin")
		.password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
		.roles("USER", "ADMIN")
		.build();
	return new InMemoryUserDetailsManager(user, admin);
}
```
The preceding samples store the passwords in a secure format but leave a lot to be desired in terms of a getting started experience.

In the following sample, we use User.withDefaultPasswordEncoder to ensure that the password stored in memory is protected. However, it does not protect against obtaining the password by decompiling the source code. For this reason, User.withDefaultPasswordEncoder should only be used for “getting started” and is not intended for production.

**InMemoryUserDetailsManager with User.withDefaultPasswordEncoder**

```java
@Bean
public UserDetailsService users() {
	// The builder will ensure the passwords are encoded before saving in memory
	UserBuilder users = User.withDefaultPasswordEncoder();
	UserDetails user = users
		.username("user")
		.password("password")
		.roles("USER")
		.build();
	UserDetails admin = users
		.username("admin")
		.password("password")
		.roles("USER", "ADMIN")
		.build();
	return new InMemoryUserDetailsManager(user, admin);
}
```

There is no simple way to use User.withDefaultPasswordEncoder with XML-based configuration. For demos or just getting started, you can choose to prefix the password with {noop} to indicate no encoding should be used:

```xml
<user-service> {noop} XML Configuration
<user-service>
	<user name="user"
		password="{noop}password"
		authorities="ROLE_USER" />
	<user name="admin"
		password="{noop}password"
		authorities="ROLE_USER,ROLE_ADMIN" />
</user-service>
```
---

## 2 JDBC Authentication

Spring Security’s JdbcDaoImpl implements UserDetailsService to provide support for username-and-password-based authentication that is retrieved by using JDBC. JdbcUserDetailsManager extends JdbcDaoImpl to provide management of UserDetails through the UserDetailsManager interface. UserDetails-based authentication is used by Spring Security when it is configured to accept a username/password for authentication.

In the following sections, we discuss:

* The Default Schema used by Spring Security JDBC Authentication
* Setting up a DataSource
* JdbcUserDetailsManager Bean

---

### Default Schema

Spring Security provides default queries for JDBC-based authentication. This section provides the corresponding default schemas used with the default queries. You need to adjust the schema to match any customizations to the queries and the database dialect you use.

#### User Schema

JdbcDaoImpl requires tables to load the password, account status (enabled or disabled) and a list of authorities (roles) for the user.

> The default schema is also exposed as a classpath resource named org/springframework/security/core/userdetails/jdbc/users.ddl.

Default User Schema

```java
create table users(
	username varchar_ignorecase(50) not null primary key,
	password varchar_ignorecase(500) not null,
	enabled boolean not null
);

create table authorities (
	username varchar_ignorecase(50) not null,
	authority varchar_ignorecase(50) not null,
	constraint fk_authorities_users foreign key(username) references users(username)
);
create unique index ix_auth_username on authorities (username,authority);
```
Oracle is a popular database choice but requires a slightly different schema:

Default User Schema for Oracle Databases
```sql
CREATE TABLE USERS (
    USERNAME NVARCHAR2(128) PRIMARY KEY,
    PASSWORD NVARCHAR2(128) NOT NULL,
    ENABLED CHAR(1) CHECK (ENABLED IN ('Y','N') ) NOT NULL
);


CREATE TABLE AUTHORITIES (
    USERNAME NVARCHAR2(128) NOT NULL,
    AUTHORITY NVARCHAR2(128) NOT NULL
);
ALTER TABLE AUTHORITIES ADD CONSTRAINT AUTHORITIES_UNIQUE UNIQUE (USERNAME, AUTHORITY);
ALTER TABLE AUTHORITIES ADD CONSTRAINT AUTHORITIES_FK1 FOREIGN KEY (USERNAME) REFERENCES USERS (USERNAME) ENABLE;
```

---

#### Group Schema

If your application uses groups, you need to provide the groups schema:

Default Group Schema

```sql
create table groups (
	id bigint generated by default as identity(start with 0) primary key,
	group_name varchar_ignorecase(50) not null
);

create table group_authorities (
	group_id bigint not null,
	authority varchar(50) not null,
	constraint fk_group_authorities_group foreign key(group_id) references groups(id)
);

create table group_members (
	id bigint generated by default as identity(start with 0) primary key,
	username varchar(50) not null,
	group_id bigint not null,
	constraint fk_group_members_group foreign key(group_id) references groups(id)
);
```

---

#### Setting up a DataSource

Before we configure JdbcUserDetailsManager, we must create a DataSource. In our example, we set up an embedded DataSource that is initialized with the default user schema.

**Embedded Data Source**

```java
@Bean
DataSource dataSource() {
	return new EmbeddedDatabaseBuilder()
		.setType(H2)
		.addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
		.build();
}
```

In a production environment, you want to ensure that you set up a connection to an external database.

JdbcUserDetailsManager Bean
In this sample, we use Spring Boot CLI to encode a password value of password and get the encoded password of {bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW. See the PasswordEncoder section for more details about how to store passwords.

**JdbcUserDetailsManager**

```java
@Bean
UserDetailsManager users(DataSource dataSource) {
	UserDetails user = User.builder()
		.username("user")
		.password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
		.roles("USER")
		.build();
	UserDetails admin = User.builder()
		.username("admin")
		.password("{bcrypt}$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxO/BTk76klW")
		.roles("USER", "ADMIN")
		.build();
	JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
	users.createUser(user);
	users.createUser(admin);
	return users;
}
```
---

## 3. UserDetailsService
UserDetailsService is used by DaoAuthenticationProvider for retrieving a username, a password, and other attributes for authenticating with a username and password. Spring Security provides in-memory, JDBC, and caching implementations of UserDetailsService.

You can define custom authentication by exposing a custom UserDetailsService as a bean. For example, the following listing customizes authentication, assuming that CustomUserDetailsService implements UserDetailsService:

This is only used if the AuthenticationManagerBuilder has not been populated and no AuthenticationProviderBean is defined.

**Custom UserDetailsService Bean**

```java
@Bean
CustomUserDetailsService customUserDetailsService() {
	return new CustomUserDetailsService();
}
```


---

# LDAP Authentication in Spring Security

## 1. What Is LDAP Authentication?

LDAP (Lightweight Directory Access Protocol) is often used by organizations as a **central repository for user information** and as an **authentication service**. In many enterprise environments, LDAP is also used to **store role information** for application users, making it a natural fit for authentication and authorization.

Spring Security’s **LDAP-based authentication** is used when Spring Security is configured to accept a **username and password** for authentication. However, even though a username and password are involved, **LDAP authentication does not use `UserDetailsService`**.

### Why `UserDetailsService` Is Not Used

In **bind authentication**, the LDAP server **never returns the password** (not even a hashed version). Because of this, the application **cannot validate the password itself**, which makes the `UserDetailsService` approach unsuitable.

Instead, Spring Security delegates authentication directly to the LDAP server.

---

## 2. How Spring Security Supports LDAP

There are many different ways an LDAP server can be configured. For this reason, Spring Security’s LDAP provider is **fully configurable**.

Spring Security:

* Uses **separate strategy interfaces** for authentication and role retrieval
* Provides **default implementations**
* Allows configuration for a wide range of LDAP scenarios

Spring Security does **not** use third-party LDAP libraries such as Mozilla LDAP or JLDAP. Instead, it makes extensive use of **Spring LDAP**.

---

## 3. Required Dependencies

To get started, you must add the **Spring Security LDAP dependencies**.

### Spring Boot Dependencies

#### Maven

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-ldap</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-ldap</artifactId>
</dependency>
```

---

## 4. Prerequisites

Before using LDAP with Spring Security, you should be familiar with:

* Basic LDAP concepts
* Directory structure
* Distinguished Names (DNs)

A recommended introduction is available at:
**[www.zytrax.com/books/ldap/](http://www.zytrax.com/books/ldap/)**

Some familiarity with **JNDI APIs** is also helpful. Since Spring Security relies heavily on **Spring LDAP**, understanding that project is beneficial if you plan to customize LDAP authentication.

> ⚠️ When using LDAP authentication, ensure that **LDAP connection pooling** is properly configured. Refer to the Java LDAP documentation if needed.

---

## 5. Setting Up an Embedded LDAP Server

Before configuring Spring Security, you need an LDAP server. For learning and testing, an **embedded LDAP server** is recommended.

Spring Security supports:

* **Embedded UnboundID Server**
* **Embedded ApacheDS Server** (⚠️ removed in Spring Security 7)

### Sample LDAP Data (`users.ldif`)

The following LDIF file initializes the embedded server with two users (`user` and `admin`), both having the password `password`:

```ldif
dn: ou=groups,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: groups

dn: ou=people,dc=springframework,dc=org
objectclass: top
objectclass: organizationalUnit
ou: people

dn: uid=admin,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Rod Johnson
sn: Johnson
uid: admin
userPassword: password

dn: uid=user,ou=people,dc=springframework,dc=org
objectclass: top
objectclass: person
objectclass: organizationalPerson
objectclass: inetOrgPerson
cn: Dianne Emu
sn: Emu
uid: user
userPassword: password
```

---

## 6. Embedded UnboundID Server (Recommended)

Spring Security 7 removes support for ApacheDS. **UnboundID is the recommended embedded LDAP server**.

### UnboundID Dependency

```xml
<dependency>
    <groupId>com.unboundid</groupId>
    <artifactId>unboundid-ldapsdk</artifactId>
    <version>7.0.4</version>
    <scope>runtime</scope>
</dependency>
```

### Embedded LDAP Server Configuration

```java
@Bean
public EmbeddedLdapServerContextSourceFactoryBean contextSourceFactoryBean() {
    return EmbeddedLdapServerContextSourceFactoryBean.fromEmbeddedLdapServer();
}
```

Alternatively, you may configure the server manually:

```java
@Bean
UnboundIdContainer ldapContainer() {
    return new UnboundIdContainer(
        "dc=springframework,dc=org",
        "classpath:users.ldif"
    );
}
```

---

## 7. LDAP ContextSource

The `ContextSource` is the LDAP equivalent of a JDBC `DataSource`.

If you configure an `EmbeddedLdapServerContextSourceFactoryBean`, Spring Security automatically creates a `ContextSource`.

### Explicit ContextSource Configuration

```java
ContextSource contextSource(UnboundIdContainer container) {
    return new DefaultSpringSecurityContextSource(
        "ldap://localhost:53389/dc=springframework,dc=org"
    );
}
```

---

## 8. Authentication in LDAP

Spring Security’s LDAP support **does not use `UserDetailsService`**.

Instead, authentication is implemented using the **`LdapAuthenticator` interface**, which:

* Authenticates the user
* Retrieves required user attributes

This is necessary because attribute permissions may depend on how authentication is performed (for example, binding as the user).

Spring Security provides **two `LdapAuthenticator` implementations**:

1. Bind Authentication
2. Password Authentication

---

## 9. Bind Authentication (Most Common)

Bind authentication submits the user’s **username and password directly to the LDAP server**.

### Advantages

* Password is never exposed to the application
* More secure
* Most commonly used approach

### Bind Authentication Configuration

```java
@Bean
AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
    LdapBindAuthenticationManagerFactory factory =
        new LdapBindAuthenticationManagerFactory(contextSource);

    factory.setUserDnPatterns("uid={0},ou=people");
    return factory.createAuthenticationManager();
}
```

### Bind Authentication with Search Filter

```java
@Bean
AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
    LdapBindAuthenticationManagerFactory factory =
        new LdapBindAuthenticationManagerFactory(contextSource);

    factory.setUserSearchBase("ou=people");
    factory.setUserSearchFilter("(uid={0})");
    return factory.createAuthenticationManager();
}
```

---

## 10. Password Authentication

Password authentication compares the user-supplied password with the stored password.

This can be done by:

* Retrieving the password attribute locally
* Performing an LDAP **compare operation**

⚠️ LDAP compare **cannot be used** if passwords are hashed with a random salt.

### Minimal Password Compare Configuration

```java
@Bean
AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
    LdapPasswordComparisonAuthenticationManagerFactory factory =
        new LdapPasswordComparisonAuthenticationManagerFactory(
            contextSource, NoOpPasswordEncoder.getInstance()
        );

    factory.setUserDnPatterns("uid={0},ou=people");
    return factory.createAuthenticationManager();
}
```

### Advanced Password Compare Configuration

```java
@Bean
AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
    LdapPasswordComparisonAuthenticationManagerFactory factory =
        new LdapPasswordComparisonAuthenticationManagerFactory(
            contextSource, new BCryptPasswordEncoder()
        );

    factory.setUserDnPatterns("uid={0},ou=people");
    factory.setPasswordAttribute("pwd");
    return factory.createAuthenticationManager();
}
```

---

## 11. LdapAuthoritiesPopulator

The `LdapAuthoritiesPopulator` determines what **authorities (roles)** are assigned to the user.

### Configuration Example

```java
@Bean
LdapAuthoritiesPopulator authorities(BaseLdapPathContextSource contextSource) {
    DefaultLdapAuthoritiesPopulator authorities =
        new DefaultLdapAuthoritiesPopulator(contextSource, "");

    authorities.setGroupSearchFilter("member={0}");
    return authorities;
}
```

```java
@Bean
AuthenticationManager authenticationManager(
        BaseLdapPathContextSource contextSource,
        LdapAuthoritiesPopulator authorities) {

    LdapBindAuthenticationManagerFactory factory =
        new LdapBindAuthenticationManagerFactory(contextSource);

    factory.setUserDnPatterns("uid={0},ou=people");
    factory.setLdapAuthoritiesPopulator(authorities);
    return factory.createAuthenticationManager();
}
```

---

## 12. Active Directory Authentication

Active Directory uses **non-standard LDAP authentication**. Users typically authenticate using:

```
user@domain
```

instead of a distinguished name.

Spring Security provides a specialized provider:
**`ActiveDirectoryLdapAuthenticationProvider`**

### Active Directory Configuration

```java
@Bean
ActiveDirectoryLdapAuthenticationProvider authenticationProvider() {
    return new ActiveDirectoryLdapAuthenticationProvider(
        "example.com",
        "ldap://company.example.com/"
    );
}
```

> DNS-based discovery of domain controllers is not currently supported.

---

## 13. Summary

* LDAP authentication delegates credential validation to the LDAP server
* `UserDetailsService` is not used
* Authentication is handled via `LdapAuthenticator`
* Bind authentication is preferred
* Password comparison is available but limited
* Spring Security 7 recommends **UnboundID**
* Active Directory has a dedicated provider

---

