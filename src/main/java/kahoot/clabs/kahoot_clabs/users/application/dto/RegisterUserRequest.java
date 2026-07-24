package kahoot.clabs.kahoot_clabs.users.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank @Size(min = 2, max = 150) String organizationName,
        @NotBlank @Size(min = 2, max = 100) String organizationSlug,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 1, max = 80) String firstName,
        @NotBlank @Size(min = 1, max = 80) String lastName,
        @NotBlank @Size(min = 8, max = 100) String password
) {
}
