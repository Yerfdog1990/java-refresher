package springsecurity.lesson2methodlevelauthorizationwithexpressions.persistance.service;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;
import springsecurity.lesson2methodlevelauthorizationwithexpressions.persistance.dto.StudentDTO;
import springsecurity.lesson2methodlevelauthorizationwithexpressions.persistance.model.PasswordResetToken;
import springsecurity.lesson2methodlevelauthorizationwithexpressions.persistance.model.Student;
import springsecurity.lesson2methodlevelauthorizationwithexpressions.persistance.model.VerificationToken;
import springsecurity.lesson2methodlevelauthorizationwithexpressions.validation.EmailExistsException;

import java.util.List;

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
    StudentDTO findById(Long id);
    List<StudentDTO> findAllStudents();
}
