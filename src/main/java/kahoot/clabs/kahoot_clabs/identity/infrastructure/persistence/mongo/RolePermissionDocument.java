package kahoot.clabs.kahoot_clabs.identity.infrastructure.persistence.mongo;

import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "role_permissions")
@CompoundIndex(name = "uq_role_permission", def = "{'roleId': 1, 'permissionId': 1}", unique = true)
@Getter
@Setter
@NoArgsConstructor
public class RolePermissionDocument {

    @Id
    private String id;

    @Indexed
    private UUID roleId;

    @Indexed
    private UUID permissionId;

    public static String composeId(UUID roleId, UUID permissionId) {
        return roleId + ":" + permissionId;
    }
}
