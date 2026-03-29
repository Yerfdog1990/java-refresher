package com.baeldung.ljj;

import com.baeldung.ljj.domain.model.FreelanceWorker;
import com.baeldung.ljj.domain.model.FullTimeWorker;
import com.baeldung.ljj.domain.model.Team;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.DatabindException;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PolymorphicTypesUnitTest {
    final JsonMapper jsonMapper = new JsonMapper();

    @Test
    void givenTeamWithMixedWorkers_whenSerializing_thenSuccessSerialization() throws Exception {
        FullTimeWorker fullTimeWorker = new FullTimeWorker(1, "John Doe", 5000.0);
        FreelanceWorker freelanceWorker = new FreelanceWorker(2, "Jane Smith", 50.0);

        Team team = new Team("Baeldung", List.of(fullTimeWorker, freelanceWorker));
        String json = jsonMapper.writeValueAsString(team);
        System.out.println(json);
    }

    @Test
    void givenJsonWithoutTypeInfo_whenDeserializingTeam_thenFails() {
        String json = "{\"name\":\"Baeldung\",\"members\":[{\"id\":1,\"name\":\"John Doe\",\"monthlySalary\":5000.0}]}";

        assertThrows(DatabindException.class, () -> {
            jsonMapper.readValue(json, Team.class);
        });
    }

    @Test
    void givenJsonWithTypeInfo_whenDeserializingTeam_thenSucceed() throws Exception {
        String json = "{\"name\":\"Baeldung\",\"members\":[{\"@type\":\"fulltime\",\"id\":1,\"name\":\"John Doe\",\"monthlySalary\":5000.0}," +
                "{\"@type\":\"freelance\",\"id\":2,\"name\":\"Jane Smith\",\"hourlyRate\":50.0}]}\n";

        Team deserialized = jsonMapper.readValue(json, Team.class);
        assertInstanceOf(FullTimeWorker.class, deserialized.getMembers().get(0));
        assertInstanceOf(FreelanceWorker.class, deserialized.getMembers().get(1));
    }
}