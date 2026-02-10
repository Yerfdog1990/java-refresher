package restapi.springhateoasrest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import restapi.springhateoasrest.persistence.entity.Account;
import restapi.springhateoasrest.persistence.repository.AccountRepository;

import restapi.springhateoasrest.exception.AccountNotFoundException;
import java.util.List;

@Service
@Transactional
public class AccountService {

    private final AccountRepository repo;

    @Autowired
    public AccountService(AccountRepository repo) {
        this.repo = repo;
    }

    public List<Account> listAll() { return repo.findAll(); }
    public Account getOrThrow(Integer id) {
        return repo.findById(id).orElseThrow(() -> new AccountNotFoundException("Account with id " + id + " not found!"));
    }
    public Account save(Account account) { return repo.save(account); }
    public Account deposit(float amount, Integer id) {
        Account account = getOrThrow(id);
        repo.deposit(amount, id);
        return account;
    }

    public Account withdraw(float amount, Integer id) {
        Account account = getOrThrow(id);
        repo.withdraw(amount, id);
        return account;
    }
    public void delete(Integer id) { repo.deleteById(id); }
}
