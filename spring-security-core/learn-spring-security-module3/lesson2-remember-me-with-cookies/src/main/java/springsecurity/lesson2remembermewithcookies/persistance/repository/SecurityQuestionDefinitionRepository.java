package springsecurity.lesson2remembermewithcookies.persistance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson2remembermewithcookies.persistance.model.SecurityQuestionDefinition;

public interface SecurityQuestionDefinitionRepository extends JpaRepository<SecurityQuestionDefinition, Long> {

}