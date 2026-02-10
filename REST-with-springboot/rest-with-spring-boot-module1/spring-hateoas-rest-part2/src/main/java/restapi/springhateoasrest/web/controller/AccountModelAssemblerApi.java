package restapi.springhateoasrest.web.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restapi.springhateoasrest.persistence.entity.Account;
import restapi.springhateoasrest.persistence.entity.Amount;
import restapi.springhateoasrest.service.AccountService;
import restapi.springhateoasrest.web.dto.AccountDto;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/accounts/model-assembler")
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
        CollectionModel<EntityModel<AccountDto>> collectionModel = CollectionModel.of(dtoModelList);
        collectionModel.add(linkTo(methodOn(AccountModelAssemblerApi.class).readAll()).withSelfRel());
        return collectionModel;
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