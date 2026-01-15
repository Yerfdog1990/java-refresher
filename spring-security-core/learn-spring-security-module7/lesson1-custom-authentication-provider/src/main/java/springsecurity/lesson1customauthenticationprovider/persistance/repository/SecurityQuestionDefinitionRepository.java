package springsecurity.lesson1customauthenticationprovider.persistance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson1customauthenticationprovider.persistance.model.SecurityQuestionDefinition;

public interface SecurityQuestionDefinitionRepository extends JpaRepository<SecurityQuestionDefinition, Long> {

}