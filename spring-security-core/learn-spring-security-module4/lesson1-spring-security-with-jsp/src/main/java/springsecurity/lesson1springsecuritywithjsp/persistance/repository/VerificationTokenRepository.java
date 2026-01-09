package springsecurity.lesson1springsecuritywithjsp.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springsecurity.lesson1springsecuritywithjsp.persistance.model.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);
    
    VerificationToken findByStudentId(Long id);

}
