package kahoot.clabs.kahoot_clabs.gameplay.application.command;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JoinGameSessionCommand(
        UUID userId,
        @NotBlank @Size(max = 30) String nickname
) {
}
