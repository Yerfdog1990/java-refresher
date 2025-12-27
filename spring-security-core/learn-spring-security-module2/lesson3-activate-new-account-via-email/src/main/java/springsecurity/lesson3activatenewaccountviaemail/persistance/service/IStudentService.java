package springsecurity.lesson3activatenewaccountviaemail.persistance.service;

import springsecurity.lesson3activatenewaccountviaemail.persistance.model.Student;
import springsecurity.lesson3activatenewaccountviaemail.persistance.model.VerificationToken;
import springsecurity.lesson3activatenewaccountviaemail.validation.EmailExistsException;

public interface IStudentService {

    Student registerNewStudent(Student student) throws EmailExistsException;

    Student updateExistingStudent(Student student) throws EmailExistsException;

    Student findById(Long id);

    Iterable<Student> findAll();

    void createVerificationTokenForUser(Student student, String token);

    VerificationToken getVerificationToken(String token);

    void enableRegisteredStudent(Student student);
    
    void deleteById(Long id);
}
