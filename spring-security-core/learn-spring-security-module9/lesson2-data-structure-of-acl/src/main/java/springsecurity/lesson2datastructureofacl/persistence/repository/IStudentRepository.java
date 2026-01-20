package springsecurity.lesson2datastructureofacl.persistence.repository;


import springsecurity.lesson2datastructureofacl.persistence.entity.Student;

import java.util.List;
import java.util.Optional;

public interface IStudentRepository {
    Student create(Student student);
    Optional<Student> findStudentById(Long id);
    Optional<Student> findByEmail(String email);
    List<Student> findAll();
    Student modify(Student student);
    void deleteStudentById(Long id);
}
