package restapi.springhateoasrest.persistence;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import restapi.springhateoasrest.persistence.entity.Account;
import restapi.springhateoasrest.persistence.repository.AccountRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Slf4j
public class AccountRepositoryTest {

    @Autowired
    private AccountRepository repo;

    @Test
    public void givenEntity_whenAddAccount_thenVerifyAccountNumberAndBalance() {
        Account account = new Account("1234567890", 1000.00f);
        repo.save(account);

        // Print the account number and balance
        log.info("\nAccount No.: {} \nBalance: {}", account.getAccountNumber(), account.getBalance());

        // Verify the account number
        String accountNumber = account.getAccountNumber();
        assertThat(accountNumber).isEqualTo("1234567890");

        // Verify the balance
        float balance = account.getBalance();
        assertThat(balance).isEqualTo(1000.00f);
    }

    @Test
    public void givenEntity_whenUpdateAccount_thenVerifyAccountBalance() {
        // Save an account
        Account account = new Account("1234567890", 1000.00f);
        repo.save(account);

        // Update balance
        account.setBalance(15000.00f);
        repo.save(account);

        // Print the account balance
        log.info("\nBalance: {}", account.getBalance());

        // Verify the balance
        float balance = account.getBalance();
        assertThat(balance).isEqualTo(15000.00f);
    }
}
