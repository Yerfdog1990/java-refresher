package com.baeldung.rwsb.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.net.URI;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IdMismatchException extends ErrorResponseException {

//    private static final long serialVersionUID = 6270729894048566429L;
//
//    public IdMismatchException(String s) {
//        super(s);
//    }

    public IdMismatchException(String message) {
        super(HttpStatus.BAD_REQUEST);
        super.setType(URI.create("https://example.com/errors/id-mismatch-exception"));
        super.setTitle("Path param and body ids didn't match");
        super.setDetail(message);
    }
}