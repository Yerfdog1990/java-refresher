package springsecurity.lesson1simpleregistrationflow.persistance.service;

import springsecurity.lesson1simpleregistrationflow.persistance.model.Student;
import springsecurity.lesson1simpleregistrationflow.validation.EmailExistsException;

public interface IStudentService {

    Student registerNewStudent(Student student) throws EmailExistsException;

    Student updateExistingStudent(Student student) throws EmailExistsException;

    Iterable<Student> findAll();
}
