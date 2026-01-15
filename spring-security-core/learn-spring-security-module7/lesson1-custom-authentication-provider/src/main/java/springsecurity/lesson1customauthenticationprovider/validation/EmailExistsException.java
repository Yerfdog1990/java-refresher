package springsecurity.lesson1customauthenticationprovider.validation;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String message) {
        super(message);
    }
}
