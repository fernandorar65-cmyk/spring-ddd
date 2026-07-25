package kahoot.clabs.kahoot_clabs.organization.application.dto;

import java.util.UUID;

public record SignUpResponse(
        UUID organizationId,
        String organizationSlug,
        UUID userId,
        String email,
        String firstName,
        String lastName
) {
}
