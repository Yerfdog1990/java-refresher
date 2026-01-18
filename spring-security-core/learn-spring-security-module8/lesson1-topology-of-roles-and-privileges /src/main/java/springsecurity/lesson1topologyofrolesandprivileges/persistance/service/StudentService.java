package springsecurity.lesson1topologyofrolesandprivileges.persistance.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.dto.StudentDTO;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Role;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.PasswordResetToken;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Student;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.VerificationToken;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.repository.IStudentRepository;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.repository.PasswordResetTokenRepository;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.repository.VerificationTokenRepository;
import springsecurity.lesson1topologyofrolesandprivileges.validation.EmailExistsException;

import java.util.List;
import java.util.stream.Collectors;

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

    @PreAuthorize("hasRole('USER')")
    @Override
    public Student registerNewStudent(StudentDTO studentDTO) throws EmailExistsException {
        if (emailExist(studentDTO.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + studentDTO.getEmail());
        }

        // Convert DTO to entity
        Student student = studentDTO.toEntity();

        // Encode the password
        student.setPassword(passwordEncoder.encode(student.getPassword()));

        // Set the default role if no authorities are set
        if (student.getRoles().isEmpty()) {
            student.getRoles().add(new Role("ROLE_USER"));
        }

        // Save and return the entity
        return repository.save(student);
    }

    // Users with WRITE_PRIVILEGE are allowed to edit data
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public Student updateExistingStudent(Student student) {
        return repository.save(student);
    }

    @PreAuthorize("hasRole('USER')")
    @Override
    public StudentDTO findById(Long id) {
        return repository.findById(id)
                .map(StudentDTO::fromEntity)
                .orElse(null);
    }

    @PostFilter("hasRole('USER') or filterObject.email == authentication.name")
    @Override
    public List<StudentDTO> findAllStudents() {
        return repository.findAll().stream()
                .map(StudentDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public void deleteById(Long id) {
        try{
            repository.deleteById(id);
        } catch (AccessDeniedException e){
            throw new AccessDeniedException("You don't have permission to delete users. Please contact your administrator.");
        }
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
        student.setPassword(passwordEncoder.encode(password));
        repository.save(student);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }
}
