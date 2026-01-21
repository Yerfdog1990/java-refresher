package springsecurity.lesson2datastructureofacl.persistence.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springsecurity.lesson2datastructureofacl.persistence.entity.Student;

import java.util.List;
import java.util.Optional;

@Repository
public interface IStudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
}
