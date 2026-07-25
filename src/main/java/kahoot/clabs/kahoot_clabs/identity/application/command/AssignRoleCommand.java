package kahoot.clabs.kahoot_clabs.identity.application.command;

import jakarta.validation.constraints.NotNull;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;

public record AssignRoleCommand(
        @NotNull RoleType roleType
) {
}
