package kahoot.clabs.kahoot_clabs.organization.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kahoot.clabs.kahoot_clabs.identity.domain.valueobject.RoleType;

public record InviteMemberCommand(
        @NotBlank @Email String email,
        @NotNull RoleType roleType
) {
}
