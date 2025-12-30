package springsecurity.lesson3remembermewithpersistence.persistance.service;

import springsecurity.lesson3remembermewithpersistence.persistance.model.PasswordResetToken;
import springsecurity.lesson3remembermewithpersistence.persistance.model.Student;
import springsecurity.lesson3remembermewithpersistence.persistance.model.VerificationToken;
import springsecurity.lesson3remembermewithpersistence.validation.EmailExistsException;

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
