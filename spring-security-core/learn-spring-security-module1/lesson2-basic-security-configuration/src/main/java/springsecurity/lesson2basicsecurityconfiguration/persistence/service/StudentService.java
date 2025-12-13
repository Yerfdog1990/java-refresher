package springsecurity.lesson2basicsecurityconfiguration.persistence.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springsecurity.lesson2basicsecurityconfiguration.exceptions.DuplicateStudentException;
import springsecurity.lesson2basicsecurityconfiguration.persistence.entity.Student;
import springsecurity.lesson2basicsecurityconfiguration.persistence.repository.IStudentRepository;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {
    private final IStudentRepository repository;

    @Autowired
    public StudentService(IStudentRepository repository) {
        this.repository = repository;
    }

    public Student create(Student student) {
        // Pre-check for uniqueness of email and password
        boolean emailExists = student.getEmail() != null && repository.findByEmail(student.getEmail()).isPresent();
        boolean passwordExists = student.getPassword() != null && repository.findByPassword(student.getPassword()).isPresent();

        if (emailExists && passwordExists) {
            throw new DuplicateStudentException("A user with that email and password exists.");
        } else if (emailExists) {
            throw new DuplicateStudentException("A user with that email exists.");
        } else if (passwordExists) {
            throw new DuplicateStudentException("A user with that password exists.");
        }
        return repository.create(student);
    }

    public Optional<Student> findById(Long id) {
        return repository.findStudentById(id);
    }

    public List<Student> findAll() {
        return repository.findAll();
    }

    public Student modify(Student student) {
        return repository.modify(student);
    }

    public void deleteById(Long id) {
        repository.deleteStudentById(id);
    }
}
