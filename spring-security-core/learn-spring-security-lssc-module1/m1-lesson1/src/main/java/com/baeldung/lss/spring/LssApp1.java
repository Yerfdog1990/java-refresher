package com.baeldung.lss.spring;

import com.baeldung.lss.persistence.InMemoryUserRepository;
import com.baeldung.lss.persistence.UserRepository;
import com.baeldung.lss.web.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.convert.converter.Converter;

@SpringBootApplication
@ComponentScan("com.baeldung.lss.web")
public class LssApp1 {

    @Bean
    public UserRepository userRepository() {
        return new InMemoryUserRepository();
    }

    @Bean
    public Converter<String, User> messageConverter(UserRepository userRepository) {
        return id -> userRepository.findUser(Long.valueOf(id));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LssApp1.class, args);
    }

}
