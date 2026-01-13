package springsecurity.lesson3springsecuritycustomexpressions.persistance.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.Authority;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.PasswordResetToken;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.VerificationToken;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.MyUserRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.PasswordResetTokenRepository;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.repository.VerificationTokenRepository;
import springsecurity.lesson3springsecuritycustomexpressions.validation.EmailExistsException;

@Service
@Transactional
public class MyUserService implements IMyUserService {

    private final MyUserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordResetTokenRepository passwordTokenRepository;

    @Autowired
    public MyUserService(MyUserRepository repository, PasswordEncoder passwordEncoder, VerificationTokenRepository verificationTokenRepository, PasswordResetTokenRepository passwordTokenRepository) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenRepository = verificationTokenRepository;
        this.passwordTokenRepository = passwordTokenRepository;
    }

    @Override
    public MyUser registerNewMyUser(MyUser myUser) throws EmailExistsException {
        if (emailExist(myUser.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + myUser.getEmail());
        }
        myUser.setPassword(passwordEncoder.encode(myUser.getPassword()));
        if (myUser.getAuthorities().isEmpty()) {
            myUser.getAuthorities().add(new Authority(myUser, "ROLE_USER"));
        }
        return repository.save(myUser);
    }

    @Override
    public MyUser updateExistingMyUser(MyUser myUser) throws EmailExistsException {
        return repository.save(myUser);
    }

    @Override
    public MyUser findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public Iterable<MyUser> findAll() {
        return repository.findAll();
    }

    private boolean emailExist(String email) {
        return repository.findByEmail(email) != null;
    }

    @Override
    public void createVerificationTokenForUser(final MyUser myUser, final String token) {
        final VerificationToken myToken = new VerificationToken(token, myUser);
        verificationTokenRepository.save(myToken);
    }

    @Override
    public VerificationToken getVerificationToken(final String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public void enableRegisteredMyUser(MyUser myUser) {
        myUser.setEnabled(true);
        repository.save(myUser);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public MyUser findUserByEmail(final String email) {
        return repository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(final MyUser myUser, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, myUser);
        passwordTokenRepository.save(myToken);
    }

    @Override
    public void changeUserPassword(final MyUser myUser, final String password) {
        myUser.setPassword(passwordEncoder.encode(password));
        repository.save(myUser);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }
}
