package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JacksonUnitTest {

    static final String EXPECTED_JSON = """
    {
      "code" : "C-001",
      "name" : "Campaign 1",
      "tasks" : [ {
        "code" : "T-001",
        "name" : "Task 1",
        "status" : "TO_DO"
      }, {
        "code" : "T-002",
        "name" : "Task 2",
        "status" : "IN_PROGRESS"
      } ]
    }""";

    @Test
    // Reading JSON with JsonParser
    void whenParsingTasksWithJsonParser_thenOnHoldTasksCountIsCorrect() throws IOException {
        int onHoldTasks = 0;
        URL resource = getClass().getClassLoader().getResource("tasks.json");

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser = jsonFactory.createParser(resource.openStream());

        if (jsonParser.nextToken() == JsonToken.START_ARRAY) {
            while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                TaskStatus taskStatus = null;
                while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                    String fieldName = jsonParser.currentName();
                    if ("status".equals(fieldName)) {
                        jsonParser.nextToken();
                        taskStatus = TaskStatus.valueOf(jsonParser.getString());
                    }
                }
                if (TaskStatus.ON_HOLD.equals(taskStatus)) {
                    onHoldTasks++;
                }
            }
        }
        jsonParser.close();

        assertEquals(250, onHoldTasks);
    }

    @Test
    void whenWritingCampaignWithTasksUsingJsonGenerator_thenExpectedJsonStructureIsWritten() throws IOException {
        File outputFile = new File("src/test/resources/campaign-with-tasks.json");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(new FileOutputStream(outputFile), JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeName("code");
        jsonGenerator.writeString("C-001");
        jsonGenerator.writeName("name");
        jsonGenerator.writeString("Campaign 1");
        jsonGenerator.writeName("tasks");
        jsonGenerator.writeStartArray();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeName("code");
        jsonGenerator.writeString("T-001");
        jsonGenerator.writeName("name");
        jsonGenerator.writeString("Task 1");
        jsonGenerator.writeName("status");
        jsonGenerator.writeString(TaskStatus.TO_DO.toString());
        jsonGenerator.writeEndObject();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeName("code");
        jsonGenerator.writeString("T-002");
        jsonGenerator.writeName("name");
        jsonGenerator.writeString("Task 2");
        jsonGenerator.writeName("status");
        jsonGenerator.writeString(TaskStatus.IN_PROGRESS.toString());
        jsonGenerator.writeEndObject();

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        String writtenJson = Files.readString(outputFile.toPath());
        assertEquals(objectMapper.readTree(EXPECTED_JSON), objectMapper.readTree(writtenJson));
    }
}