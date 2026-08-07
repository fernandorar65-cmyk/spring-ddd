package kahoot.clabs.kahoot_clabs.organization.application.readmodel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Read model for organization queries (Mongo). Independent from the domain aggregate.
 */
public record OrganizationReadModel(
        UUID id,
        String name,
        String slug,
        String description,
        String logo,
        String timezone,
        String language,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<MemberReadModel> members) {

    public record MemberReadModel(
            UUID id,
            UUID userId,
            UUID roleId,
            String status,
            LocalDateTime joinedAt) {
    }
}
