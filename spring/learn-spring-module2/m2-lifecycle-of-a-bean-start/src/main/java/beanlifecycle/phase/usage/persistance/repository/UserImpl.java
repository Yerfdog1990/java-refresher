package beanlifecycle.phase.usage.persistance.repository;

import beanlifecycle.phase.usage.persistance.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserImpl implements IUser {
    @Override
    public void create(User user) {
        String name = user.getName();
        int age = user.getAge();
        System.out.println("Creating user: " + name + ", Age: " + age);
    }
}