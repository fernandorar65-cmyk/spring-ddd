package kahoot.clabs.kahoot_clabs.organization;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
class OrganizationMembershipIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void signUpCreatesOrganizationWithOwnerAsMember() throws Exception {
        String signUpBody = """
                {
                  "organizationName": "Acme Corp",
                  "organizationSlug": "acme-corp",
                  "email": "admin@acme.test",
                  "firstName": "Ada",
                  "lastName": "Lovelace",
                  "password": "secret123"
                }
                """;

        MvcResult signUpResult = mockMvc.perform(post("/api/v1/organizations/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.organizationId").isNotEmpty())
                .andExpect(jsonPath("$.organizationSlug").value("acme-corp"))
                .andExpect(jsonPath("$.email").value("admin@acme.test"))
                .andReturn();

        JsonNode signUp = objectMapper.readTree(signUpResult.getResponse().getContentAsString());
        String organizationId = signUp.get("organizationId").asText();
        String ownerId = signUp.get("userId").asText();

        mockMvc.perform(get("/api/v1/organizations/" + organizationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("acme-corp"))
                .andExpect(jsonPath("$.members.length()").value(1))
                .andExpect(jsonPath("$.members[0].userId").value(ownerId))
                .andExpect(jsonPath("$.members[0].status").value("ACTIVE"));

        mockMvc.perform(get("/api/v1/users/" + ownerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleId").isNotEmpty());
    }

    @Test
    void invitesAndRemovesMembers() throws Exception {
        String signUpBody = """
                {
                  "organizationName": "Globex",
                  "organizationSlug": "globex",
                  "email": "owner@globex.test",
                  "firstName": "Hedy",
                  "lastName": "Lamarr",
                  "password": "secret123"
                }
                """;

        MvcResult signUpResult = mockMvc.perform(post("/api/v1/organizations/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(signUpBody))
                .andExpect(status().isCreated())
                .andReturn();
        String organizationId = objectMapper.readTree(signUpResult.getResponse().getContentAsString())
                .get("organizationId").asText();

        String registerBody = """
                {
                  "email": "member@globex.test",
                  "firstName": "Alan",
                  "lastName": "Turing",
                  "password": "secret123"
                }
                """;
        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andReturn();
        String memberUserId = objectMapper.readTree(registerResult.getResponse().getContentAsString())
                .get("userId").asText();

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
                .andExpect(jsonPath("$.members.length()").value(2));

        mockMvc.perform(delete("/api/v1/organizations/" + organizationId + "/members/" + memberUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.members.length()").value(1));
    }
}
