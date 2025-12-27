package springsecurity.lesson1simpleregistrationflow.validation;


import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import springsecurity.lesson1simpleregistrationflow.persistance.model.Student;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object> {

    @Override
    public void initialize(final PasswordMatches constraintAnnotation) {
        //
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final Student student = (Student) obj;
        return student.getPassword().equals(student.getPasswordConfirmation());
    }

}
