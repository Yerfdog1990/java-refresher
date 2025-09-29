package junit;

import junit.controller.UserController;
import junit.model.repository.ClassRegisterRepository;
import junit.model.repository.InMemoryClassRegisterRepository;
import junit.model.service.UserService;
import junit.model.service.UserServiceImpl;

public class MainApplication {
    public static void main(String[] args) {
        ClassRegisterRepository registerRepository = new InMemoryClassRegisterRepository();
        UserService userService = new UserServiceImpl(registerRepository);
        UserController userController = new UserController(userService);

        // Run the program
        userController.run();
    }
}
