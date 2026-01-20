package springsecurity.lesson2datastructureofacl.exceptions;

public class DuplicateStudentException extends RuntimeException {
    public DuplicateStudentException(String message) {
        super(message);
    }
}
