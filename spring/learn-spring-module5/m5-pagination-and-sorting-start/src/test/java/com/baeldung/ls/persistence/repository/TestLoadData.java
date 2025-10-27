package com.baeldung.ls.persistence.repository;

import com.baeldung.ls.persistence.model.Project;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

@Component
public class TestLoadData implements ApplicationContextAware {
    @Autowired
    IProjectRepository repository;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        repository.save(new Project(randomAlphabetic(6), LocalDate.now()));
        repository.save(new Project(randomAlphabetic(6), LocalDate.now()));
        repository.save(new Project(randomAlphabetic(6), LocalDate.now()));
        repository.save(new Project(randomAlphabetic(6), LocalDate.now()));
        repository.save(new Project(randomAlphabetic(6), LocalDate.now()));
        repository.save(new Project(randomAlphabetic(6), LocalDate.now()));
    }
}
