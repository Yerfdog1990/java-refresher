package springsecurity.lesson1rememberme.persistance.service;

import springsecurity.lesson1rememberme.persistance.model.PasswordResetToken;
import springsecurity.lesson1rememberme.persistance.model.Student;
import springsecurity.lesson1rememberme.persistance.model.VerificationToken;
import springsecurity.lesson1rememberme.validation.EmailExistsException;

public interface IStudentService {

    Student registerNewStudent(Student student) throws EmailExistsException;

    Student updateExistingStudent(Student student) throws EmailExistsException;

    Student findById(Long id);

    Iterable<Student> findAll();

    void createVerificationTokenForUser(Student student, String token);

    VerificationToken getVerificationToken(String token);

    void enableRegisteredStudent(Student student);
    
    void deleteById(Long id);

    Student findUserByEmail(String email);

    void createPasswordResetTokenForUser(final Student student, final String token);

    PasswordResetToken getPasswordResetToken(final String token);

    void changeUserPassword(final Student student, final String password);
}
