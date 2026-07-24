package kahoot.clabs.kahoot_clabs.users.application.dto;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        UUID organizationId,
        UUID roleId,
        String email,
        String firstName,
        String lastName,
        String status
) {
}
