package springsecurity.lesson5securityquestions.persistance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson5securityquestions.persistance.model.SecurityQuestionDefinition;

public interface SecurityQuestionDefinitionRepository extends JpaRepository<SecurityQuestionDefinition, Long> {

}