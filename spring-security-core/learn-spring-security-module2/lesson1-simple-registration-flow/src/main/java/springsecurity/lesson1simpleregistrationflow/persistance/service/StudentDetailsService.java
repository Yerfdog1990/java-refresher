package springsecurity.lesson1simpleregistrationflow.persistance.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springsecurity.lesson1simpleregistrationflow.persistance.model.Student;
import springsecurity.lesson1simpleregistrationflow.persistance.repository.IStudentRepository;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
public class StudentDetailsService implements UserDetailsService {

    private static final String ROLE_USER = "ROLE_USER";

    private final IStudentRepository studentRepository;

    @Autowired
    public StudentDetailsService(IStudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final Student student = studentRepository.findByEmail(email);
        if (student == null) {
            throw new UsernameNotFoundException("No user found with username: " + email);
        }
        return new User(student.getEmail(), student.getPassword(), true, true, true, true, getAuthorities(ROLE_USER));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return List.of(new SimpleGrantedAuthority(role));
    }

}
