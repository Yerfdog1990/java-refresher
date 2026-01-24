package com.example.demo.requests;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Burak Fircasiguzel < www.github.com/burakfircasiguzel >
 */
@Setter
@Getter
public class SignOTPRequest {
    private String username;
    private String code;

}
