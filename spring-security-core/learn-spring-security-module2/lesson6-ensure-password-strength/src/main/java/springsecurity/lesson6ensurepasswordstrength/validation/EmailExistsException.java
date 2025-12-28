package springsecurity.lesson6ensurepasswordstrength.validation;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String message) {
        super(message);
    }
}
