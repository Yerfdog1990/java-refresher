package restapi.springhateoasrest.web.controller;


import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import restapi.springhateoasrest.web.dto.AccountDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class AccountApiTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    public void testCrudOperations() throws Exception {
        AccountDto dto = new AccountDto();
        dto.setAccountNumber("1234567890");
        dto.setBalance(1000.0f);

        // Create
        String json = mockMvc.perform(post("/api/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andReturn().getResponse().getContentAsString();

        AccountDto createdDto = objectMapper.readValue(json, AccountDto.class);
        Integer id = createdDto.getId();

        // Read One
        mockMvc.perform(get("/api/accounts/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"));

        // Read All
        mockMvc.perform(get("/api/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        // Update
        createdDto.setBalance(2000.0f);
        mockMvc.perform(put("/api/accounts/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(2000.0f));

        // Delete
        mockMvc.perform(delete("/api/accounts/" + id))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/api/accounts/" + id))
                .andExpect(status().isNotFound());
    }
}
