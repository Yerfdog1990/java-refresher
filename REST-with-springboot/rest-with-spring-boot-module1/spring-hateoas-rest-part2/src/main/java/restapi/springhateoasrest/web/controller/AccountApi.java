package restapi.springhateoasrest.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restapi.springhateoasrest.persistence.entity.Amount;
import restapi.springhateoasrest.web.dto.AccountDto;
import restapi.springhateoasrest.persistence.entity.Account;
import restapi.springhateoasrest.service.AccountService;

import restapi.springhateoasrest.exception.AccountNotFoundException;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/accounts")
public class AccountApi {

    private final AccountService service;

    @Autowired
    public AccountApi(AccountService service) {
        this.service = service;
    }

    @GetMapping
    public List<AccountDto> getAll() {
        return service.listAll().stream()
                .map(AccountDto.Mapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getOne(@PathVariable("id") Integer id) {
        Account account = service.getOrThrow(id);
        return ResponseEntity.ok(AccountDto.Mapper.toDto(account));
    }

    @PostMapping
    public ResponseEntity<AccountDto> create(@RequestBody AccountDto accountDto) {
        Account account = AccountDto.Mapper.toEntity(accountDto);
        Account created = service.save(account);

        URI location = URI.create("/api/accounts/" + created.getId());
        return ResponseEntity
                .created(location)
                .body(AccountDto.Mapper.toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountDto> update(@PathVariable("id") Integer id, @RequestBody AccountDto accountDto) {

        accountDto.setId(id);
        Account account = AccountDto.Mapper.toEntity(accountDto);
        Account updated = service.save(account);

        return ResponseEntity.ok(AccountDto.Mapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deposit")
    public ResponseEntity<AccountDto> deposit(@PathVariable("id") Integer id, @RequestBody Amount amount) {

        Account updated = service.deposit(amount.getAmount(), id);
        return ResponseEntity.ok(AccountDto.Mapper.toDto(updated));
    }

    @PatchMapping("/{id}/withdraw")
    public ResponseEntity<AccountDto> withdraw(@PathVariable("id") Integer id, @RequestBody Amount amount) {

        Account updated = service.withdraw(amount.getAmount(), id);
        return ResponseEntity.ok(AccountDto.Mapper.toDto(updated));
    }
}

