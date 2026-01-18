package springsecurity.lesson1topologyofrolesandprivileges.persistance.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.SecurityQuestionDefinition;

public interface SecurityQuestionDefinitionRepository extends JpaRepository<SecurityQuestionDefinition, Long> {

}