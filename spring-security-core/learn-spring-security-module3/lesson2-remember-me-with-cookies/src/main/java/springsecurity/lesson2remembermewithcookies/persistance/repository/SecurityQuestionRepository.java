package springsecurity.lesson2remembermewithcookies.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springsecurity.lesson2remembermewithcookies.persistance.model.SecurityQuestion;

@Repository
public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {
    SecurityQuestion findByQuestionDefinitionIdAndStudentIdAndAnswer(Long questionDefinitionId, Long userId, String answer);

    Object findByStudentIdAndAnswer(Long id, String answer);
}
