package restapi.springhateoasrest.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restapi.springhateoasrest.persistence.entity.Account;
import restapi.springhateoasrest.persistence.entity.Amount;
import restapi.springhateoasrest.service.AccountService;
import restapi.springhateoasrest.web.dto.AccountDto;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/accounts/hateoas")
public class AccountHateoasApi {
    private final AccountService service;

    @Autowired
    public AccountHateoasApi(AccountService service) {
        this.service = service;
    }

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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deposit")
    public ResponseEntity<AccountDto> deposit(@PathVariable("id") Integer id, @RequestBody Amount amount) {
        Account updated = service.deposit(amount.getAmount(), id);
        AccountDto dto = AccountDto.Mapper.toDto(updated);
        dto.add(linkTo(methodOn(AccountHateoasApi.class).getOne(updated.getId())).withSelfRel());
        dto.add(linkTo(methodOn(AccountHateoasApi.class).getAll()).withRel(IanaLinkRelations.COLLECTION));
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<AccountDto> withdraw(@PathVariable("id") Integer id, @RequestBody Amount amount) {
        Account updated = service.withdraw(amount.getAmount(), id);
        AccountDto dto = AccountDto.Mapper.toDto(updated);
        dto.add(linkTo(methodOn(AccountHateoasApi.class).getOne(updated.getId())).withSelfRel());
        dto.add(linkTo(methodOn(AccountHateoasApi.class).getAll()).withRel(IanaLinkRelations.COLLECTION));
        return ResponseEntity.ok(dto);
    }
}
