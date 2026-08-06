package kahoot.clabs.kahoot_clabs.gameplay.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateNicknameCommand(
        @NotNull UUID userId,
        @NotBlank @Size(max = 30) String nickname
) {
}
