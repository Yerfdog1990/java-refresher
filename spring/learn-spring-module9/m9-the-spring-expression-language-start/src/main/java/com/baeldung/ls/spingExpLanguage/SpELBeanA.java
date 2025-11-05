package com.baeldung.ls.spingExpLanguage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
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

    public static void main(String[] args) {
        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("'Hello' + 'world'.concat('!!!!')");
        System.out.println(expression.getValue(String.class));
    }
}
