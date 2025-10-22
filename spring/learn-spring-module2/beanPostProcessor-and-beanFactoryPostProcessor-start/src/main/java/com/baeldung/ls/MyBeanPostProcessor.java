package com.baeldung.ls;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

@Component

public class MyBeanPostProcessor implements BeanFactoryPostProcessor, Ordered {
    Logger LOG = LoggerFactory.getLogger(MyBeanPostProcessor.class);
//    @Nullable
//    @Override
//    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        LOG.info("Before initialising the bean: {}", beanName);
//        return bean;
//    }
//
//    @Nullable
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        LOG.info("After initialising the bean: {}", beanName);
//        return bean;
//    }


    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        BeanDefinition beanDefinition = beanFactory.getBeanDefinition("beanA");
        beanDefinition.getPropertyValues().add("foo", "bar");
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
