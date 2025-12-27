package springsecurity.lesson1simpleregistrationflow.persistance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import springsecurity.lesson1simpleregistrationflow.persistance.model.Student;

@Repository
public interface IStudentRepository extends JpaRepository<Student, Long> {
    Student findByEmail(String email);
}
