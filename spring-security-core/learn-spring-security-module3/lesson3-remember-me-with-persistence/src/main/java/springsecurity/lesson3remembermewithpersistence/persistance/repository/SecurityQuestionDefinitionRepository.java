package springsecurity.lesson3remembermewithpersistence.persistance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson3remembermewithpersistence.persistance.model.SecurityQuestionDefinition;

public interface SecurityQuestionDefinitionRepository extends JpaRepository<SecurityQuestionDefinition, Long> {

}