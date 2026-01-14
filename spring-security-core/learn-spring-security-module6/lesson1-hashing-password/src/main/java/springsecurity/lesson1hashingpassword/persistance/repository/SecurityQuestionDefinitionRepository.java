package springsecurity.lesson1hashingpassword.persistance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson1hashingpassword.persistance.model.SecurityQuestionDefinition;

public interface SecurityQuestionDefinitionRepository extends JpaRepository<SecurityQuestionDefinition, Long> {

}