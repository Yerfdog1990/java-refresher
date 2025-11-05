package com.baeldung.ls.spingExpLanguage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpELBeanA {
    @Value("#{2+3}")
    private Integer add;

    @Value("#{'Hello' + 'world!'}")
    private String stringConcatenation;

    @Value("#{2==2}")
    private boolean equal;

    @Value("#{spELBeanB.prop1}")
    private String otherBeanProperty;
}
