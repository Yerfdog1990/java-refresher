package com.example.demo.repos.responses;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Burak Fircasiguzel < www.github.com/burakfircasiguzel >
 */
@Setter
@Getter
public class StandardResponse {
    private String message;

    public StandardResponse() {}

    public StandardResponse(String message) {
        this.message = message;
    }

}
