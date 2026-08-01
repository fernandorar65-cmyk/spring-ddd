package kahoot.clabs.kahoot_clabs.organization;

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

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrganizationMembershipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createsOrganizationAndInvitesExistingUser() throws Exception {
        String createBody = """
                {
                  "name": "Globex",
                  "slug": "globex"
                }
                """;

        MvcResult createResult = mockMvc.perform(post("/api/v1/organizations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.slug").value("globex"))
                .andExpect(jsonPath("$.data.members.length()").value(0))
                .andReturn();

        String organizationId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .path("data")
                .path("id")
                .asText();

        String registerBody = """
                {
                  "email": "member@globex.test",
                  "firstName": "Alan",
                  "lastName": "Turing",
                  "password": "secret123"
                }
                """;
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated());

        String inviteBody = """
                {
                  "email": "member@globex.test",
                  "roleType": "COMMON_MEMBER"
                }
                """;
        mockMvc.perform(post("/api/v1/organizations/" + organizationId + "/invitations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(inviteBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.members.length()").value(1))
                .andExpect(jsonPath("$.data.members[0].status").value("INVITED"));

        mockMvc.perform(get("/api/v1/organizations/" + organizationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.slug").value("globex"))
                .andExpect(jsonPath("$.data.members.length()").value(1));
    }
}
