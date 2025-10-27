package com.baeldung.ls.persistence.repository;

import com.baeldung.ls.persistence.model.Project;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class ProjectRepositoryIntegrationTest {

    @Autowired
    IProjectRepository projectRepository;

    @Test
    public void givenDataCreated_whenFindAllPaginated_thenSuccess() {
        Page<Project> retrievedProjects = projectRepository.findAll(PageRequest.of(0, 10));
        assertThat(retrievedProjects.getContent(), hasSize(6));
    }

    @Test
    public void givenDataCreated_whenFindAllAllSorted_thenSuccess() {
        Sort ascendingSort = Sort.by(Sort.Order.asc("name"));
        List<Project> retrievedProjects = projectRepository.findAll(ascendingSort);

        List<Project> sortedProjects = new ArrayList<>(retrievedProjects);

        sortedProjects.sort(Comparator.comparing(Project::getName));
        assertEquals(sortedProjects,  retrievedProjects);
    }

    @Test
    public void givenDataCreated_whenFindAllAndSorted_thenSuccess() {
        Sort ascendingSort = Sort.by(Sort.Order.asc("name"));
        PageRequest pageRequest = PageRequest.of(0, 10, ascendingSort);
        List<Project> retrievedProjects = projectRepository.findAll(pageRequest).getContent();

        assertThat(retrievedProjects, hasSize(6));
    }
}