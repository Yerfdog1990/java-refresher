package com.baeldung.ls.client;

import com.baeldung.ls.web.dto.ProjectDto;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Random;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ProjectRestApiLiveTest {
    private static final String BASE_URL = "http://localhost:8080/projects/";
    private RestTemplate restTemplate = new RestTemplate();

    @Test
    public void givenProjectExists_whenGet_thenSuccess(){
        ResponseEntity<ProjectDto> response = restTemplate.getForEntity(BASE_URL + "1", ProjectDto.class);
        assertThat(response.getStatusCodeValue(), Matchers.equalTo(200));
        assertNotNull(response.getBody());
    }
    @Test
    public void givenNewProjectExists_whenCreate_thenSuccess(){
        ProjectDto newProject = new ProjectDto(new Random().nextLong(), RandomStringUtils.randomAlphabetic(6), LocalDate.now());
        ResponseEntity<Void> response = restTemplate.postForEntity(BASE_URL, newProject, Void.class);
        assertSame(HttpStatus.OK, response.getStatusCode());
    }
}
