
---

# Spring Boot REST API CRUD with HATEOAS Tutorial Part 2

---

## 14. Refactoring with RepresentationModelAssembler

So far we have done coding REST APIs for CRUD operations that follow HATEOAS principle. 

However, you can notice we have the following code snippet gets duplicated across APIs:

```java
account.add(linkTo(methodOn(AccountApi.class)
            .getOne(savedAccount.getId())).withSelfRel());
 
account.add(linkTo(methodOn(AccountApi.class)
            .listAll()).withRel(IanaLinkRelations.COLLECTION));
```

Spring HATEOAS provides a kind of assembler class that helps minimizing such duplication, and thus making leaner code.

## 15. AccountModelAssembler

>Note: As part of refactoring HATEOAS Code, we need to stop extending the RepresentationModel<Account> in the account entity class. 

Create a new class named AccountModelAssembler with the following code:



```java
@Component
public class AccountModelAssembler implements RepresentationModelAssembler<AccountDto, EntityModel<AccountDto>> {

  @Override
  public EntityModel<AccountDto> toModel(AccountDto entity) {
    EntityModel<AccountDto> dtoModel = EntityModel.of(entity);
    dtoModel.add(linkTo(methodOn(AccountModelAssemblerApi.class).readOne(entity.getId())).withSelfRel());
    dtoModel.add(linkTo(methodOn(AccountModelAssemblerApi.class).readAll()).withRel(IanaLinkRelations.COLLECTION));
    dtoModel.add(linkTo(methodOn(AccountModelAssemblerApi.class).deposit(entity.getId(), null)).withRel("Deposit"));
    dtoModel.add(linkTo(methodOn(AccountModelAssemblerApi.class).withdraw(entity.getId(), null)).withRel("Withdrawal"));
    return dtoModel;
  }
}
```

---

## 16. Controller class 

Then update the controller class to use model assembler as follows:
```java
@RestController
@RequestMapping("/api/accounts")
public class AccountModelAssemblerApi {
  private final AccountService service;
  private final AccountModelAssembler modelAssembler;

  @Autowired
  public AccountModelAssemblerApi(AccountService service,  AccountModelAssembler modelAssembler) {
    this.service = service;
    this.modelAssembler = modelAssembler;
  }

  @GetMapping("/{id}")
  public ResponseEntity<EntityModel<AccountDto>> readOne(@PathVariable("id") Integer id) {
    Account account = service.getOrThrow(id);
    AccountDto dto = AccountDto.Mapper.toDto(account);
    EntityModel<AccountDto> model = modelAssembler.toModel(dto);
    return new ResponseEntity<>(model, HttpStatus.OK);
  }

  @GetMapping
  public CollectionModel<EntityModel<AccountDto>> readAll() {
    List<EntityModel<AccountDto>> dtoModelList = service.listAll().stream()
            .map(AccountDto.Mapper::toDto)
            .map(modelAssembler::toModel)
            .collect(Collectors.toList());
    return CollectionModel.of(dtoModelList);
  }

  @PostMapping
  public ResponseEntity<EntityModel<AccountDto>> create(@RequestBody @Valid AccountDto accountDto) {
    Account dto = AccountDto.Mapper.toEntity(accountDto);
    Account createdAccount = service.save(dto);

    AccountDto resource = AccountDto.Mapper.toDto(createdAccount);
    EntityModel<AccountDto> model = modelAssembler.toModel(resource);

    return ResponseEntity.created(linkTo(methodOn(AccountModelAssemblerApi.class)
            .readOne(createdAccount.getId())).toUri()).body(model);
  }

  @PutMapping("/{id}")
  public ResponseEntity<EntityModel<AccountDto>> update(@PathVariable("id") Integer id, @RequestBody @Valid AccountDto accountDto) {
    accountDto.setId(id);
    Account account = AccountDto.Mapper.toEntity(accountDto);
    Account updatedAccount = service.save(account);

    AccountDto resource = AccountDto.Mapper.toDto(updatedAccount);
    EntityModel<AccountDto> model = modelAssembler.toModel(resource);
    return new ResponseEntity<>(model, HttpStatus.OK);
  }

  @PatchMapping("/{id}/deposits")
  public ResponseEntity<EntityModel<AccountDto>> deposit(@PathVariable("id") Integer id, @RequestBody Amount amount) {
    Account updatedAccount = service.deposit(amount.getAmount(), id);
    AccountDto resource = AccountDto.Mapper.toDto(updatedAccount);
    return new ResponseEntity<>(modelAssembler.toModel(resource), HttpStatus.OK);
  }

  @PatchMapping("/{id}/withdrawals")
  public ResponseEntity<EntityModel<AccountDto>> withdraw(@PathVariable("id") Integer id, @RequestBody Amount amount) {
    Account updatedAccount = service.withdraw(amount.getAmount(), id);
    AccountDto resource = AccountDto.Mapper.toDto(updatedAccount);
    return new ResponseEntity<>(modelAssembler.toModel(resource), HttpStatus.OK);
  }
}
```

You see, now we can use modelAssembler.toModel() method to convert a JPA entity class to an EntityModel class that includes HATEOAS links. 

And we don’t need to have our entity classes extend RepresentationModel anymore.

---

## 17. Test getOne endpoint

Now, if you test the retrieve API http://localhost:8080/api/accounts/1, you will get the following data:

```json
{
  "_links": {
    "self": {
      "href": "http://localhost:8080/api/accounts/model-assembler/1"
    },
    "collection": {
      "href": "http://localhost:8080/api/accounts/model-assembler"
    },
    "Deposit": {
      "href": "http://localhost:8080/api/accounts/model-assembler/1/deposits"
    },
    "Withdrawal": {
      "href": "http://localhost:8080/api/accounts/model-assembler/1/withdrawals"
    }
  },
  "accountNumber": "1982080185",
  "balance": 1021.99,
  "id": 1
}
```

You see, with these links included in data of an account, the client can follow to perform desired actions.

---

## 18. Add arbitrary links

You can add an arbitrary link to an entity model object like this:
>entityModel.add(Link.of("https://company.xyz", "Ref"));

The following line adds an arbitrary link to a collection model:
>collectionModel.add(Link.of("http://company.com/api/docs", "docs"));

And add a link with relation follows IANA-based link relation:
>collectionModel.add(Link.of("http://company.com/namhaminh", IanaLinkRelations.AUTHOR));

---

