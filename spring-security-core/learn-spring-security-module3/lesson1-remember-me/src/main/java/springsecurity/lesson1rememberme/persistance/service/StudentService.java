package springsecurity.lesson1rememberme.persistance.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springsecurity.lesson1rememberme.persistance.model.PasswordResetToken;
import springsecurity.lesson1rememberme.persistance.model.Student;
import springsecurity.lesson1rememberme.persistance.model.VerificationToken;
import springsecurity.lesson1rememberme.persistance.repository.IStudentRepository;
import springsecurity.lesson1rememberme.persistance.repository.PasswordResetTokenRepository;
import springsecurity.lesson1rememberme.persistance.repository.VerificationTokenRepository;
import springsecurity.lesson1rememberme.validation.EmailExistsException;

@Service
@Transactional
public class StudentService implements IStudentService {

    private final IStudentRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordTokenRepository;

    @Autowired
    public StudentService(IStudentRepository repository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository, PasswordResetTokenRepository passwordTokenRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordTokenRepository = passwordTokenRepository;
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

    @Override
    public Student findUserByEmail(final String email) {
        return repository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(final Student student, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, student);
        passwordTokenRepository.save(myToken);
    }

    @Override
    public void changeUserPassword(final Student student, final String password) {
        student.setPassword(password);
        repository.save(student);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }
}
