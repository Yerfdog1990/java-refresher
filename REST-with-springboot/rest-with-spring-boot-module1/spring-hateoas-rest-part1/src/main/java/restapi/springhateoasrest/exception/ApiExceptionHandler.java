package restapi.springhateoasrest.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import restapi.springhateoasrest.exception.AccountNotFoundException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<Void> handleAccountNotFound(AccountNotFoundException ex) {
        return ResponseEntity.notFound().build();
    }
}

