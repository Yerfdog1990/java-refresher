package springsecurity.lesson3springsecuritycustomexpressions.persistance.service;

import jakarta.annotation.security.RolesAllowed;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.dto.StudentDTO;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.Authority;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.PasswordResetToken;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.Student;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.VerificationToken;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.IStudentRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.PasswordResetTokenRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.VerificationTokenRepository;
import springsecurity.lesson3springsecuritycustomexpressions.validation.EmailExistsException;

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
        if (student.getAuthorities().isEmpty()) {
            student.getAuthorities().add(new Authority(student, "ROLE_USER"));
        } else {
            student.getAuthorities().add(new Authority(student, student.getAuthorities().stream().findFirst().get().getAuthority()));
        }

        // Save and return the entity
        return repository.save(student);
    }

    // Users with ROLE_ADMIN are allowed to edit data
    // @RolesAllowed("ROLE_ADMIN")

    // UPDATE - Owner or Admin
    @PreAuthorize("hasPermission(#student.id, 'Student', 'UPDATE')")
    @Override
    public Student updateExistingStudent(Student student) throws EmailExistsException, AccessDeniedException {
        try {
            return repository.save(student);
        } catch (AccessDeniedException e) {
            throw new AccessDeniedException("You don't have permission to edit users. Please contact your administrator.");
        }
    }

    //@PreAuthorize("hasRole('ADMIN') or @studentSecurityService.canAccessUser(authentication.principal.username, #id)")
    //@PostAuthorize("hasRole('ADMIN') or returnObject.username == authentication.name")

    // READ - Owner or Admin
    @PreAuthorize("hasPermission(#id, 'Student', 'READ')")
    @Override
    public StudentDTO findById(Long id) {
        return repository.findById(id)
                .map(StudentDTO::fromEntity)
                .orElse(null);
    }

    // @PostFilter("hasRole('ADMIN') or filterObject.email == authentication.name")

    // LIST - Only own records unless Admin
    @PostFilter("hasPermission(filterObject, 'READ')")
    @Override
    public List<StudentDTO> findAllStudents() {
        return repository.findAll().stream()
                .map(StudentDTO::fromEntity)
                .collect(Collectors.toList());
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

    // DELETE
    @PreAuthorize("hasPermission(#id, 'Student', 'DELETE')")
    @Override
    public void deleteById(Long id) {
        try{
            repository.deleteById(id);
        } catch (AccessDeniedException e){
            throw new AccessDeniedException("You don't have permission to delete users. Please contact your administrator.");
        }
    }

    // READ - No security check as it's used for authentication
    @Override
    @PostAuthorize("hasPermission(returnObject, 'READ')")
    public Student findUserByEmail(final String email) {
        return repository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(final Student student, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, student);
        passwordTokenRepository.save(myToken);
    }

    // CHANGE PASSWORD - Only the owner can change their own password
    @PreAuthorize("#student.email == authentication.principal.username or hasRole('ADMIN')")
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
