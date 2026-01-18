package springsecurity.lesson1topologyofrolesandprivileges.persistance.service;

import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Privilege;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Role;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Student;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.repository.IStudentRepository;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    public UserDetails loadUserByUsername(final @NonNull String email) throws UsernameNotFoundException {
        final Student student = studentRepository.findByEmail(email);
        if (student == null) {
            throw new UsernameNotFoundException("No user found with username: " + email);
        }
        return new User(student.getEmail(), student.getPassword(), student.isEnabled(), true, true, true, getAuthorities(student.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getAuthorities(final Collection<Role> roles) {
        Set<GrantedAuthority> authorities = new HashSet<>();

        for (Role role : roles) {
            // Add the role itself
            authorities.add(new SimpleGrantedAuthority(role.getRole()));

            // Add privileges of the role
            for(Privilege privilege : role.getPrivileges()) {
                authorities.add(new SimpleGrantedAuthority(privilege.getPrivilege()));
            }
        }
        return authorities;
    }
}
