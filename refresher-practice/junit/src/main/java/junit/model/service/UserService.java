package junit.model.service;

import junit.model.repository.ClassRegister;
import junit.model.repository.RegistrationStatus;
import junit.model.repository.Student;

import java.util.List;
import java.util.Optional;

public interface UserService {
    ClassRegister registerStudent(Integer studentId, Student student, RegistrationStatus register);
    Optional<ClassRegister> findById(Integer id);
    List<ClassRegister> findAll();
    void deleteById(Integer id);
}
