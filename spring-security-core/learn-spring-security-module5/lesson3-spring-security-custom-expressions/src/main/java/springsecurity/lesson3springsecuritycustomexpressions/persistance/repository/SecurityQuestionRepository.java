package springsecurity.lesson3springsecuritycustomexpressions.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.SecurityQuestion;

@Repository
public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {
    SecurityQuestion findByQuestionDefinitionIdAndStudentIdAndAnswer(Long questionDefinitionId, Long userId, String answer);

    Object findByStudentIdAndAnswer(Long id, String answer);

    SecurityQuestion findByQuestionDefinitionIdAndMyUserIdAndAnswer(Long questionDefinitionId, Long myUserId, String answer);
}
