package springsecurity.lesson3remembermewithpersistence.validation;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String message) {
        super(message);
    }
}
