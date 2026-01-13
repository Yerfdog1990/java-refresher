
---

# Custom Security Expressions in Spring Security (Spring 6)

## 1. Overview

Spring Security provides a powerful **expression-based access control** system using annotations such as:

* `@PreAuthorize`
* `@PostAuthorize`
* `@PreFilter`
* `@PostFilter`

Common built-in expressions include:

* `hasRole('ADMIN')`
* `hasAuthority('READ_PRIVILEGE')`
* `isAuthenticated()`
* `principal`, `authentication`, `#methodParam`

However, **real-world authorization rules are often more domain-specific**, such as:

* “User can read only resources belonging to their organization”
* “User can update entities they own”
* “Permission depends on entity state”

In such cases, **built-in expressions are not expressive enough**.

### What We’ll Build

We’ll progressively explore **three levels of customization**:

1. **Custom `PermissionEvaluator`**
2. **Fully custom security expression (`isMember`)**
3. **Overriding / disabling a built-in expression**
4. **Alternative bean-based expressions**
5. **Mini-project demonstrating all concepts**

---

## 2. Domain Model (Foundation)

### 2.1 User Entity

```java
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "users_privileges",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "privilege_id")
    )
    private Set<Privilege> privileges = new HashSet<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "organization_id")
    private Organization organization;

    // getters and setters
}
```

---

### 2.2 Privilege Entity

```java
@Entity
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Privilege() {}
    public Privilege(String name) {
        this.name = name;
    }

    // getters and setters
}
```

---

### 2.3 Organization Entity

```java
@Entity
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    public Organization() {}
    public Organization(String name) {
        this.name = name;
    }

    // getters and setters
}
```

---

## 3. Custom Principal (`UserDetails`)

Spring Security works with `UserDetails`. We wrap our domain `User`.

```java
public class MyUserPrincipal implements UserDetails {

    private final User user;

    public MyUserPrincipal(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getPrivileges()
            .stream()
            .map(p -> new SimpleGrantedAuthority(p.getName()))
            .toList();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
```

---

## 4. `UserDetailsService`

```java
@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
        return new MyUserPrincipal(user);
    }
}
```

---

## 5. Test Data Initialization

```java
@Component
public class SetupData {

    private final UserRepository userRepository;
    private final PrivilegeRepository privilegeRepository;
    private final OrganizationRepository organizationRepository;

    public SetupData(
        UserRepository userRepository,
        PrivilegeRepository privilegeRepository,
        OrganizationRepository organizationRepository
    ) {
        this.userRepository = userRepository;
        this.privilegeRepository = privilegeRepository;
        this.organizationRepository = organizationRepository;
    }

    @PostConstruct
    public void init() {
        initPrivileges();
        initOrganizations();
        initUsers();
    }

    private void initPrivileges() {
        privilegeRepository.save(new Privilege("FOO_READ_PRIVILEGE"));
        privilegeRepository.save(new Privilege("FOO_WRITE_PRIVILEGE"));
    }

    private void initOrganizations() {
        organizationRepository.save(new Organization("FirstOrg"));
        organizationRepository.save(new Organization("SecondOrg"));
    }

    private void initUsers() {
        Privilege read = privilegeRepository.findByName("FOO_READ_PRIVILEGE");
        Privilege write = privilegeRepository.findByName("FOO_WRITE_PRIVILEGE");

        User john = new User();
        john.setUsername("john");
        john.setPassword("{noop}123");
        john.setPrivileges(Set.of(read));
        john.setOrganization(organizationRepository.findByName("FirstOrg"));
        userRepository.save(john);

        User tom = new User();
        tom.setUsername("tom");
        tom.setPassword("{noop}111");
        tom.setPrivileges(Set.of(read, write));
        tom.setOrganization(organizationRepository.findByName("SecondOrg"));
        userRepository.save(tom);
    }
}
```

---

## 6. Custom PermissionEvaluator

### 6.1 Why PermissionEvaluator?

* Avoid hardcoding privilege names
* Enable semantic expressions like:

  ```java
  hasPermission(foo, 'read')
  ```

---

### 6.2 Implementation

```java
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Override
    public boolean hasPermission(
        Authentication auth,
        Object targetDomainObject,
        Object permission
    ) {
        if (auth == null || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }

        String targetType =
            targetDomainObject.getClass().getSimpleName().toUpperCase();

        return hasPrivilege(auth, targetType, permission.toString().toUpperCase());
    }

    @Override
    public boolean hasPermission(
        Authentication auth,
        Serializable targetId,
        String targetType,
        Object permission
    ) {
        if (auth == null || targetType == null || !(permission instanceof String)) {
            return false;
        }

        return hasPrivilege(auth, targetType.toUpperCase(),
            permission.toString().toUpperCase());
    }

    private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
        return auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(a ->
                a.startsWith(targetType) &&
                a.contains(permission)
            );
    }
}
```

---

## 7. Method Security Configuration (Spring Security 6)

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {

    @Bean
    MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        DefaultMethodSecurityExpressionHandler handler =
            new DefaultMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return handler;
    }
}
```

---

## 8. Using `hasPermission` in Practice

```java
@RestController
@RequestMapping("/foos")
public class FooController {

    @PostAuthorize("hasPermission(returnObject, 'read')")
    @GetMapping("/{id}")
    public Foo findById(@PathVariable long id) {
        return new Foo("Sample");
    }

    @PreAuthorize("hasPermission(#foo, 'write')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Foo create(@RequestBody Foo foo) {
        return foo;
    }
}
```

---

## 9. Fully Custom Expression: `isMember()`

### 9.1 Custom Expression Root

```java
public class CustomMethodSecurityExpressionRoot
    extends SecurityExpressionRoot
    implements MethodSecurityExpressionOperations {

    private Object filterObject;
    private Object returnObject;
    private Object target;

    public CustomMethodSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    public boolean isMember(Long organizationId) {
        MyUserPrincipal principal = (MyUserPrincipal) getPrincipal();
        return principal.getUser()
            .getOrganization()
            .getId()
            .equals(organizationId);
    }

    // required overrides
    public void setFilterObject(Object filterObject) { this.filterObject = filterObject; }
    public Object getFilterObject() { return filterObject; }
    public void setReturnObject(Object returnObject) { this.returnObject = returnObject; }
    public Object getReturnObject() { return returnObject; }
    public Object getThis() { return target; }
    public void setThis(Object target) { this.target = target; }
}
```

---

### 9.2 Custom Expression Handler (Spring 6)

```java
public class CustomMethodSecurityExpressionHandler
    extends DefaultMethodSecurityExpressionHandler {

    @Override
    public EvaluationContext createEvaluationContext(
        Supplier<Authentication> authentication,
        MethodInvocation mi
    ) {
        StandardEvaluationContext context =
            (StandardEvaluationContext) super.createEvaluationContext(authentication, mi);

        CustomMethodSecurityExpressionRoot root =
            new CustomMethodSecurityExpressionRoot(authentication.get());

        root.setThis(mi.getThis());
        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(new AuthenticationTrustResolverImpl());
        root.setRoleHierarchy(getRoleHierarchy());

        context.setRootObject(root);
        return context;
    }
}
```

---

### 9.3 Register Handler

```java
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class CustomMethodSecurityConfig {

    @Bean
    MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        CustomMethodSecurityExpressionHandler handler =
            new CustomMethodSecurityExpressionHandler();
        handler.setPermissionEvaluator(new CustomPermissionEvaluator());
        return handler;
    }
}
```

---

### 9.4 Using `isMember`

```java
@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    @PreAuthorize("isMember(#id)")
    @GetMapping("/{id}")
    public Organization findOrgById(@PathVariable Long id) {
        return organizationRepository.findById(id).orElseThrow();
    }
}
```

---

## 10. Overriding / Disabling Built-in Expressions

### 10.1 Custom Root Blocking `hasAuthority`

```java
public class MySecurityExpressionRoot
    implements MethodSecurityExpressionOperations {

    protected Authentication authentication;

    public MySecurityExpressionRoot(Authentication authentication) {
        this.authentication = authentication;
    }

    public boolean hasAuthority(String authority) {
        throw new RuntimeException("hasAuthority() is disabled");
    }

    // boilerplate methods omitted
}
```

If someone writes:

```java
@PreAuthorize("hasAuthority('FOO_READ_PRIVILEGE')")
```

➡ **RuntimeException at runtime**

---

## 11. Alternative: Bean-Based Custom Expressions

```java
@Component("methodSecurityExpressionProvider")
public class CustomMethodSecurityExpressionProvider {

    public boolean isAdmin(MethodSecurityExpressionOperations root) {
        return root.hasAuthority("ROLE_ADMIN");
    }
}
```

Usage:

```java
@PreAuthorize("@methodSecurityExpressionProvider.isAdmin(#root)")
```

---

# 12. Mini-Project Summary

### Project Name

**spring-security-custom-expressions**

### Features

* Spring Boot 3 + Spring Security 6
* JPA + H2
* Custom `PermissionEvaluator`
* Custom security expressions (`isMember`)
* Disabled built-in expressions
* Integration tests using RestAssured

### Endpoints

| Endpoint                | Security               |
| ----------------------- | ---------------------- |
| GET /foos/{id}          | `hasPermission(read)`  |
| POST /foos              | `hasPermission(write)` |
| GET /organizations/{id} | `isMember(id)`         |

---

## 13. Spring Boot 2 / Spring Security 5 Notes (Legacy)

* Use `@EnableGlobalMethodSecurity`
* Extend `GlobalMethodSecurityConfiguration`
* Override `createSecurityExpressionRoot(...)`
* No `Supplier<Authentication>` API

Your provided Boot 2 snippets are **correct and compatible**.

---

## 14. Conclusion

Custom security expressions allow you to:

* Encode **business rules** directly into security
* Keep controllers and services clean
* Avoid fragile string-based role checks
* Build **semantic authorization logic**

**Rule of thumb**:

* Use `PermissionEvaluator` for **object-based permissions**
* Use custom expressions for **domain rules**
* Use bean-based expressions for **simple reuse**

---

