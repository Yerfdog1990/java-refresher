package springsecurity.lesson3activatenewaccountviaemail.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson3activatenewaccountviaemail.persistance.model.VerificationToken;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);
    
    VerificationToken findByStudentId(Long id);

}
