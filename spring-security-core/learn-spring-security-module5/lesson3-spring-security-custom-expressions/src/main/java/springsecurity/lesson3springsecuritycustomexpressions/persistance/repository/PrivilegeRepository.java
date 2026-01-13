package springsecurity.lesson3springsecuritycustomexpressions.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    Privilege findByName(String fooReadPrivilege);
}
