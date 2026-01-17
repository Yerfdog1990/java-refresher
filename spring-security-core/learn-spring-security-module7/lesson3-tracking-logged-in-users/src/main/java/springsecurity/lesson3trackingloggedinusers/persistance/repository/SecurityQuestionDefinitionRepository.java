package springsecurity.lesson3trackingloggedinusers.persistance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson3trackingloggedinusers.persistance.model.SecurityQuestionDefinition;

public interface SecurityQuestionDefinitionRepository extends JpaRepository<SecurityQuestionDefinition, Long> {

}