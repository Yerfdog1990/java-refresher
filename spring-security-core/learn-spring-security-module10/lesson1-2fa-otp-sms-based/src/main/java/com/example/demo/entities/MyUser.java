package com.example.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Burak Fircasiguzel < www.github.com/burakfircasiguzel >
 */
@Setter
@Getter
@Entity
@Table(name = "myUser")
public class MyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    @JsonIgnore
    private String password;
    private String telephoneNumber;

}
