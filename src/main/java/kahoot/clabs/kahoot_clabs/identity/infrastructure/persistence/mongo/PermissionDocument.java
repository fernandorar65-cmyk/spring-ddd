package kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "permissions")
@CompoundIndex(name = "uq_permission_name_module", def = "{'name': 1, 'module': 1}", unique = true)
@Getter
@Setter
@NoArgsConstructor
public class PermissionDocument {

    @Id
    private UUID id;

    private String name;
    private String description;
    private String module;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
