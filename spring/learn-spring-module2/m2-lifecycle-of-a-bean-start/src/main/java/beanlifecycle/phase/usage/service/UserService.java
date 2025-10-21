package beanlifecycle.phase.usage.service;

import beanlifecycle.phase.usage.persistance.model.User;
import beanlifecycle.phase.usage.persistance.repository.IUser;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class UserService {
    private final IUser userRepository;

    // No @Autowired needed - Spring will automatically use this constructor
    public UserService(IUser userRepository) {
        this.userRepository = userRepository; // Just assignment - no @PostConstruct needed
    }

    @PostConstruct // Use this for initialization that needs to happen after injection
    private void initializeDefaultUser() {
        User userDetails = new User();
        userDetails.setName("Joe Doe");
        userDetails.setAge(18);
        userRepository.create(userDetails);
    }
    /*
    Purpose and necessity of @PostConstruct:
    When @PostConstruct is Needed:
        1.For Initialization Logic: Only use it when you need to execute code after all dependencies are injected but before the bean is ready to use.
        2.For One-Time Setup: Such as loading initial data, establishing connections, or validating configuration.
    When It's Not Needed:
        1.If your bean only has field assignments in the constructor
        2.If you don't need any post-construction logic
     */
}