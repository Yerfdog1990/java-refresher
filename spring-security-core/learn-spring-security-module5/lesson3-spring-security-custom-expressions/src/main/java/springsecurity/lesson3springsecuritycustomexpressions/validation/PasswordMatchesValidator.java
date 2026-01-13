package springsecurity.lesson3springsecuritycustomexpressions.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import springsecurity.lesson3springsecuritycustomexpressions.persistance.model.MyUser;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
        //
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final MyUser myUser = (MyUser) obj;
        boolean isValid = true;
        if (myUser.getPasswordConfirmation() != null && !myUser.getPassword().equals(myUser.getPasswordConfirmation())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords do not match")
                    .addPropertyNode("passwordConfirmation")
                    .addConstraintViolation();
            isValid = false;
        }
        if (myUser.getEmail() != null && myUser.getPassword() != null) {
            String email = myUser.getEmail().toLowerCase();
            String password = myUser.getPassword().toLowerCase();
            String localPart = email.split("@")[0];
            if (password.contains(email) || password.contains(localPart)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("Your password cannot contain your email")
                        .addPropertyNode("password")
                        .addConstraintViolation();
                isValid = false;
            }
        }
        return isValid;
    }

}
