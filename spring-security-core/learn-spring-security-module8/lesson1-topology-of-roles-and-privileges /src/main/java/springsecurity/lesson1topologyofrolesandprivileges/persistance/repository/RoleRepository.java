package springsecurity.lesson1topologyofrolesandprivileges.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Role;

@Repository
public abstract class RoleRepository implements JpaRepository<Role,Long> {
}
