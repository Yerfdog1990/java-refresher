package springsecurity.lesson3springsecuritycustomexpressions.persistance.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.MyUserRepository;

@Service
@Transactional
public class MyUserDetailsService implements UserDetailsService {

    private final MyUserRepository myUserRepository;

    @Autowired
    public MyUserDetailsService(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
        final MyUser myUser = myUserRepository.findByEmail(email);
        if (myUser == null) {
            throw new UsernameNotFoundException("No user found with email: " + email);
        }
        return new MyUserPrincipal(myUser);
    }

}
