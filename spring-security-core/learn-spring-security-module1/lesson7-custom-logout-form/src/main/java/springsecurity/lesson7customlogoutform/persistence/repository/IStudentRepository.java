package springsecurity.lesson7customlogoutform.persistence.repository;


import springsecurity.lesson7customlogoutform.persistence.entity.Student;

import java.util.List;
import java.util.Optional;

public interface IStudentRepository {

    // Create
    Student create(Student student);

    // Read
    Optional<Student> findStudentById(Long id);

    List<Student> findAll();

    Optional<Student> findByEmail(String email);

    Optional<Student> findByPassword(String password);

    // Update
    Student modify(Student student);

    // Delete
    void deleteStudentById(Long id);
}
