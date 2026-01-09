package springsecurity.lesson1springsecuritywithjsp.validation;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String message) {
        super(message);
    }
}
