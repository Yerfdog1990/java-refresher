package springsecurity.lesson4forgotpassword.validation;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String message) {
        super(message);
    }
}
