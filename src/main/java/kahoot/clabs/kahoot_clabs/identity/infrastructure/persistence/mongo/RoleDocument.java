package kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "roles")
@Getter
@Setter
@NoArgsConstructor
public class RoleDocument {

    @Id
    private UUID id;

    private String name;

    @Indexed(unique = true)
    private String type;

    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
