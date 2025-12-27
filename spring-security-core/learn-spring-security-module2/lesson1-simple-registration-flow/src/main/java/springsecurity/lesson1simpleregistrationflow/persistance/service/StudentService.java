package springsecurity.lesson1simpleregistrationflow.persistance.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springsecurity.lesson1simpleregistrationflow.persistance.model.Student;
import springsecurity.lesson1simpleregistrationflow.persistance.repository.IStudentRepository;
import springsecurity.lesson1simpleregistrationflow.validation.EmailExistsException;

@Service
@Transactional
public class StudentService implements IStudentService {

    private final IStudentRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public StudentService(IStudentRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Student registerNewStudent(Student student) throws EmailExistsException {
        if (emailExist(student.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + student.getEmail());
        }
        student.setPassword(passwordEncoder.encode(student.getPassword()));
        return repository.save(student);
    }

    @Override
    public Student updateExistingStudent(Student student) throws EmailExistsException {
        return repository.save(student);
    }

    @Override
    public Iterable<Student> findAll() {
        return repository.findAll();
    }

    private boolean emailExist(String email) {
        return repository.findByEmail(email) != null;
    }
}
