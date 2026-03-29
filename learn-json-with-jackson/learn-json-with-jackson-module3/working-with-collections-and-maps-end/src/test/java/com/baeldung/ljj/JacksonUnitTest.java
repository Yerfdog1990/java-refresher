package com.baeldung.ljj;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.baeldung.ljj.domain.model.Task;
import com.baeldung.ljj.domain.model.TaskStatus;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

class JacksonUnitTest {

    final JsonMapper objectMapper = new JsonMapper();

    Task task1 = new Task("T1", "Task 1", "Description of Task 1", null, TaskStatus.TO_DO, null);
    Task task2 = new Task("T2", "Task 2", "Description of Task 2", null, TaskStatus.TO_DO, null);

    @Test
    void givenListOfTasks_whenSerializing_thenJsonArrayPreservesOrder() throws JacksonException {

        List<Task> tasks = List.of(task1, task2);
        String json = objectMapper.writeValueAsString(tasks);
        System.out.println(json);

        assertTrue(json.contains("\"code\":\"T1\""));
        assertTrue(json.contains("\"code\":\"T2\""));
        assertTrue(json.indexOf("\"code\":\"T1\"") < json.indexOf("\"code\":\"T2\""));
    }

    @Test
    void givenJsonTaskArray_whenDeserializingToListOfTasks_thenCorrect() throws Exception {

        String json = """
                [
                    {"code":"T1","name":"Task 1","description":"Description of Task 1","dueDate":null,"status":"TO_DO","campaign":null},
                    {"code":"T2","name":"Task 2","description":"Description of Task 2","dueDate":null,"status":"TO_DO","campaign":null}
                ]
                """;

        List<Task> tasks = objectMapper.readValue(json, new TypeReference<List<Task>>() {
        });

        assertTrue(tasks instanceof ArrayList);
        assertEquals(2, tasks.size());
        assertEquals("T1", tasks.get(0)
                .getCode());
        assertEquals("T2", tasks.get(1)
                .getCode());
    }

    @Test
    void givenSetOfTasks_whenSerializing_thenJsonArrayWithoutOrderGuarantee() throws Exception {

        Set<Task> tasks = new HashSet<>();
        tasks.add(task1);
        tasks.add(task2);
        String json = objectMapper.writeValueAsString(tasks);

        assertTrue(json.contains("\"code\":\"T1\""));
        assertTrue(json.contains("\"code\":\"T2\""));
    }

    @Test
    void givenJsonTaskArray_whenDeserializingToSet_thenOk() throws Exception {
        String json = """
                [
                    {"code":"T1","name":"Task 1","description":"Description of Task 1","dueDate":null,"status":"TO_DO","campaign":null},
                    {"code":"T2","name":"Task 2","description":"Description of Task 2","dueDate":null,"status":"TO_DO","campaign":null}
                ]
                """;

        Set<Task> tasks = objectMapper.readValue(json, new TypeReference<Set<Task>>() {
        });

        assertTrue(tasks instanceof HashSet);
        assertEquals(2, tasks.size());
        assertTrue(tasks.stream()
                .anyMatch(task -> "T1".equals(task.getCode())));
        assertTrue(tasks.stream()
                .anyMatch(task -> "T2".equals(task.getCode())));
    }

    @Test
    void givenMapOfTasks_whenSerializing_thenOk() throws Exception {
        Map<String, Task> byCode = new LinkedHashMap<>();
        byCode.put("T1", task1);
        byCode.put("T2", task2);

        String json = objectMapper.writeValueAsString(byCode);
        System.out.println(json);

        assertTrue(json.contains("\"T1\""));
        assertTrue(json.contains("\"T2\""));
    }

    @Test
    void givenJson_whenDeserializingToMap_thenKeysAndValuesAreRestored() throws Exception {
        String json = """
                {
                  "T1": { "code": "T1", "name": "Task 1", "description": "Task 1 description", "status": "TO_DO" },
                  "T2": { "code": "T2", "name": "Task 2", "description": "Task 2 description", "status": "TO_DO" }
                }
                """;

        Map<String, Task> tasks = objectMapper.readValue(json, new TypeReference<Map<String, Task>>() {
        });

        assertTrue(tasks instanceof LinkedHashMap);
        assertTrue(tasks.containsKey("T1"));
        assertTrue(tasks.containsKey("T2"));
        assertEquals("Task 2", tasks.get("T2")
                .getName());
    }

    @Test
    void givenMapOfLists_whenSerializing_thenNestedShapeIsHandled() throws Exception {
        Map<String, List<Task>> groups = new LinkedHashMap<>();
        groups.put("todo", List.of(task1, task2));
        groups.put("empty", Collections.emptyList());

        String json = objectMapper.writeValueAsString(groups);
        System.out.println(json);

        assertTrue(json.contains("\"todo\""));
        assertTrue(json.contains("\"empty\""));
    }

    @Test
    void givenMapOfLists_whenDeserializing_thenNestedShapeIsHandled() throws Exception {
        String json = """
                {
                    "todo": [
                      { "code": "T1", "name": "Task 1", "description": "Task 1 description", "status": "TO_DO" },
                      { "code": "T2", "name": "Task 2", "description": "Task 2 description", "status": "TO_DO" }
                    ],
                    "empty": []
                  }
                """;

        Map<String, List<Task>> restored = objectMapper.readValue(json, new TypeReference<Map<String, List<Task>>>() {
        });

        assertEquals(2, restored.get("todo")
                .size());
        assertTrue(restored.get("empty")
                .isEmpty());
    }

}