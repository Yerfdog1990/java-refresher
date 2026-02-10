package restapi.springhateoasrest.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import restapi.springhateoasrest.persistence.entity.Account;

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

