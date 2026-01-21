package springsecurity.lesson2datastructureofacl.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasRole('ADMIN')")
    public Student create(Student student) {
        // Pre-check for uniqueness of email
        boolean emailExists = student.getEmail() != null && repository.findByEmail(student.getEmail()).isPresent();

        if (emailExists) {
            throw new DuplicateStudentException("A user with that email exists.");
        }
        return repository.save(student);
    }

    @PostAuthorize("hasPermission(returnObject.orElse(null), 'READ') or hasRole('ADMIN')")
    public Optional<Student> findById(Long id) {
        return repository.findById(id);
    }

    @PostFilter("hasPermission(filterObject, 'READ') or hasRole('ADMIN')")
    public List<Student> findAll() {
        return repository.findAll();
    }

    @PreAuthorize("hasPermission(#student, 'WRITE') or hasRole('ADMIN')")
    public Student modify(Student student) {
        return repository.save(student);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
