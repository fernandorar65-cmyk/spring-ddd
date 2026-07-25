package kahoot.clabs.kahoot_clabs.organization.application.command;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Creates an organization and its first administrator in a single step.
 */
public record SignUpCommand(
        @NotBlank @Size(min = 2, max = 150) String organizationName,
        @NotBlank @Size(min = 2, max = 100) String organizationSlug,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 1, max = 80) String firstName,
        @NotBlank @Size(min = 1, max = 80) String lastName,
        @NotBlank @Size(min = 8, max = 100) String password
) {
}
