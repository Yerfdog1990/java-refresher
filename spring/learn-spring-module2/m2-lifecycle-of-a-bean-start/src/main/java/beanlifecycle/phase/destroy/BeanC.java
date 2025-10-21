package beanlifecycle.phase.destroy;

import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class BeanC {

    @PreDestroy
    public void preDestroy() {
        System.out.println("@PreDestroy: BeanC is about to be destroyed.");
    }

    public void destroy() {
        System.out.println("Custom destroy method called.");
    }
}
