package springsecurity.lesson2datastructureofacl.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springsecurity.lesson2datastructureofacl.persistence.entity.Student;
import springsecurity.lesson2datastructureofacl.persistence.repository.IStudentRepository;

@Service
public class StudentUserDetailsService implements UserDetailsService {

    private final IStudentRepository repo;

    public StudentUserDetailsService(IStudentRepository repo) {
        this.repo = repo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Student s = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        return User.withUsername(s.getEmail())
                .password("{noop}" + s.getPassword())
                .roles(s.getEmail().startsWith("bob") ? "ADMIN" : "USER")
                .build();
    }
}
