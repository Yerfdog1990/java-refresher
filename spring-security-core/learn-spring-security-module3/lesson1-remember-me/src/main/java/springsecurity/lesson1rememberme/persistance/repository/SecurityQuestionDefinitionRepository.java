package springsecurity.lesson1rememberme.persistance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson1rememberme.persistance.model.SecurityQuestionDefinition;

public interface SecurityQuestionDefinitionRepository extends JpaRepository<SecurityQuestionDefinition, Long> {

}