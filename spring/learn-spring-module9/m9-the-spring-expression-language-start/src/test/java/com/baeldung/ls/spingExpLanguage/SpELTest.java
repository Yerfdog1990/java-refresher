package com.baeldung.ls.spingExpLanguage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class SpELTest {

    @Autowired
    private SpELBeanA spelBean;

    @Test
    public void whenSpElBeanAIsInjected_thenExpressionsResolvedCorrectly() {
        assertNotNull(spelBean);
    }
}
