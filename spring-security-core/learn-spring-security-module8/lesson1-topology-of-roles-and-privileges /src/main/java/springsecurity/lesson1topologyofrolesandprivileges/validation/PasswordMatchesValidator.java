package springsecurity.lesson1topologyofrolesandprivileges.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import springsecurity.lesson1topologyofrolesandprivileges.persistance.model.Student;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
        //
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final Student student = (Student) obj;
        boolean isValid = true;
        if (student.getPasswordConfirmation() != null && !student.getPassword().equals(student.getPasswordConfirmation())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Passwords do not match")
                    .addPropertyNode("passwordConfirmation")
                    .addConstraintViolation();
            isValid = false;
        }
        if (student.getEmail() != null && student.getPassword() != null) {
            String email = student.getEmail().toLowerCase();
            String password = student.getPassword().toLowerCase();
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
