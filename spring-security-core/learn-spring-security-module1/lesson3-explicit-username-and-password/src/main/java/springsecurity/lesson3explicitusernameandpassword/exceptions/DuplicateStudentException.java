package springsecurity.lesson3explicitusernameandpassword.exceptions;

public class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException(String message) {
        super(message);
    }
}
