package springsecurity.lesson6ensurepasswordstrength.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import springsecurity.lesson6ensurepasswordstrength.persistance.model.Student;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
        //
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final Student student = (Student) obj;
        if (student.getPasswordConfirmation() == null) {
            return true;
        }
        return student.getPassword().equals(student.getPasswordConfirmation());
    }

}
