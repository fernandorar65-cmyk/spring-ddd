package kahoot.clabs.kahoot_clabs.identity.application.command;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

public record UpdateProfileCommand(
        @Size(max = 100) String department,
        @Size(max = 100) String jobTitle,
        @Size(max = 30) String phoneNumber,
        LocalDate birthDate,
        String bio,
        @Size(max = 150) String location
) {
}
