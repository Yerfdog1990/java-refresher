package com.baeldung.ls;

import com.baeldung.ls.persistence.model.Project;
import com.baeldung.ls.persistence.repository.IProjectRepository;
import com.baeldung.ls.service.IProjectService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest
public class SpringContextIntegrationTest {

    @Autowired
    private IProjectRepository repository;
    @Test
    public void whenContextIsLoaded_thenNoExceptions() {
    }

    @Test
    public void givenAnExistingDatabase_WhenSavingNewProject_ThenSuccess(){
        Project project = new Project(1L, "Project 1", LocalDate.now());
        assertThat(repository.save(project), is(notNullValue()));

    }

    @Test
    public void givenAnExistingDatabase_whenFindById_thenSuccess(){
        Project project = new Project(1L, "Project 1", LocalDate.now());
        repository.save(project);
        Optional<Project> retrievedProject = repository.findById(1L);
        assertThat(retrievedProject, is(notNullValue()));

    }
}