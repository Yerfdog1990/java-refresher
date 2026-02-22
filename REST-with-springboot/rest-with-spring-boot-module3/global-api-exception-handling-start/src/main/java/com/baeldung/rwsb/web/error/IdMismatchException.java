package com.baeldung.rwsb.web.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IdMismatchException extends IllegalArgumentException {

    public IdMismatchException() {
        super("ids didn't match");
    }

    public IdMismatchException(String s) {
        super(s);
    }
}
