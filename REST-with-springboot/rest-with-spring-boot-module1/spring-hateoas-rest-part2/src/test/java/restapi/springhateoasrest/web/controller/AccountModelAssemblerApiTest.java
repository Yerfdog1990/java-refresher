package restapi.springhateoasrest.web.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import restapi.springhateoasrest.web.dto.AccountDto;
import tools.jackson.databind.ObjectMapper;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SpringBootTest
public class AccountModelAssemblerApiTest {
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
    public void testCrudOperationsWithHateoas() throws Exception {
        AccountDto dto = new AccountDto();
        dto.setAccountNumber("0987654321");
        dto.setBalance(5000.0f);

        // Create
        String json = mockMvc.perform(post("/api/accounts/model-assembler")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/accounts/model-assembler/")))
                .andExpect(jsonPath("$.accountNumber").value("0987654321"))
                .andExpect(jsonPath("$._links.self.href").exists())
                .andReturn().getResponse().getContentAsString();

        AccountDto createdDto = objectMapper.readValue(json, AccountDto.class);
        Integer id = createdDto.getId();

        // Read One
        mockMvc.perform(get("/api/accounts/model-assembler/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$._links.self.href", containsString("/api/accounts/model-assembler/" + id)))
                .andExpect(jsonPath("$._links.collection.href", containsString("/api/accounts/model-assembler")))
                .andExpect(jsonPath("$._links.Deposit.href", containsString("/api/accounts/model-assembler/" + id + "/deposits")))
                .andExpect(jsonPath("$._links.Withdrawal.href", containsString("/api/accounts/model-assembler/" + id + "/withdrawals")));

        // Read All
        mockMvc.perform(get("/api/accounts/model-assembler"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.accountDtoList").isArray())
                .andExpect(jsonPath("$._links.self.href", containsString("/api/accounts/model-assembler")));

        // Update
        createdDto.setBalance(6000.0f);
        mockMvc.perform(put("/api/accounts/model-assembler/" + id)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createdDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$.balance").value(6000.0f));
    }
}
