package springsecurity.lesson2datastructureofacl.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springsecurity.lesson2datastructureofacl.exceptions.DuplicateStudentException;
import springsecurity.lesson2datastructureofacl.persistence.entity.Student;
import springsecurity.lesson2datastructureofacl.persistence.repository.IStudentRepository;

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
        // Pre-check for uniqueness of email
        boolean emailExists = student.getEmail() != null && repository.findByEmail(student.getEmail()).isPresent();

        if (emailExists) {
            throw new DuplicateStudentException("A user with that email exists.");
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
