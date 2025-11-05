package com.baeldung.ls.spingExpLanguage;

import org.springframework.stereotype.Component;

@Component
public class SpELBeanB {
    private Integer prop1 = 10;

    public Integer getProp1() {
        return prop1;
    }
}
