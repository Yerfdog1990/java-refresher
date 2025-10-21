package beanlifecycle.phase.usage;

import beanlifecycle.phase.usage.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UserApp {
    public static void main(String[] args) {
        SpringApplication.run(UserApp.class, args);
    }

//    @Bean
//    public CommandLineRunner demo(UserService userService) {
//        return args -> {
//            // This will trigger the initializeDefaultUser() method
//            System.out.println("Application started - UserService is working!");
//        };
//    }
}
