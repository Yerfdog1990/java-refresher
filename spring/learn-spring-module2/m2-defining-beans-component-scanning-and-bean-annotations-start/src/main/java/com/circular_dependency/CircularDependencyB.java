package com.circular_dependency;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CircularDependencyB implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;
    //@Autowired
    private  CircularDependencyA circularDependencyA;

    public CircularDependencyA getCircularDependencyA() {
        return circularDependencyA;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        circularDependencyA = applicationContext.getBean(CircularDependencyA.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        applicationContext = ctx;
    }

    //    @PostConstruct
//    public void initialize(){
//        circularDependencyA.setCircularDependencyB(this);
//    }

//    @Autowired
//    public void setCircularDependencyA(CircularDependencyA circularDependencyA) {
//        this.circularDependencyA = circularDependencyA;
//    }
}
