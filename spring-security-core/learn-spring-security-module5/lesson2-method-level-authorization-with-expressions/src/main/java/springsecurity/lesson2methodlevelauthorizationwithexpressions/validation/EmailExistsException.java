package springsecurity.lesson2methodlevelauthorizationwithexpressions.validation;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String message) {
        super(message);
    }
}
