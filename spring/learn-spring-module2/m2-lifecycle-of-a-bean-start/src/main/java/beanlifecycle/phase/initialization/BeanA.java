package beanlifecycle.phase.initialization;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class BeanA {

    @PostConstruct
    public void post() {
        System.out.println("@PostConstruct: BeanA has been initialized.");
    }

    public void doSomething() {
        System.out.println("BeanA is doing some work.");
    }
}
