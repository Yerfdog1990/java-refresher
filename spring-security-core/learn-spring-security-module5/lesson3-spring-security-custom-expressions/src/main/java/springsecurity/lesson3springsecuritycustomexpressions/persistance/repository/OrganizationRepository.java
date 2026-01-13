package springsecurity.lesson3springsecuritycustomexpressions.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Organization findByName(String firstOrg);
}
