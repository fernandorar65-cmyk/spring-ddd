package kahoot.clabs.kahoot_clabs.identity.application.readmodel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record RoleReadModel(
        UUID id,
        String name,
        String type,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<UUID> permissionIds) {

    public RoleReadModel {
        permissionIds = permissionIds == null ? List.of() : List.copyOf(permissionIds);
    }
}
