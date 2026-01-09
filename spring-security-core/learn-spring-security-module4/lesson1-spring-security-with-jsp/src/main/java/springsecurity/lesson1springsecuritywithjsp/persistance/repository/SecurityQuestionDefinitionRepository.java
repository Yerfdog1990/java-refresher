package springsecurity.lesson1springsecuritywithjsp.persistance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson1springsecuritywithjsp.persistance.model.SecurityQuestionDefinition;

public interface SecurityQuestionDefinitionRepository extends JpaRepository<SecurityQuestionDefinition, Long> {

}