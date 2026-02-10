package restapi.springhateoasrest.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;
import org.springframework.hateoas.RepresentationModel;

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

