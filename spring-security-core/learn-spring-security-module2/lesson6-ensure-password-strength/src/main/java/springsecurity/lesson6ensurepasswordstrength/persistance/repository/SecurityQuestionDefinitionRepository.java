package springsecurity.lesson6ensurepasswordstrength.persistance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson6ensurepasswordstrength.persistance.model.SecurityQuestionDefinition;

public interface SecurityQuestionDefinitionRepository extends JpaRepository<SecurityQuestionDefinition, Long> {

}