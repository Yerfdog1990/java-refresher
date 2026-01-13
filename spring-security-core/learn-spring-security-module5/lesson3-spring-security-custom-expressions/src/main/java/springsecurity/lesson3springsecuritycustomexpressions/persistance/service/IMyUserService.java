package springsecurity.lesson3springsecuritycustomexpressions.persistance.service;

import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.PasswordResetToken;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.VerificationToken;
import springsecurity.lesson3springsecuritycustomexpressions.validation.EmailExistsException;

public interface IMyUserService {

    MyUser registerNewMyUser(MyUser myUser) throws EmailExistsException;

    MyUser updateExistingMyUser(MyUser myUser) throws EmailExistsException;

    MyUser findById(Long id);

    Iterable<MyUser> findAll();

    void createVerificationTokenForUser(MyUser myUser, String token);

    VerificationToken getVerificationToken(String token);

    void enableRegisteredMyUser(MyUser myUser);
    
    void deleteById(Long id);

    MyUser findUserByEmail(String email);

    void createPasswordResetTokenForUser(final MyUser myUser, final String token);

    PasswordResetToken getPasswordResetToken(final String token);

    void changeUserPassword(final MyUser myUser, final String password);
}
