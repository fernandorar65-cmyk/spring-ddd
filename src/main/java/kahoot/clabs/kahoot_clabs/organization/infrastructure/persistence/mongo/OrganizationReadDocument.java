package kahoot.clabs.kahoot_clabs.organization.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "organization_read_models")
@Getter
@Setter
@NoArgsConstructor
public class OrganizationReadDocument {

    @Id
    private UUID id;

    private String name;

    @Indexed(unique = true)
    private String slug;

    private String description;
    private String logo;
    private String timezone;
    private String language;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<MemberEmbedded> members = new ArrayList<>();

    @Getter
    @Setter
    @NoArgsConstructor
    public static class MemberEmbedded {
        private UUID id;
        private UUID userId;
        private UUID roleId;
        private String status;
        private LocalDateTime joinedAt;
    }
}
