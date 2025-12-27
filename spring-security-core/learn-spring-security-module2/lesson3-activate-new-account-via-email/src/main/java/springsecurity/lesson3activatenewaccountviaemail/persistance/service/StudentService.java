package springsecurity.lesson3activatenewaccountviaemail.persistance.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springsecurity.lesson3activatenewaccountviaemail.persistance.model.Student;
import springsecurity.lesson3activatenewaccountviaemail.persistance.model.VerificationToken;
import springsecurity.lesson3activatenewaccountviaemail.persistance.repository.IStudentRepository;
import springsecurity.lesson3activatenewaccountviaemail.persistance.repository.VerificationTokenRepository;
import springsecurity.lesson3activatenewaccountviaemail.validation.EmailExistsException;

@Service
@Transactional
public class StudentService implements IStudentService {

    private final IStudentRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;

    @Autowired
    public StudentService(IStudentRepository repository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
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
    public Student findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Iterable<Student> findAll() {
        return repository.findAll();
    }

    private boolean emailExist(String email) {
        return repository.findByEmail(email) != null;
    }

    @Override
    public void createVerificationTokenForUser(final Student student, final String token) {
        final VerificationToken myToken = new VerificationToken(token, student);
        verificationTokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(final String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public void enableRegisteredStudent(Student student) {
        student.setEnabled(true);
        repository.save(student);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
