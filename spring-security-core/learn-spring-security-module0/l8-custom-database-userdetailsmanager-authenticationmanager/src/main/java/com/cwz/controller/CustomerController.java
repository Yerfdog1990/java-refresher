package com.cwz.controller;

import com.cwz.model.Customer;
import com.cwz.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class CustomerController {

    CustomerRepository customerRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    CustomerController(CustomerRepository customerRepository,PasswordEncoder passwordEncoder){
        this.customerRepository=customerRepository;
        this.passwordEncoder=passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody Customer customer){

        try{
            String hashedPassword=passwordEncoder.encode(customer.getPwd());
            customer.setPwd(hashedPassword);
            Customer savedCustomer=customerRepository.save(customer);

            if(savedCustomer.getId()>0){
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body("Given customer details are stored successfully");
            }
            else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Customer Registration failed");
            }
        }
        catch (Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An exception occurred "+ex.getMessage());


        }
    }

    @GetMapping("/user")
    public Customer getCustomerDetailsAfterLogin(Authentication authentication){
        Optional<Customer> optionalCustomer=customerRepository.findCustomerByEmail(authentication.getName());
        return  optionalCustomer.orElse(null);
    }

}
