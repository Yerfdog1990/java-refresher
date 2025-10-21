package com.circular_dependency;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class CircularDependencyA {
    private CircularDependencyB circularDependencyB;
    private String message = "Hello World";

    public CircularDependencyB getCircularDependencyA() {
        return circularDependencyB;
    }

    @Autowired
    public void setCircularDependencyB(CircularDependencyB circularDependencyB) {
        this.circularDependencyB = circularDependencyB;
    }

    public String getMessage() {
        return message;
    }
}
