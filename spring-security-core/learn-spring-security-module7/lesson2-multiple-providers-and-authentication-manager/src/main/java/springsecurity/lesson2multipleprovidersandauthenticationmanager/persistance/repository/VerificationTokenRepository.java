package springsecurity.lesson2multipleprovidersandauthenticationmanager.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springsecurity.lesson2multipleprovidersandauthenticationmanager.persistance.model.VerificationToken;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);
    
    VerificationToken findByStudentId(Long id);

}
