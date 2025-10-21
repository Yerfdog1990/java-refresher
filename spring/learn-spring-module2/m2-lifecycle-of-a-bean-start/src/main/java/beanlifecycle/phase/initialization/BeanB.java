package beanlifecycle.phase.initialization;

public class BeanB {
    public void initialize() {
        System.out.println("Custom initializer is called for BeanB.");
    }
}
