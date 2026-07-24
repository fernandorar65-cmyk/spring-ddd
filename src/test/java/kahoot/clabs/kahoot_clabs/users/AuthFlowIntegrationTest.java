package kahoot.clabs.kahoot_clabs.users;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerLoginAndGetProfile() throws Exception {
        String registerBody = """
                {
                  "organizationName": "Acme Corp",
                  "organizationSlug": "acme-corp",
                  "email": "admin@acme.test",
                  "firstName": "Ada",
                  "lastName": "Lovelace",
                  "password": "secret123"
                }
                """;

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNotEmpty())
                .andExpect(jsonPath("$.email").value("admin@acme.test"))
                .andReturn();

        JsonNode registerJson = objectMapper.readTree(registerResult.getResponse().getContentAsString());
        String userId = registerJson.get("userId").asText();

        mockMvc.perform(get("/api/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@acme.test"))
                .andExpect(jsonPath("$.firstName").value("Ada"));

        String loginBody = """
                {
                  "email": "admin@acme.test",
                  "password": "secret123"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("admin@acme.test"));
    }
}
