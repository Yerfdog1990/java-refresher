package springsecurity.lesson2datastructureofacl.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springsecurity.lesson2datastructureofacl.persistence.entity.Possession;

@Repository
public interface IPossessionRepository extends JpaRepository<Possession, Long> {
}
