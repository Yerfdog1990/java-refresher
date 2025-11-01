package com.baeldung.ls.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.baeldung.ls.persistence.model.Project;
import com.baeldung.ls.service.IProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;


//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@WebMvcTest
public class ProjectControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProjectService projectService;
    @Test
    public void whenEnvironmentIsInstantiated_thenOk(){}


    @Test
    void whenGetProjectById_thenReturnsJson() throws Exception {
        Project project = new Project("testName", LocalDate.now());
        Mockito.when(projectService.findById(1L)).thenReturn(Optional.of(project));

        mockMvc.perform(get("/projects/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testName"));  // No casting needed with correct import
    }
}
