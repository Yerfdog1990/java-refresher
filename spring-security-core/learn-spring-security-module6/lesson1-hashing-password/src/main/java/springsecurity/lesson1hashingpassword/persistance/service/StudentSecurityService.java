package springsecurity.lesson1hashingpassword.persistance.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springsecurity.lesson1hashingpassword.persistance.model.Student;
import springsecurity.lesson1hashingpassword.persistance.repository.IStudentRepository;

@Service
public class StudentSecurityService {

    private final IStudentRepository studentRepository;

    @Autowired
    public StudentSecurityService(IStudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public boolean canAccessUser(String username, Long userId) {
        Student student = studentRepository.findByEmail(username);
        return student != null && student.getId().equals(userId);
    }
}