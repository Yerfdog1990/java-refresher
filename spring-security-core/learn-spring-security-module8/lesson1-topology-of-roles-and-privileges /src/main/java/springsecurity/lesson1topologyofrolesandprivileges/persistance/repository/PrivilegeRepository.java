package springsecurity.lesson1topologyofrolesandprivileges.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Privilege;

@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege,Long> {
}
