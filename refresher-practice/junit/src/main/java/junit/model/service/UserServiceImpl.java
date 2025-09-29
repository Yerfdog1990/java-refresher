package junit.model.service;

import junit.model.repository.*;

import java.util.List;
import java.util.Optional;

public class UserServiceImpl implements UserService {
    private final ClassRegisterRepository repository;
    public UserServiceImpl(ClassRegisterRepository repository) {
        this.repository = repository;
    }
    @Override
    public ClassRegister registerStudent(Integer studentId, Student student, RegistrationStatus registrationStatus) {
        if (studentId == null) throw new IllegalArgumentException("Student id is required.");
        if (student.getFirstName() == null  && student.getLastName() == null) throw new IllegalArgumentException("Both first name and last name is required.");
        if (student.getYearGroup() == null) throw new IllegalArgumentException("Year group is required.");
        if (student.getEmail() == null) throw new IllegalArgumentException("Email is required.");
        if (!student.getEmail().contains("@")) throw new IllegalArgumentException("Invalid email address.");
        if (!student.getEmail().contains(student.getLastName().toLowerCase())) throw new IllegalArgumentException("Student email must bear the last name.");
        if (student.getSubject() == null) throw new IllegalArgumentException("Subject is required.");
        if (registrationStatus == null) throw new IllegalArgumentException("Registration status is required.");
        Student studentDetails = new Student(student.getFirstName(), student.getLastName(), student.getYearGroup(), student.getSubject(), student.getEmail());
        ClassRegister registeredStudent = new ClassRegister(studentId, studentDetails, registrationStatus);
        return repository.save(registeredStudent);
    }

    @Override
    public Optional<ClassRegister> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<ClassRegister> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        // business rule: don't throw if it doesn't exist — idempotent delete
        repository.deleteById(id);
    }
}
