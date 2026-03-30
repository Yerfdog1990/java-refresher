package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.TaskStatus;
import tools.jackson.core.JsonEncoding;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.json.JsonFactory;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JacksonUnitTest {

    private static final String EXPECTED_JSON = """
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
    void whenParsingTasksWithJsonParser_thenOnHoldTasksCountIsCorrect() throws IOException {
        int onHoldTasks = 0;
        InputStream resource = getClass().getClassLoader().getResourceAsStream("tasks.json");

        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser = jsonFactory.createParser(ObjectReadContext.empty(), resource);

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
        JsonMapper jsonMapper = new JsonMapper();
        JsonGenerator jsonGenerator = jsonMapper
                .writer()
                .withDefaultPrettyPrinter()
                .createGenerator(outputFile, JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringProperty("code", "C-001");
        jsonGenerator.writeStringProperty("name", "Campaign 1");
        jsonGenerator.writeName("tasks");
        jsonGenerator.writeStartArray();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringProperty("code", "T-001");
        jsonGenerator.writeStringProperty("name", "Task 1");
        jsonGenerator.writePOJOProperty("status", TaskStatus.TO_DO);
        jsonGenerator.writeEndObject();

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringProperty("code", "T-002");
        jsonGenerator.writeStringProperty("name", "Task 2");
        jsonGenerator.writePOJOProperty("status", TaskStatus.IN_PROGRESS);
        jsonGenerator.writeEndObject();

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.close();

        String writtenJson = Files.readString(outputFile.toPath());
        assertEquals(
                jsonMapper.readTree(EXPECTED_JSON),
                jsonMapper.readTree(writtenJson));
    }

}
