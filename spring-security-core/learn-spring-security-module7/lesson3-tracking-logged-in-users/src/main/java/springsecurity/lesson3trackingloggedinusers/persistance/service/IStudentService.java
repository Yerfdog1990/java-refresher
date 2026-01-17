package springsecurity.lesson3trackingloggedinusers.persistance.service;

import springsecurity.lesson3trackingloggedinusers.persistance.dto.StudentDTO;
import springsecurity.lesson3trackingloggedinusers.persistance.model.PasswordResetToken;
import springsecurity.lesson3trackingloggedinusers.persistance.model.Student;
import springsecurity.lesson3trackingloggedinusers.persistance.model.VerificationToken;
import springsecurity.lesson3trackingloggedinusers.validation.EmailExistsException;

import java.util.List;
import java.util.Optional;

public interface IStudentService {

    Student updateExistingStudent(Student student) throws EmailExistsException;

    void createVerificationTokenForUser(Student student, String token);

    VerificationToken getVerificationToken(String token);

    void enableRegisteredStudent(Student student);

    void deleteById(Long id);

    Student findUserByEmail(String email);

    void createPasswordResetTokenForUser(final Student student, final String token);

    PasswordResetToken getPasswordResetToken(final String token);

    void changeUserPassword(final Student student, final String password);

    Student registerNewStudent(StudentDTO studentDTO) throws EmailExistsException;

    Optional<StudentDTO> findById(Long id);

    List<StudentDTO> findAllStudents();
}
