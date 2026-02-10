package restapi.springhateoasrest.web.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;
import restapi.springhateoasrest.persistence.entity.Account;

@Data
@EqualsAndHashCode(of = {"accountNumber", "balance"}, callSuper = false)
public class AccountDto extends RepresentationModel<AccountDto> {

    private Integer id;

    private String accountNumber;

    private float balance;


    public static class Mapper {
        public static AccountDto toDto(Account account) {
            if (account == null) return null;
            AccountDto dto = new AccountDto();
            dto.id = account.getId();
            dto.accountNumber = account.getAccountNumber();
            dto.balance = account.getBalance();
            return dto;
        }

        public static Account toEntity(AccountDto dto) {
            if (dto == null) return null;
            Account account = new Account();
            account.setId(dto.id);
            account.setAccountNumber(dto.accountNumber);
            account.setBalance(dto.balance);
            return account;
        }
    }
}
