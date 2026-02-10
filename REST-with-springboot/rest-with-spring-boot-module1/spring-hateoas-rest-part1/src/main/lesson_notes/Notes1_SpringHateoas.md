
---

# Spring Boot REST API CRUD with HATEOAS Tutorial

## 1. Lesson Goal

In this tutorial, you will learn how to **build a complete CRUD REST API with Spring Boot that fully supports HATEOAS (Hypermedia as the Engine of Application State)**.

This lesson upgrades a traditional **Level 2 REST API** (resources + HTTP verbs) into a **Level 3 REST API** by embedding **hypermedia links** that guide clients through available actions.

By the end of this lesson, you will be able to:

* Explain HATEOAS and why it matters
* Build REST APIs with **Spring Boot + Spring HATEOAS**
* Implement **CRUD operations** with hypermedia controls
* Use **HAL** as the hypermedia format
* Refactor controllers using **RepresentationModelAssembler**
* Design APIs that are **discoverable, evolvable, and decoupled**

---

## 2. Technologies Used

This project uses the following technologies:

* **Spring Boot:** simplifies development of Spring-based projects
* **Spring Data JPA:** simplifies coding of data access layer, with Hibernate is the default ORM framework
* **Spring Web:** simplifies developing web-based apps, especially RESTful webservices and REST APIs
* **Spring HATEOAS:** eases creation of links embedded in JSON

### Development Tools

* Java JDK 11 or 17
* Java IDE (IntelliJ IDEA, Eclipse, or Spring Tool Suite)
* Postman or curl for API testing

---

## 3. What Is HATEOAS?

**HATEOAS (Hypermedia as the Engine of Application State)** is a REST architectural constraint that makes REST APIs truly RESTful.

With HATEOAS:

* The server returns **data + links**
* Links describe **possible next actions**
* Clients navigate APIs by **following links**, not hardcoding URLs

### Example HATEOAS Response

```json
{
  "id": 3,
  "accountNumber": "1982094128",
  "balance": 6211,
  "_links": {
    "self": { "href": "http://localhost:8080/api/accounts/3" },
    "deposits": { "href": "http://localhost:8080/api/accounts/3/deposit" },
    "withdrawls": { "href": "http://localhost:8080/api/accounts/3/withdraw" },
    "collection": { "href": "http://localhost:8080/api/accounts" }
  }
}
```
As you can see, in addition to the data (id, accountNumber and balance), the JSON document also includes 4 links that allow the REST client to perform actions related to that data, such as get account details - specified by the link relation (rel) is self, deposit an amount (with rel deposits), withdraw an amount (with rel withdrawals) or get all accounts (with rel collection).
So in this way of communication, from a base URI, the clients can follow the links included in server’s responses in order to navigate resources and perform related actions - or change states of resources - hence the term Hypermedia as the Engine of Application State. The term hypermedia refers to any content that contains links to other form of media such as text, images, movies.


HATEOAS in REST APIs is like hyperlinks in web pages: the users can browse a website from a domain name and click on hyperlinks to explore content without prior knowledge of hyperlinks. And REST clients do not need to have prior knowledge of resource URIs to use APIs - all they need is a base URI from which they can traverse and follow the links included in responses.
And the greatest advantage of HATEOAS is that it decouples server and client. Developers can update and evolve their APIs without worrying about breaking clients because the client should make no assumption about resource URIs rather than the base one.

In a nutshell, this response tells the client:

* Where the resource is (`self`)
* What actions are possible (`deposit`, `withdraw`)
* How to navigate back to the collection

### Key Benefit

> HATEOAS **decouples clients from server URI structures**, allowing APIs to evolve without breaking clients.

---

## 4. Spring HATEOAS and HAL

### Spring HATEOAS

Spring HATEOAS provides APIs to:

* Create hypermedia links
* Wrap entities in representation models
* Support standard hypermedia formats

All HATEOAS-related APIs live in:

```
org.springframework.hateoas
```

### HAL (Hypertext Application Language)

HAL is a standard JSON format for hypermedia APIs.
It defines conventions for:

* `_links`
* `_embedded`

Spring HATEOAS uses **HAL by default**, making APIs interoperable with HAL-aware clients.

---

## 5. Project Setup

### Maven Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>    
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```
Besides spring-boot-starter-web that supports RESTful webservices and spring-boot-starter-jpa that supports JPA repositories, we use spring-boot-starter-hateoas that simplifies the creation of hypermedia links for REST APIs. We also use h2 for in-memory database.

---

## 6. Domain Model and Repository Layer

### Account Entity

Next, in the repository layer, code an entity class that represents a bank account:
```java
@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = {"accountNumber", "balance"}, callSuper = false)
public class Account extends RepresentationModel<Account> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true, length = 20)
    private String accountNumber;

    private float balance;

    public Account(String accountNumber, float balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }
}
```

### Amount Value Object

And code a POJO class that represents an amount used to deposit and withdraw:

```java
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Amount {
  private float amount;
}
```

### Repository Interface

And code a JPA repository interface as follows:
```java
@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = a.balance + ?1 WHERE a.id = ?2")
    void deposit(float amount, Integer id);

    @Modifying
    @Transactional
    @Query("UPDATE Account a SET a.balance = a.balance - ?1 WHERE a.id = ?2")
    void withdraw(float amount, Integer id);
}
```

---

## 7. Centralize HTTP Mapping with @RestControllerAdvice

And code a Global exception handler as follows:
```java
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Void> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}
```
## 8. Service Layer

Next, in the service layer, code the service class as shown below:
```java
@Service
@Transactional
public class AccountService {

  private final AccountRepository repo;

  @Autowired
  public AccountService(AccountRepository repo) {
    this.repo = repo;
  }

  public List<Account> listAll() { return repo.findAll(); }
  public Account getOrThrow(Integer id) throws AccountNotFoundException {
    return repo.findById(id).orElseThrow(() -> new AccountNotFoundException("Account with id " + id + "not found!"));
  }
  public Account save(Account account) { return repo.save(account); }
  public Account deposit(float amount, Integer id) throws AccountNotFoundException {
    Account account = getOrThrow(id);
    repo.deposit(amount, id);
    return account;
  }

  public Account withdraw(float amount, Integer id) throws AccountNotFoundException {
    Account account = getOrThrow(id);
    repo.withdraw(amount, id);
    return account;
  }
  public void delete(Integer id) { repo.deleteById(id); }
}
```

---

## 9. Data Initialization

And for the database, code the following Spring configuration class that initializes some sample data upon applications startup:
```java
@Configuration
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(AccountRepository repo) {
        return args -> {
            repo.saveAll(List.of(
                new Account("1982080185", 1021.99f),
                new Account("1982032177", 231.50f),
                new Account("1982094128", 6211.00f)
            ));
        };
    }
}
```
This will insert 3 rows into the accounts table when the application starts. And note that we use H2 in-memory database. And you can configure to use a physical database like MySQL

---

## 9. Retrieve Operations using Traditional controller

For retrieve operation, the HTTP request method should be GET and response status code should be 200 OK for successful operation, or 404 Not Found if the resource not available.

### Single Resource

The base URI for Account APIs should be /api/accounts which returns a list of accounts to the client. However, we start from the retrieve operation that gets information of a single account - with the URI /api/accounts/{id} - so you will be able to understand the code from simple to complex.
The initial code is as simple as follows:
```java
 @GetMapping("/{id}")
public ResponseEntity<AccountDto> getOne(@PathVariable("id") Integer id) throws AccountNotFoundException {
  Account account = service.getOrThrow(id);
  return ResponseEntity.ok(AccountDto.Mapper.toDto(account));
}
```

If you run the command curl localhost:8080/api/accounts/3, you will get the following data of the account ID 3:

```json
{
  "id": 3,
  "accountNumber": "1982094128",
  "balance": 6211
}
```

It’s just data only, no links which the client can use to perform actions on the data. So let’s update the code to add the first link that points to the resource itself (with self relation).

## 10. Code HATEOAS-driven REST API for Retrieve Operations

Update the entity class to extend the RepresentionalModel abstract class defined by Spring HATEOAS:
```java
public class Account extends RepresentationModel<Account> {

  // existing code remains unchanged
}
```

Import the RepresentionalModel type from the org.springframework.hateoas package. Then update the getOne() method like this:

```java
@GetMapping("/{id}")
public ResponseEntity<AccountDto> getOne(@PathVariable("id") Integer id) {
  Account account = service.getOrThrow(id);
  AccountDto dto = AccountDto.Mapper.toDto(account);

  dto.add(linkTo(methodOn(AccountHateoasApi.class).getOne(id)).withSelfRel());
  dto.add(linkTo(methodOn(AccountHateoasApi.class).getAll()).withRel(IanaLinkRelations.COLLECTION));
  dto.add(linkTo(methodOn(AccountHateoasApi.class).delete(id)).withRel("delete"));
  dto.add(linkTo(methodOn(AccountHateoasApi.class).update(id, dto)).withRel("update"));


  return ResponseEntity.ok(dto);
}
```

For this to work, you need to add the following additional import statements:

```java
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
```
We use the add() method provided by the RepresentionalModel class to include a link in the JSON response. The linkTo() method creates a link pointing to a controller method, and the methodOn() method tells which method of which controller class will be used to generate the link. And the withSelRef() method specifies the relation of the link is self.
Now, if you call the API again, you will see the data includes a link:

```json
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/accounts/hateoas/3"
    },
    "delete": {
      "href": "http://localhost:8080/api/accounts/hateoas/3"
    },
    "update": {
      "href": "http://localhost:8080/api/accounts/hateoas/3"
    },
    "deposit": {
      "href": "http://localhost:8080/api/accounts/hateoas/3/deposit"
    },
    "withdraw": {
      "href": "http://localhost:8080/api/accounts/hateoas/3/withdraw"
    }
  },
  "accountNumber": "1982094128",
  "balance": 6211.0,
  "id": 3
}
```

You see, the link with relation “self” tells the client that it is the link of the current resource itself.
It’s common to include a link pointing to a collection of accounts. Put the following statement to add the second link with the relation name “collection”:

>account.add(linkTo(methodOn(AccountHateoasApi.class).getAll()).withRel(IanaLinkRelations.COLLECTION));

Here, we use a relation name defined by IANA (Internet Assigned Numbers Authority) to make the links easily discoverable by REST clients that also use IANA-based relations.
For this to work, you need to implement the getAll() method as follows:

```java
@GetMapping
public CollectionModel<AccountDto> getAll() {
  List<AccountDto> listAccounts = service.listAll().stream()
          .map(account -> {
            AccountDto dto = AccountDto.Mapper.toDto(account);
            dto.add(linkTo(methodOn(AccountHateoasApi.class).getOne(account.getId())).withSelfRel());
            return dto;
          })
          .collect(Collectors.toList());

  CollectionModel<AccountDto> collectionModel = CollectionModel.of(listAccounts);

  collectionModel.add(linkTo(methodOn(AccountHateoasApi.class).getAll()).withSelfRel());

  return collectionModel;
}
```
Here, you can see we use a CollectionModel object to hold a list of Account objects so we can put links to the JSON response.
Now, if you call the URI /api/accounts/hateoas, you will see the data looks like the following:

```json
{
  "_embedded": {
    "accountDtoList": [
      {
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/accounts/hateoas/1"
          },
          "collection": {
            "href": "http://localhost:8080/api/accounts/hateoas"
          }
        },
        "accountNumber": "1982080185",
        "balance": 1021.99,
        "id": 1
      },
      {
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/accounts/hateoas/2"
          },
          "collection": {
            "href": "http://localhost:8080/api/accounts/hateoas"
          }
        },
        "accountNumber": "1982032177",
        "balance": 231.5,
        "id": 2
      },
      {
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/accounts/hateoas/3"
          },
          "collection": {
            "href": "http://localhost:8080/api/accounts/hateoas"
          }
        },
        "accountNumber": "1982094128",
        "balance": 6211.0,
        "id": 3
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/accounts/hateoas"
    }
  }
}
```
---

## 10. Create Operation (POST)

For create operation, the HTTP request method should be POST and response status code should be 201 Created for successful creation, or 400 Bad Request if the input is invalid. Add the following method into the AccountApi class:

```java
@PostMapping
public ResponseEntity<AccountDto> create(@RequestBody AccountDto accountDto) {
  Account account = AccountDto.Mapper.toEntity(accountDto);
  Account created = service.save(account);

  AccountDto resource = AccountDto.Mapper.toDto(created);
  resource.add(linkTo(methodOn(AccountHateoasApi.class).getOne(created.getId())).withSelfRel());
  resource.add(linkTo(methodOn(AccountHateoasApi.class).getAll()).withRel(IanaLinkRelations.COLLECTION));

  return ResponseEntity
          .created(URI.create("/api/accounts/hateoas/" + created.getId()))
          .body(resource);
}
```

* Returns **201 Created**
* Sets **Location header**
* Includes hypermedia links

### Request body for create operation

You see, this API returns details of the newly created account. Use the following curl command to test adding an account:

>URL: http://localhost:8080/api/accounts/hateoas

```json
{
  "accountNumber": "112233440099", 
  "balance": 99.99
}
```

You will see the following output:

```json
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/accounts/hateoas/4"
    },
    "collection": {
      "href": "http://localhost:8080/api/accounts/hateoas"
    }
  },
  "accountNumber": "112233440099",
  "balance": 99.99,
  "id": 4
}
```
You see, the status code is 201 Created and the header Location pointing to a URL of the account details. And in JSON data, it includes 2 links with relation self and collection. This allows the client to follow the links to do the next action.

---

## 11. Full Update (PUT)

For full update operation, the HTTP request method should be PUT and response status code should be 200 OK for successful update operation. So add the following code into the AccountApi class:
```java
@PutMapping("/{id}")
public ResponseEntity<AccountDto> update(@PathVariable("id") Integer id, @RequestBody AccountDto accountDto) {
  accountDto.setId(id);
  Account account = AccountDto.Mapper.toEntity(accountDto);
  Account updated = service.save(account);

  AccountDto dto = AccountDto.Mapper.toDto(updated);
  dto.add(linkTo(methodOn(AccountHateoasApi.class).getOne(updated.getId())).withSelfRel());
  dto.add(linkTo(methodOn(AccountHateoasApi.class).getAll()).withRel(IanaLinkRelations.COLLECTION));

  return ResponseEntity.ok(dto);
}
```
### Request body for full update operation

>URL: http://localhost:8080/api/accounts/hateoas/3

```json
{
  "accountNumber": "1982094188",
  "balance": 15950.0
}
```

You will see the following output:

```json
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/accounts/hateoas/3"
    },
    "collection": {
      "href": "http://localhost:8080/api/accounts/hateoas"
    }
  },
  "accountNumber": "1982094188",
  "balance": 15950.0,
  "id": 3
}
```

You see, the data also includes HATEOAS links same as the create API.

---

## 12. Partial Updates (PATCH)

### Deposit
Next, I’m going to show you how to implement APIs for deposit and withdrawal, which are partial update operations - only the account balance gets updated.

For the partial update operation, the HTTP request method should be PATCH and the response status code is 200 OK for successful update operation. Code the following method for deposit API:
```java
 @PatchMapping("/{id}/deposit")
public ResponseEntity<AccountDto> deposit(@PathVariable("id") Integer id, @RequestBody Amount amount) {
  try {
    Account updated = service.deposit(amount.getAmount(), id);
    AccountDto dto = AccountDto.Mapper.toDto(updated);
    dto.add(linkTo(methodOn(AccountHateoasApi.class).getOne(updated.getId())).withSelfRel());
    dto.add(linkTo(methodOn(AccountHateoasApi.class).getAll()).withRel(IanaLinkRelations.COLLECTION));
    return ResponseEntity.ok(dto);
  } catch (AccountNotFoundException e) {
    return ResponseEntity.notFound().build();
  }
}
```
### Request body for partial update operation

To test this deposit API, you can use the following

>URL: http://localhost:8080/api/accounts/hateoas/2/deposit
```json
{
  "amount": 300.0
}
```
This adds an amount of 300 to the account ID 3. You will see the data in the response like this:


```json
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/accounts/hateoas/2"
    },
    "collection": {
      "href": "http://localhost:8080/api/accounts/hateoas"
    }
  },
  "accountNumber": "1982032177",
  "balance": 131.5,
  "id": 2
}
```

You see, only the account balance got updated (increased), right?
This is because the deposit API only updates the balance field of the account entity.
The full update API updates all fields of the account entity.
So, if you want to update other fields of the account entity, you need to use the full update API.
The same applies to withdrawal API.
Now let us move to the withdrawal API:

### Withdraw

```java
 @PatchMapping("/{id}/withdraw")
public ResponseEntity<AccountDto> withdraw(@PathVariable("id") Integer id, @RequestBody Amount amount) {
  try {
    Account updated = service.withdraw(amount.getAmount(), id);
    AccountDto dto = AccountDto.Mapper.toDto(updated);
    dto.add(linkTo(methodOn(AccountHateoasApi.class).getOne(updated.getId())).withSelfRel());
    dto.add(linkTo(methodOn(AccountHateoasApi.class).getAll()).withRel(IanaLinkRelations.COLLECTION));
    return ResponseEntity.ok(dto);
  } catch (AccountNotFoundException e) {
    return ResponseEntity.notFound().build();
  }
}
```
And run the following request body to withdraw an amount of 100 from the account ID 2:

>URL: http://localhost:8080/api/accounts/hateoas/2/withdraw

```json
{
  "amount": 100.0
}
```
And observe the data in the response:

```json
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/accounts/hateoas/2"
    },
    "collection": {
      "href": "http://localhost:8080/api/accounts/hateoas"
    }
  },
  "accountNumber": "1982032177",
  "balance": 231.5,
  "id": 2
}
```

You see, only the account balance gets updated (decreased), right? 100 was withdrawn from the account.
This is because the withdraw API only updates the balance field of the account entity.
The full update API updates all fields of the account entity.
So, if you want to update other fields of the account entity, you need to use the full update API.

---

## 13. Delete Operation (DELETE)

For delete operation, the HTTP request method should be DELETE and the response status code should be 204 No Content for successful deletion. So write the following code for delete account API:
```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
  service.delete(id);
  return ResponseEntity.noContent().build();
}
```
You see, this is the only API that doesn’t return any data. For testing, run the following request to delete the account ID 1:

>URL: http://localhost:8080/api/accounts/hateoas/1

And you will see the status code 204 in the output.

However, if you call the URI http://localhost:8080/api/accounts/hateoas to confirm the deletion, you will see the account ID 1 does not exist. 

```json
{
  "_embedded": {
    "accountDtoList": [
      {
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/accounts/hateoas/2"
          }
        },
        "accountNumber": "1982032177",
        "balance": 431.5,
        "id": 2
      },
      {
        "_links": {
          "self": {
            "href": "http://localhost:8080/api/accounts/hateoas/3"
          }
        },
        "accountNumber": "1982094128",
        "balance": 6211.0,
        "id": 3
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/accounts/hateoas"
    }
  }
}
```
---

## 15. Refactor controllers using **RepresentationModelAssembler**

> This section will be covered in part 2 of "Spring Boot REST API CRUD with HATEOAS Tutorial."

## 14. Key Takeaways

* HATEOAS elevates REST APIs to **Richardson Level 3**
* Clients navigate APIs via **links, not documentation**
* CRUD operations remain the same — **hypermedia makes them discoverable**
* APIs become **evolvable, decoupled, and self-describing**

---

