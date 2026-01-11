package springsecurity.lesson2methodlevelauthorizationwithexpressions.persistance.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springsecurity.lesson2methodlevelauthorizationwithexpressions.persistance.model.Student;
import springsecurity.lesson2methodlevelauthorizationwithexpressions.persistance.repository.IStudentRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentDetailsService implements UserDetailsService {

    private final IStudentRepository studentRepository;

    @Autowired
    public StudentDetailsService(IStudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final Student student = studentRepository.findByEmail(email);
        if (student == null) {
            throw new UsernameNotFoundException("No user found with username: " + email);
        }
        return new User(student.getEmail(), student.getPassword(), student.isEnabled(), true, true, true, getAuthorities(student));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(Student student) {
        return student.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .collect(Collectors.toList());
    }

}
