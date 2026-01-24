package com.example.demo.repos;

import com.example.demo.entities.MyUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author Burak Fircasiguzel < www.github.com/burakfircasiguzel >
 */
public interface UserRepository extends JpaRepository<MyUser, Long> {

    MyUser findByUsername(String userName);

    MyUser findByUsernameAndPassword(String userName, String password);

}
