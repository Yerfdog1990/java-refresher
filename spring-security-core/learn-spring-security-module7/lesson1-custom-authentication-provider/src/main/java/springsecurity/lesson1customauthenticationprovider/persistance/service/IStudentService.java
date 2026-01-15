package springsecurity.lesson1customauthenticationprovider.persistance.service;

import springsecurity.lesson1customauthenticationprovider.persistance.dto.StudentDTO;
import springsecurity.lesson1customauthenticationprovider.persistance.model.PasswordResetToken;
import springsecurity.lesson1customauthenticationprovider.persistance.model.Student;
import springsecurity.lesson1customauthenticationprovider.persistance.model.VerificationToken;
import springsecurity.lesson1customauthenticationprovider.validation.EmailExistsException;

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
