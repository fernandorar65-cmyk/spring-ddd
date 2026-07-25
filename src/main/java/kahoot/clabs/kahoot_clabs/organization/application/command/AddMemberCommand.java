package kahoot.clabs.kahoot_clabs.organization.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;

public record AddMemberCommand(
        @NotNull UUID userId,
        @NotNull RoleType roleType
) {
}
