package kahoot.clabs.kahoot_clabs.identity.application.readmodel;

import java.time.LocalDateTime;
import java.util.UUID;

public record PermissionReadModel(
        UUID id,
        String name,
        String description,
        String module,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
