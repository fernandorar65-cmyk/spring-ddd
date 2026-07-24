package kahoot.clabs.kahoot_clabs.users.application.dto;

import java.util.UUID;

public record AuthUserResponse(
        UUID userId,
        UUID organizationId,
        String email,
        String firstName,
        String lastName
) {
}
