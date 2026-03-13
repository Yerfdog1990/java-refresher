package com.baeldung.rwsb.simplified;

import com.baeldung.rwsb.commons.contract.SimpleContractWebTestClient;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static com.baeldung.rwsb.commons.contract.SimpleRequestBodyBuilder.fromResource;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContractTaskSimplifiedApiIntegrationTest {

    @Autowired
    SimpleContractWebTestClient webClient;

    @Value("classpath:task.json")
    Resource taskResource;

    @Test
    void givenPreloadedData_whenCreateTasks_thenSuccess() throws IOException {
        webClient.create("/tasks", generateTaskJson());
    }

    private String generateTaskJson() throws IOException {
        Reader reader = new InputStreamReader(taskResource.getInputStream(), StandardCharsets.UTF_8);
        return FileCopyUtils.copyToString(reader);
    }

    @Test
    void whenCreateNewTask_thenSuccessWithExpectedFields() throws Exception {
        String taskJson = generateTaskJson();

        webClient.create("/tasks", taskJson)
                .containsFields("id", "name", "campaignId", "dueDate")
                .fieldsMatch(
                        Map.entry("name", CoreMatchers.equalTo("Test - New Task 1")),
                        Map.entry("campaignId", Matchers.greaterThan(0)),
                        Map.entry("dueDate", equalTo("2050-12-30")));
    }

    @Test
    void whenCreateNewTaskWithBlankName_thenBadRequest() throws Exception {
        String taskJson = fromResource(this.taskResource)
                .with("name", "")
                .build();

        webClient.requestWithResponseStatus("/tasks", HttpMethod.POST, taskJson, HttpStatus.BAD_REQUEST);
    }


}
